package com.westboy.clients;

import com.westboy.Node;

import java.util.List;

public interface ZhangClient {

    boolean isReady(Node node, long now);

    boolean ready(Node node, long now);

    void send(ClientRequest request);

    List<ClientResponse> poll(long timeout);

    void disconnect(String nodeId);

    void close(String nodeId);

    // ClientRequest newClientRequest(String nodeId, AbstractRequest.Builder<?> builder, boolean expectedResponse);
    //
    // ClientRequest newClientRequest(String nodeId, AbstractRequest.Builder<?> builder, boolean expectedResponse, RequestCompletionHandler callback);

}
