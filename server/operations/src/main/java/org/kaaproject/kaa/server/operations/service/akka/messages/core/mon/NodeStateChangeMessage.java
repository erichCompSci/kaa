package org.kaaproject.kaa.server.operations.service.akka.messages.core.mon;

import org.kaaproject.kaa.server.common.monitoring.NodeState;

public final class NodeStateChangeMessage {

    private NodeState nodeState;

    public NodeStateChangeMessage(NodeState nodeState) {
        this.nodeState = nodeState;
    }

    public NodeState getNodeState() {
        return nodeState;
    }
}
