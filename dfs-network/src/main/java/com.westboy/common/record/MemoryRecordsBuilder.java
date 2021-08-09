package com.westboy.common.record;

import com.westboy.clients.FileEntity;
import com.westboy.common.utils.ByteBufferOutputStream;
import com.westboy.common.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;

public class MemoryRecordsBuilder {

    private final DataOutputStream appendStream;
    private final ByteBufferOutputStream bufferStream;

    public MemoryRecordsBuilder(ByteBufferOutputStream bufferStream) {
        this.bufferStream = bufferStream;
        this.appendStream = new DataOutputStream(bufferStream);
    }

    private MemoryRecordsBuilder recordsBuilder(ByteBufferOutputStream bufferStream) {
        return new MemoryRecordsBuilder(bufferStream);
    }

    public void append(FileEntity fileEntity) throws IOException {
        DefaultRecord.writeTo(appendStream, Utils.wrap(fileEntity.getFileBytes()), Utils.wrap(fileEntity.getPathBytes()));
    }
}
