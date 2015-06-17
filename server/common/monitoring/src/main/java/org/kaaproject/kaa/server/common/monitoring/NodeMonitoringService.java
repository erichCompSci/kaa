/*
 * Copyright 2015 CyberVision, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kaaproject.kaa.server.common.monitoring;

import com.google.common.util.concurrent.AtomicDouble;
import com.sun.management.OperatingSystemMXBean;
import org.kaaproject.kaa.server.common.monitoring.gc.GCAlarmCallback;
import org.kaaproject.kaa.server.common.monitoring.gc.KaaGCInspector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.kaaproject.kaa.server.common.monitoring.NodeState.NORMAL;
import static org.kaaproject.kaa.server.common.monitoring.NodeState.OVERLOADED;

@Service
public class NodeMonitoringService implements MonitoringService {

    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(NodeMonitoringService.class);
    /**
     * The Constant MB.
     */
    private static final int MB = 1024 * 1024;

    private Runtime runtime = Runtime.getRuntime();
    private Map<String, MonitoringState> registered = new ConcurrentHashMap<>();
    private Map<String, NodeStateChangeCallback> callbackMap = new ConcurrentHashMap<>();
    private OperatingSystemMXBean systemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    private ScheduledExecutorService checkStateScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService recoveryStateScheduler = Executors.newScheduledThreadPool(1);

    private AtomicInteger freeMemory = new AtomicInteger();
    private AtomicInteger pendingTasks = new AtomicInteger();
    private AtomicDouble la = new AtomicDouble();

    private NodeState state;

    @Autowired
    private MonitoringOptions options;

    @Autowired
    private KaaGCInspector inspector;

    public NodeMonitoringService() {
        state = NORMAL;
    }

    @PostConstruct
    public void start() {
        LOG.info("Starting JVM monitoring service...");
        checkStateScheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                printStatistics();
            }
        }, options.getPrintStatisticsDelay(), options.getPrintStatisticPeriod(), TimeUnit.MILLISECONDS);
        inspector.addGCListener("Monitoring", new GCAlarmCallback() {
            @Override
            public void onGCAlarm(long gcDuration) {
                collectMonitoringData();
            }
        });
    }

    @PreDestroy
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


    private void printStatistics() {
        collectMonitoringData();
        if (options.isPrintResourceUsageInfo()) {
            for (Map.Entry<String, MonitoringState> entry : registered.entrySet()) {
                String name = entry.getKey();
                MonitoringState info = entry.getValue();
                int input = info.getInputTasksCount();
                int success = info.getSuccessTasksCount();
                int failure = info.getFailureTasksCount();
                if (input != 0 || success != 0 || failure != 0) {
                    LOG.info("{}: {}/{}/{} input/success/failure perf second", name, input, success, failure);
                }
            }
            LOG.info("Free memory: {}, pending tasks: {}, LA: {}", freeMemory.get(), pendingTasks.get(), la.get());
        }
    }

    @Override
    public void addNodeStateListener(String name, NodeStateChangeCallback callback) {
        callbackMap.put(name, callback);
        if (OVERLOADED == state) {
            callback.onStateChange(state);
        }
    }

    @Override
    public MonitoringState createMonitoringState(String name) {
        LOG.info("Create monitoring state with name {}", name);
        MonitoringState monInfo = new MonitoringState();
        registered.put(name, monInfo);
        return monInfo;
    }

    private void onChangeState() {
        LOG.info("Execute on changing state callbacks.");
        if (!callbackMap.isEmpty()) {
            for (Map.Entry<String, NodeStateChangeCallback> entry : callbackMap.entrySet()) {
                entry.getValue().onStateChange(state);
            }
        }
    }

    private void collectMonitoringData() {
        int free = toMB(runtime.freeMemory());
        freeMemory.set(free);
        pendingTasks.set(getPendingTaskCount());
        la.set(systemMXBean.getSystemLoadAverage());
        if (!OVERLOADED.equals(state)) {
            if (pendingTasks.get() >= options.getMaxPendingTaskCount()) {
                LOG.warn("Exceeded pending tasks limit. Going to overload state.");
                updateState(OVERLOADED);
            } else if (isLowFreeMemory()) {
                LOG.warn("JVM is close to out of memory error. Going to overload state.");
                updateState(OVERLOADED);
            } else if (!isNormalLA()) {
                LOG.warn("LA exceed limit. Going to overload state.");
                updateState(OVERLOADED);
            }
        }
    }

    private int toMB(long bytes) {
        return (int) (bytes / MB);
    }

    private void updateState(NodeState currentState) {
        if (OVERLOADED.equals(currentState) && !OVERLOADED.equals(state)) {
            recoveryStateScheduler.schedule(new RecoveryCheckRunner(), options.getRejectRequestPeriod(), TimeUnit.MILLISECONDS);
        }
        this.state = currentState;
        onChangeState();
    }

    private boolean isRecovered() {
        boolean result = false;
        boolean pending = getPendingTaskCount() <= options.getCalmPendingTaskCount();
        boolean normalLA = isNormalLA();
        boolean lowMemory = !isLowFreeMemory();
        if (pending && normalLA && lowMemory) {
            result = true;
            LOG.info("System going to normal state.");
        } else {
            LOG.info("System still didn't recovered: pending tasks: {}, low memory {}, high LA {}", !pending, !lowMemory, !normalLA);
        }
        return result;
    }

    private boolean isLowFreeMemory() {
        boolean result = false;
        int max = toMB(runtime.maxMemory());
        int total = toMB(runtime.totalMemory());
        int free = toMB(runtime.freeMemory());
        if ((max == total) || (max - total) < (max * 0.1f)) {
            if (((float) (free) / (float) (max)) < options.getMinFreeMemory()) {
                result = true;
            }
        }
        return result;
    }

    private boolean isNormalLA() {
        boolean result = false;
        if (la.get() < options.getMaxLoadAverage()) {
            result = true;
        }
        return result;
    }

    private int getPendingTaskCount() {
        int count = 0;
        for (Map.Entry<String, MonitoringState> entry : registered.entrySet()) {
            count += entry.getValue().getPendingTasksCount();
        }
        return count;
    }

    private class RecoveryCheckRunner implements Runnable {
        @Override
        public void run() {
            if (isRecovered()) {
                updateState(NodeState.NORMAL);
            } else {
                recoveryStateScheduler.schedule(new RecoveryCheckRunner(), options.getRejectRequestPeriod(), TimeUnit.MILLISECONDS);
            }
        }
    }
}