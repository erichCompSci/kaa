package org.kaaproject.kaa.server.operations.service.akka.messages.core.logs;

import akka.actor.ActorRef;
import org.kaaproject.kaa.server.common.monitoring.MonitoringState;

public class MonitoringSingleLogDeliveryCallback extends SingleLogDeliveryCallback {

    private final MonitoringState monitoringState;
    private final int logCount;


    /**
     * Instantiates a new actor log delivery callback.
     *
     * @param monitoringState
     * @param actor
     * @param requestId
     * @param logCount
     */
    public MonitoringSingleLogDeliveryCallback(MonitoringState monitoringState, ActorRef actor, int requestId, int logCount) {
        super(actor, requestId);
        this.monitoringState = monitoringState;
        this.logCount = logCount;
    }

    @Override
    public void onInternalError() {
        monitoringState.appendFailureTaskCount(logCount);
        super.onInternalError();
    }

    @Override
    public void onConnectionError() {
        monitoringState.appendFailureTaskCount(logCount);
        super.onConnectionError();
    }

    @Override
    public void onRemoteError() {
        monitoringState.appendFailureTaskCount(logCount);
        super.onRemoteError();
    }

    @Override
    public void onSuccess() {
        monitoringState.appendSuccessTaskCount(logCount);
        super.onSuccess();
    }
}
