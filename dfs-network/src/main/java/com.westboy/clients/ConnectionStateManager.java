package com.westboy.clients;

import cn.hutool.core.util.ObjectUtil;

import java.util.HashMap;
import java.util.Map;

public class ConnectionStateManager {

    private final long reconnectBackoffMs;
    private final Map<String, NodeConnectionState> nodeStates;

    public ConnectionStateManager(long reconnectBackoffMs) {
        this.reconnectBackoffMs = reconnectBackoffMs;
        this.nodeStates = new HashMap<>();
    }

    public boolean canConnect(String idStr, long now) {
        NodeConnectionState state = nodeStates.get(idStr);
        if (ObjectUtil.isNull(state)) {
            return true;
        } else {
            // 已经断开连接
            return state.state.isDisconnected() && (now - state.lastConnectAttemptMs) >= state.reconnectBackoffMs;
        }
    }

    public void connecting(String idStr, long now) {
        if (nodeStates.containsKey(idStr)) {
            NodeConnectionState nodeState = nodeStates.get(idStr);
            nodeState.lastConnectAttemptMs = now;
            nodeState.state = ConnectionState.CONNECTING;
        } else {
            nodeStates.put(idStr, new NodeConnectionState(ConnectionState.CONNECTING, now, this.reconnectBackoffMs));
        }
    }

    public void disconnected(String nodeId, long now) {
        NodeConnectionState nodeState = nodeState(nodeId);
        nodeState.state = ConnectionState.DISCONNECTED;
        nodeState.lastConnectAttemptMs = now;
        nodeState.failedAttempts += 1;
    }

    private NodeConnectionState nodeState(String id) {
        NodeConnectionState state = nodeStates.get(id);
        if (state == null) {
            throw new IllegalStateException("No entry found for connection " + id);
        }
        return state;
    }

    static class NodeConnectionState {
        private ConnectionState state;
        private long lastConnectAttemptMs;
        private long failedAttempts;
        private long reconnectBackoffMs;

        public NodeConnectionState(ConnectionState state, long lastConnectAttemptMs, long reconnectBackoffMs) {
            this.state = state;
            this.lastConnectAttemptMs = lastConnectAttemptMs;
            this.failedAttempts = 0;
            this.reconnectBackoffMs = reconnectBackoffMs;
        }
    }

    enum ConnectionState {
        DISCONNECTED, CONNECTING, READY;
        public boolean isDisconnected() {
            return this == DISCONNECTED;
        }
    }

}
