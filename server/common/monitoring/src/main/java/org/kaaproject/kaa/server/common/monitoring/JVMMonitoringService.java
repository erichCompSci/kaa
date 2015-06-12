package org.kaaproject.kaa.server.common.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.kaaproject.kaa.server.common.monitoring.MachineStateType.OVERLOADED;

@Service
public class JVMMonitoringService {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JVMMonitoringService.class);
    /**
     * The Constant MB.
     */
    private static final int MB = 1024 * 1024;

    private Runtime runtime = Runtime.getRuntime();
    private Map<String, TaskStatisticInfo> registered = new ConcurrentHashMap<>();
    private List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    private OperatingSystemMXBean systemMXBean = ManagementFactory.getOperatingSystemMXBean();

    private ScheduledExecutorService checkStateScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService recoveryStateScheduler = Executors.newScheduledThreadPool(1);

    private long totalGCCount = 0;
    private long totalGCTime = 0;

    private MachineStateType state;

    @Autowired
    private MonitoringOptions options;

    public JVMMonitoringService() {
        state = MachineStateType.NORMAL;
    }

    @PostConstruct
    public void start() {
        LOG.info("Starting JVM monitoring service...");
        checkStateScheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                updateStatistics();
            }
        }, options.getPrintStatisticsDelay(), options.getPrintStatisticPeriod(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        LOG.info("Stop JVM monitoring service...");
        checkStateScheduler.shutdown();
        try {
            checkStateScheduler.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Got error during stop check state scheduler.", e);
        }
        recoveryStateScheduler.shutdown();
        try {
            recoveryStateScheduler.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("Got error during stop recovery scheduler.", e);
        }
    }


    private void updateStatistics() {
        calculate();
        if (options.isPrintResourceUsageInfo()) {
            for (Map.Entry<String, TaskStatisticInfo> entry : registered.entrySet()) {
                String name = entry.getKey();
                TaskStatisticInfo info = entry.getValue();
                int input = info.getInputTasksCount();
                int success = info.getSuccessTasksCount();
                int failure = info.getFailureTasksCount();
                if (input != 0 || success != 0 || failure != 0) {
                    LOG.info("{}: {}/{}/{} input/success/failure perf second", name, input, success, failure);
                }
            }
        }
    }

    public TaskStatisticInfo registerStatistics(String name, StateChangeCallback callback) {
        LOG.info("Register statistics with name {}", name);
        TaskStatisticInfo statistic = new TaskStatisticInfo(callback);
        registered.put(name, statistic);
        if (OVERLOADED == state) {
            callback.onStateChange(state);
        }
        return statistic;
    }

    private void onChangeState() {
        LOG.info("Execute on changing state callbacks.");
        if (!registered.isEmpty()) {
            for (Map.Entry<String, TaskStatisticInfo> entry : registered.entrySet()) {
                entry.getValue().getCallback().onStateChange(state);
            }
        }
    }

    private void calculate() {
        if (!gcMXBeans.isEmpty() && gcMXBeans.size() > 1) {
            long max = toMB(runtime.maxMemory());
            long total = toMB(runtime.totalMemory());
            long free = toMB(runtime.freeMemory());
            long used = total - free;
            LOG.debug("Memory: {free: {}, max: {},  total: {}, used: {}}, LA: {}", free, max, total, used, systemMXBean.getSystemLoadAverage());
            GarbageCollectorMXBean gcOldGen = gcMXBeans.get(1);
            long gcTimeDelta = gcOldGen.getCollectionTime() - totalGCTime;
            long gcCountDelta = gcOldGen.getCollectionCount() - totalGCCount;
            totalGCTime += gcTimeDelta;
            totalGCCount += gcCountDelta;
            if (gcCountDelta >= 1 || getPendingTaskCount() >= options.getMaxPendingTaskCount()) {
                if ((max == total) || (max - total) < (max * 0.1f)) {
                    if (((float) (free) / (float) (max)) < options.getMinFreeMemory()) {
                        if (gcCountDelta >= options.getFullGCCountDeltaLimit()) {
                            LOG.warn("JVM is close to out of memory error. Sending notifications...");
                            updateState(OVERLOADED);
                        }
                    }
                }
            }
        } else {
            LOG.warn("GC MX beans not found.");
        }
    }

    private long toMB(long bytes) {
        return bytes / MB;
    }

    private void updateState(MachineStateType currentState) {
        if (OVERLOADED.equals(currentState) && !OVERLOADED.equals(state)) {
            recoveryStateScheduler.schedule(new RecoveryCheckRunner(), options.getRejectRequestPeriod(), TimeUnit.MILLISECONDS);
        }
        this.state = currentState;
        onChangeState();
    }

    private boolean isRecovered() {
        boolean result = false;
        if (getPendingTaskCount() <= options.getMinPendingTaskCount()) {
            result = true;
        }
        return result;
    }

    private int getPendingTaskCount() {
        int count = 0;
        for (Map.Entry<String, TaskStatisticInfo> entry : registered.entrySet()) {
            count += entry.getValue().getPendingTasksCount();
        }
        return count;
    }

    private class RecoveryCheckRunner implements Runnable {
        @Override
        public void run() {
            if (isRecovered()) {
                updateState(MachineStateType.NORMAL);
            } else {
                recoveryStateScheduler.schedule(new RecoveryCheckRunner(), options.getRejectRequestPeriod(), TimeUnit.MILLISECONDS);
            }
        }
    }
}