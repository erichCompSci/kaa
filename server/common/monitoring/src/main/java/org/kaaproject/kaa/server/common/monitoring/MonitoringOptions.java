package org.kaaproject.kaa.server.common.monitoring;

public class MonitoringOptions {

    private static final float DEFAULT_MIN_FREE_MEMORY_IN_PERCENTAGE = 5f / 100f;
    private static final float DEFAULT_MAX_LOAD_AVERAGE = 20f;
    private static final int DEFAULT_MAX_PENDING_TASK_COUNT = 1000000;

    private Float minFreeMemoryInPercentage;
    private Float maxLoadAverage;
    private Integer maxPendingTaskCount;


    public Float getMinFreeMemoryInPercentage() {
        if (minFreeMemoryInPercentage == null) {
            minFreeMemoryInPercentage = DEFAULT_MIN_FREE_MEMORY_IN_PERCENTAGE;
        }
        return minFreeMemoryInPercentage;
    }

    public Float getMaxLoadAverage() {
        if (maxLoadAverage == null) {
            maxLoadAverage = DEFAULT_MAX_LOAD_AVERAGE;
        }
        return maxLoadAverage;
    }

    public Integer getMaxPendingTaskCount() {
        if (maxPendingTaskCount == null) {
            maxPendingTaskCount = DEFAULT_MAX_PENDING_TASK_COUNT;
        }
        return maxPendingTaskCount;
    }
}
