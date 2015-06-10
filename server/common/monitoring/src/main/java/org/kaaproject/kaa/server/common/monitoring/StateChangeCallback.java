package org.kaaproject.kaa.server.common.monitoring;

public interface StateChangeCallback {

    void onStateChange(MachineStateType stateType);

}
