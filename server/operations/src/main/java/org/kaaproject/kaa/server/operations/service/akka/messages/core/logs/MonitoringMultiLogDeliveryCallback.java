package org.kaaproject.kaa.server.operations.service.akka.messages.core.logs;

import akka.actor.ActorRef;
import org.kaaproject.kaa.server.common.monitoring.MonitoringState;

import java.util.concurrent.atomic.AtomicInteger;

public class MonitoringMultiLogDeliveryCallback extends MultiLogDeliveryCallback {

    private final MonitoringState monitoringState;
    private final int logCount;

    private final AtomicInteger errorCount;

    /**
     * Instantiates a new actor log delivery callback.
     *
     * @param actor          the actor
     * @param requestId      the request id
     * @param appendersCount
     */
    public MonitoringMultiLogDeliveryCallback(MonitoringState monitoringState, ActorRef actor, int requestId, int appendersCount, int logCount) {
        super(actor, requestId, appendersCount);
        this.monitoringState = monitoringState;
        this.logCount = logCount;
        errorCount = new AtomicInteger(1);
    }

    @Override
    public void onInternalError() {
        onErrorCallback();
        super.onInternalError();
    }

    @Override
    public void onConnectionError() {
        onErrorCallback();
        super.onConnectionError();
    }

    @Override
    public void onRemoteError() {
        onErrorCallback();
        super.onRemoteError();
    }

    @Override
    public void onSuccess() {
        if (appendersCount.decrementAndGet() == 0) {
            monitoringState.appendSuccessTaskCount(logCount);
            sendSuccessToEndpoint();
        }
    }

    private void onErrorCallback() {
        if (errorCount.decrementAndGet() == 0) {
            monitoringState.appendFailureTaskCount(logCount);
        }
    }
}
