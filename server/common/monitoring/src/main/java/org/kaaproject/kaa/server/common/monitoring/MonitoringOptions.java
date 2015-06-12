package org.kaaproject.kaa.server.common.monitoring;

import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.TimeUnit;

public class MonitoringOptions {

    /**
     * The Constant DEFAULT_MIN_FREE_MEMORY.
     */
    private static final float DEFAULT_MIN_FREE_MEMORY = 5f / 100f;
    /**
     * The Constant DEFAULT_MAX_LOAD_AVERAGE.
     */
    private static final float DEFAULT_MAX_LOAD_AVERAGE = 20.0f;
    /**
     * The Constant DEFAULT_MAX_PENDING_TASK_COUNT.
     */
    private static final int DEFAULT_MAX_PENDING_TASK_COUNT = 1000000;
    /**
     * The Constant DEFAULT_MAX_PENDING_TASK_COUNT.
     */
    private static final int DEFAULT_MIN_PENDING_TASK_COUNT = 10000;
    /**
     * The Constant DEFAULT_PRINT_STATISTIC_PERIOD.
     */
    private static final long DEFAULT_PRINT_STATISTIC_PERIOD = TimeUnit.SECONDS.toMillis(1);
    /**
     * The Constant DEFAULT_PRINT_STATISTIC_DELAY.
     */
    private static final long DEFAULT_PRINT_STATISTIC_DELAY = TimeUnit.SECONDS.toMillis(0);
    /**
     * The Constant DEFAULT_PRINT_STATISTIC_DELAY.
     */
    private static final long DEFAULT_REJECT_REQUEST_PERIOD = TimeUnit.SECONDS.toMillis(10);

    /**
     * The Constant DEFAULT_FULL_GC_COUNT_DELTA.
     */
    private static final long DEFAULT_FULL_GC_COUNT_DELTA_LIMIT = 1;

    @Value("${monit[min_free_memory]}")
    private Float minFreeMemory;
    @Value("${monit[max_load_average]}")
    private Float maxLoadAverage;
    @Value("${monit[max_pending_task_count]}")
    private Integer maxPendingTaskCount;
    @Value("${monit[min_pending_task_count]}")
    private Integer minPendingTaskCount;
    @Value("${monit[print_statistics_delay]}")
    private Long printStatisticsDelay;
    @Value("${monit[print_statistics_period]}")
    private Long printStatisticPeriod;
    @Value("${monit[reject_request_period]}")
    private Long rejectRequestPeriod;
    @Value("${monit[full_gc_count_delta_limit]}")
    private Long fullGCCountDeltaLimit;
    @Value("${monit[print_executing_task_info]}")
    private Boolean printExecutingTaskInfo;
    @Value("${monit[print_resource_usage_info]}")
    private Boolean printResourceUsageInfo;

    public Float getMinFreeMemory() {
        if (minFreeMemory == null) {
            minFreeMemory = DEFAULT_MIN_FREE_MEMORY;
        }
        return minFreeMemory;
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

    public Long getPrintStatisticsDelay() {
        if (printStatisticsDelay == null) {
            printStatisticsDelay = DEFAULT_PRINT_STATISTIC_DELAY;
        }
        return printStatisticsDelay;
    }

    public Long getPrintStatisticPeriod() {
        if (printStatisticPeriod == null) {
            printStatisticPeriod = DEFAULT_PRINT_STATISTIC_PERIOD;
        }
        return printStatisticPeriod;
    }

    public Long getRejectRequestPeriod() {
        if (rejectRequestPeriod == null) {
            rejectRequestPeriod = DEFAULT_REJECT_REQUEST_PERIOD;
        }
        return rejectRequestPeriod;
    }

    public Long getFullGCCountDeltaLimit() {
        if (fullGCCountDeltaLimit == null) {
            fullGCCountDeltaLimit = DEFAULT_FULL_GC_COUNT_DELTA_LIMIT;
        }
        return fullGCCountDeltaLimit;
    }

    public Integer getMinPendingTaskCount() {
        if (minPendingTaskCount == null) {
            minPendingTaskCount = DEFAULT_MIN_PENDING_TASK_COUNT;
        }
        return minPendingTaskCount;
    }

    public Boolean isPrintExecutingTaskInfo() {
        return printExecutingTaskInfo;
    }

    public Boolean isPrintResourceUsageInfo() {
        return printResourceUsageInfo;
    }
}
