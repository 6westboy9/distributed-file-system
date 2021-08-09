package com.westboy.client.network.newversion;

import com.westboy.common.entity.Node;
import lombok.Data;

import java.nio.ByteBuffer;

@Data
public class NetworkResponse {
    private String requestId;
    private String nodeId;
    private Node node;
    private ByteBuffer lengthBuffer;
    private ByteBuffer buffer;
    private Boolean error;
    private Boolean finished;
}
