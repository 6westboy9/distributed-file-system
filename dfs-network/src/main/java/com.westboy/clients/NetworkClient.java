package com.westboy.clients;

import com.westboy.common.ZhangSelector;
import com.westboy.Node;
import com.westboy.Selectable;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
public class NetworkClient implements ZhangClient {

    private final Selectable selector;
    private final int socketSendBuffer;
    private final int socketReceiveBuffer;

    // 状态管理器
    private final ConnectionStateManager connectionStateManager;

    public NetworkClient(long reconnectBackoffMs, int socketSendBuffer, int socketReceiveBuffer) throws IOException {
        this.selector = new ZhangSelector();
        this.connectionStateManager = new ConnectionStateManager(reconnectBackoffMs);
        this.socketSendBuffer = socketSendBuffer;
        this.socketReceiveBuffer = socketReceiveBuffer;
    }

    @Override
    public boolean isReady(Node node, long now) {
        return false;
    }

    @Override
    public boolean ready(Node node, long now) {
        if (node.isEmpty()) {
            throw new IllegalArgumentException("Cannot connect to empty node " + node);
        }

        if (isReady(node, now)) {
            return true;
        }

        if (connectionStateManager.canConnect(node.idStr(), now)) {
            initiateConnect(node, now);
        }
        return false;
    }

    private void initiateConnect(Node node, long now) {
        String nodeId = node.idStr();
        try {
            connectionStateManager.connecting(nodeId, now);
            selector.connect(nodeId, node.host(), node.port(), socketSendBuffer, socketReceiveBuffer);
        } catch (IOException e) {
            connectionStateManager.disconnected(nodeId, now);
            log.error("Error connecting to node {}", node, e);
        }
    }

    @Override
    public void send(ClientRequest request) {

    }

    @Override
    public List<ClientResponse> poll(long timeout) {
        return null;
    }

    @Override
    public void disconnect(String nodeId) {

    }

    @Override
    public void close(String nodeId) {

    }

    // @Override
    // public ClientRequest newClientRequest(String nodeId, AbstractRequest.Builder<?> builder, boolean expectedResponse) {
    //     return null;
    // }
    //
    // @Override
    // public ClientRequest newClientRequest(String nodeId, AbstractRequest.Builder<?> builder, boolean expectedResponse, RequestCompletionHandler callback) {
    //     return null;
    // }
}
