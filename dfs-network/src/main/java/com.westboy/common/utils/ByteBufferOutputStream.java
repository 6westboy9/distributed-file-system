package com.westboy.common.utils;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class ByteBufferOutputStream extends OutputStream {

    private static final float REALLOCATION_FACTOR = 1.1f;

    private final int initialCapacity;
    private final int initialPosition;
    private ByteBuffer buffer;

    public ByteBufferOutputStream(ByteBuffer buffer) {
        this.buffer = buffer;
        this.initialPosition = buffer.position();
        this.initialCapacity = buffer.capacity();
    }

    public ByteBufferOutputStream(int initialCapacity) {
        this(initialCapacity, false);
    }

    public ByteBufferOutputStream(int initialCapacity, boolean directBuffer) {
        this(directBuffer ? ByteBuffer.allocateDirect(initialCapacity) : ByteBuffer.allocate(initialCapacity));
    }

    public void write(int b) {
        ensureRemaining(1);
        buffer.put((byte) b);
    }

    public void write(byte[] bytes, int off, int len) {
        ensureRemaining(len);
        buffer.put(bytes, off, len);
    }

    public void write(ByteBuffer sourceBuffer) {
        ensureRemaining(sourceBuffer.remaining());
        buffer.put(sourceBuffer);
    }

    public ByteBuffer buffer() {
        return buffer;
    }

    public int position() {
        return buffer.position();
    }

    public int remaining() {
        return buffer.remaining();
    }

    public int limit() {
        return buffer.limit();
    }

    public void position(int position) {
        ensureRemaining(position - buffer.position());
        buffer.position(position);
    }

    public int initialCapacity() {
        return initialCapacity;
    }

    public void ensureRemaining(int remainingBytesRequired) {
        if (remainingBytesRequired > buffer.remaining())
            expandBuffer(remainingBytesRequired);
    }

    private void expandBuffer(int remainingRequired) {
        int expandSize = Math.max((int) (buffer.limit() * REALLOCATION_FACTOR), buffer.position() + remainingRequired);
        ByteBuffer temp = ByteBuffer.allocate(expandSize);
        int limit = limit();
        buffer.flip();
        temp.put(buffer);
        buffer.limit(limit);
        // reset the old buffer's position so that the partial data in the new buffer cannot be mistakenly consumed
        // we should ideally only do this for the original buffer, but the additional complexity doesn't seem worth it
        buffer.position(initialPosition);
        buffer = temp;
    }
}
