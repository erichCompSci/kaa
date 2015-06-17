package org.kaaproject.kaa.server.operations.service.akka.messages.core.logs;

import org.kaaproject.kaa.server.common.log.shared.appender.LogDeliveryCallback;
import org.kaaproject.kaa.server.common.monitoring.MonitoringState;

public class MonitoringVoidCallback implements LogDeliveryCallback {

    private final MonitoringState monitoringState;
    private final int logCount;

    public MonitoringVoidCallback(MonitoringState monitoringState, int logCount) {
        this.logCount = logCount;
        this.monitoringState = monitoringState;
    }

    @Override
    public void onSuccess() {
        monitoringState.appendSuccessTaskCount(logCount);
    }

    @Override
    public void onRemoteError() {
        monitoringState.appendFailureTaskCount(logCount);
    }

    @Override
    public void onInternalError() {
        monitoringState.appendFailureTaskCount(logCount);
    }

    @Override
    public void onConnectionError() {
        monitoringState.appendFailureTaskCount(logCount);
    }
}
