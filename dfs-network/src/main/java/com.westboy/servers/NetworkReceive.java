package com.westboy.servers;

import cn.hutool.core.util.ObjectUtil;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NetworkReceive {

    private final ByteBuffer size;
    private ByteBuffer payload;
    private final String connectionId;
    private int requestedBufferSize = -1;

    public NetworkReceive(String connectionId) {
        this.size = ByteBuffer.allocate(4);
        this.connectionId = connectionId;
    }

    public String connectionId() {
        return connectionId;
    }

    public ByteBuffer payload() {
        return payload;
    }

    public void readFromChannel(SocketChannel channel) throws IOException {
        if (size.hasRemaining()) {
            int bytesRead = channel.read(size);
            if (bytesRead < 0) {
                throw new EOFException();
            }
            if (!size.hasRemaining()) {
                size.rewind();
                requestedBufferSize = size.getInt();
            }
        }

        if (ObjectUtil.isNull(payload) && requestedBufferSize != -1) {
            payload = ByteBuffer.allocate(requestedBufferSize);
        }

        if (ObjectUtil.isNotNull(payload)) {
            int bytesRead = channel.read(payload);
            if (bytesRead < 0) {
                throw new EOFException();
            }
        }
    }

    public boolean complete() {
        return !size.hasRemaining() && ObjectUtil.isNotNull(payload) && !payload.hasRemaining();
    }

    public void payloadRewind() {
        payload.rewind();
    }

}
