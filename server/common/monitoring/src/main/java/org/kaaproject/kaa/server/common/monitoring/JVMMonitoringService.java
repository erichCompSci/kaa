package org.kaaproject.kaa.server.common.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class JVMMonitoringService {

    private static final int MB = 1024 * 1024;
    /**
     * The Constant DEFAULT_PRINT_STATISTIC_PERIOD.
     */
    private static final long DEFAULT_PRINT_STATISTIC_PERIOD = TimeUnit.SECONDS.toMillis(1);
    /**
     * The Constant DEFAULT_PRINT_STATISTIC_DELAY.
     */
    private static final long DEFAULT_PRINT_STATISTIC_DELAY = TimeUnit.SECONDS.toMillis(0);
    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(JVMMonitoringService.class);

    private static final JVMMonitoringService instance = new JVMMonitoringService();
    private static Map<String, TaskStatisticInfo> registered = new ConcurrentHashMap<>();
    private static Runtime runtime = Runtime.getRuntime();

    private ScheduledExecutorService checkStateScheduler = Executors.newScheduledThreadPool(1);
    private List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
    private static MachineStateType state = MachineStateType.NORMAL;

    public void start() {
        checkStateScheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                printStatistics();
            }
        }, DEFAULT_PRINT_STATISTIC_DELAY, DEFAULT_PRINT_STATISTIC_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        checkStateScheduler.shutdown();
        try {
            checkStateScheduler.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOG.error("", e);
        }
    }


    private void printStatistics() {
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

    public static JVMMonitoringService getInstance(String name) {
        return instance;
    }

    public static TaskStatisticInfo registerStatistics(String name, StateChangeCallback callback) {
        TaskStatisticInfo statistic = new TaskStatisticInfo(callback);
        registered.put(name, statistic);
        if (MachineStateType.OVERLOADED == state) {
            callback.onStateChange(state);
        }
        return statistic;
    }
}
