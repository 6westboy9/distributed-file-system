package com.westboy.common.requests;

import java.net.InetAddress;

public class RequestContext {

    private final RequestHeader header;
    private final String connectionId;
    private final InetAddress clientAddress;

    public RequestContext(RequestHeader header, String connectionId, InetAddress clientAddress) {
        this.header = header;
        this.connectionId = connectionId;
        this.clientAddress = clientAddress;
    }
}
