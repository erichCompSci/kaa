package org.kaaproject.kaa.server.common.monitoring;

import java.util.concurrent.atomic.AtomicInteger;

public class MonitoringInfo {

    private NodeStateChangeCallback callback;
    private AtomicInteger inputTasks = new AtomicInteger();
    private AtomicInteger successTasks = new AtomicInteger();
    private AtomicInteger failureTasks = new AtomicInteger();
    private AtomicInteger pendingTasks = new AtomicInteger();

    public MonitoringInfo(NodeStateChangeCallback callback) {
        this.callback = callback;
    }

    public void appendInputTaskCount(int count) {
        inputTasks.getAndAdd(count);
        pendingTasks.getAndAdd(count);
    }

    public void appendSuccessTaskCount(int count) {
        successTasks.getAndAdd(count);
        pendingTasks.set(pendingTasks.get() - successTasks.get());
    }

    public void appendFailureTaskCount(int count) {
        failureTasks.getAndAdd(count);
        pendingTasks.set(pendingTasks.get() - failureTasks.get());
    }

    public int getInputTasksCount() {
        return inputTasks.getAndSet(0);
    }

    public int getSuccessTasksCount() {
        return successTasks.getAndSet(0);
    }

    public int getFailureTasksCount() {
        return failureTasks.getAndSet(0);
    }

    public int getPendingTasksCount() {
        return pendingTasks.getAndSet(0);
    }

    public NodeStateChangeCallback getCallback() {
        return callback;
    }
}
