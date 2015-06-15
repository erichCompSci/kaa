package org.kaaproject.kaa.server.common.monitoring.callback;

import com.google.common.util.concurrent.FutureCallback;
import org.kaaproject.kaa.server.common.log.shared.appender.LogDeliveryCallback;
import org.kaaproject.kaa.server.common.monitoring.MonitoringInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public final class CallbackHolder implements FutureCallback<Void> {

    private static final Logger LOG = LoggerFactory.getLogger(CallbackHolder.class);

    private final MonitoringInfo info;
    private final LogDeliveryCallback callback;
    private final int size;

    public CallbackHolder(LogDeliveryCallback callback, MonitoringInfo info, int size) {
        this.callback = callback;
        this.info = info;
        this.size = size;
        info.appendInputTaskCount(size);
    }

    @Override
    public void onSuccess(Void v) {
        info.appendSuccessTaskCount(size);
        callback.onSuccess();
    }

    @Override
    public void onFailure(Throwable t) {
        info.appendFailureTaskCount(size);
        LOG.warn("Failed to store record", t);
        if (t instanceof IOException) {
            callback.onConnectionError();
        } else {
            callback.onInternalError();
        }
    }
}