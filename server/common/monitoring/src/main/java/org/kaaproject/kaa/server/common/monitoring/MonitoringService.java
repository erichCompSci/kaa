package org.kaaproject.kaa.server.common.monitoring;

public interface MonitoringService {

    MonitoringInfo registerStatistics(String name, NodeStateChangeCallback callback);

}
