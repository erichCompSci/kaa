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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MonitoringOptions {

    @Value("#{monitoring[min_free_memory]}")
    private Float minFreeMemory;
    @Value("#{monitoring[max_load_average]}")
    private Float maxLoadAverage;
    @Value("#{monitoring[max_pending_task_count]}")
    private Integer maxPendingTaskCount;
    @Value("#{monitoring[calm_pending_task_count]}")
    private Integer calmPendingTaskCount;
    @Value("#{monitoring[print_statistics_delay]}")
    private Long printStatisticsDelay;
    @Value("#{monitoring[print_statistics_period]}")
    private Long printStatisticPeriod;
    @Value("#{monitoring[reject_request_period]}")
    private Long rejectRequestPeriod;
    @Value("#{monitoring[print_resource_usage_info]}")
    private Boolean printResourceUsageInfo;
    @Value("#{monitoring[gc_warn_duration_time_ms]}")
    private Long gcWarnDurationTime;

    public Float getMinFreeMemory() {
        return minFreeMemory;
    }

    public Float getMaxLoadAverage() {
        return maxLoadAverage;
    }

    public Integer getMaxPendingTaskCount() {
        return maxPendingTaskCount;
    }

    public Long getPrintStatisticsDelay() {
        return printStatisticsDelay;
    }

    public Long getPrintStatisticPeriod() {
        return printStatisticPeriod;
    }

    public Long getRejectRequestPeriod() {
        return rejectRequestPeriod;
    }

    public Integer getCalmPendingTaskCount() {
        return calmPendingTaskCount;
    }

    public Boolean isPrintResourceUsageInfo() {
        return printResourceUsageInfo;
    }

    public Long getGcWarnDurationTime() {
        return gcWarnDurationTime;
    }
}
