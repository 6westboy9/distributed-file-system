package com.westboy;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;

public class ByteBufferSend implements Send {

    private final String destination;
    private final int size;
    private final ByteBuffer[] buffers;
    private int remaining;

    public ByteBufferSend(String destination, ByteBuffer[] buffers) {
        this.destination = destination;
        this.buffers = buffers;
        for (ByteBuffer buffer : buffers) {
            remaining += buffer.remaining();
        }
        this.size = remaining;
    }

    @Override
    public String destination() {
        return destination;
    }

    @Override
    public boolean completed() {
        return remaining <= 0;
    }

    @Override
    public long writeTo(GatheringByteChannel channel) throws IOException {
        long written = channel.write(buffers);
        if (written < 0) {
            throw new EOFException("Wrote negative bytes to channel. This shouldn't happen.");
        }

        remaining -= written;
        return written;
    }

    @Override
    public long size() {
        return size;
    }
}
