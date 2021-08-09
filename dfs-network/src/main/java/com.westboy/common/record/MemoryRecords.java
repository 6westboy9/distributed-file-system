package com.westboy.common.record;

import com.westboy.common.utils.ByteBufferOutputStream;

import java.nio.ByteBuffer;

public class MemoryRecords {

    private final ByteBuffer buffer;

    public MemoryRecords(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static MemoryRecordsBuilder builder(ByteBuffer buffer) {
        return new MemoryRecordsBuilder(new ByteBufferOutputStream(buffer));
    }

}
