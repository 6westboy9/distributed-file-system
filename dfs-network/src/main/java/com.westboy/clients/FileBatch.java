package com.westboy.clients;

import com.westboy.common.record.MemoryRecordsBuilder;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileBatch {

    private final MemoryRecordsBuilder recordsBuilder;
    private final List<Thunk> thunks;

    public FileBatch(MemoryRecordsBuilder recordsBuilder) {
        this.recordsBuilder = recordsBuilder;
        thunks = new LinkedList<>();
    }

    public FutureResultMetadata tryAppend(FileEntity fileEntity, Callback callback) {
        try {
            recordsBuilder.append(fileEntity);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Result result = new Result();
        FutureResultMetadata future = new FutureResultMetadata(result);
        thunks.add(new Thunk(callback, future));
        return future;
    }

    static class Thunk {
        final Callback callback;
        final FutureResultMetadata future;

        Thunk(Callback callback, FutureResultMetadata future) {
            this.callback = callback;
            this.future = future;
        }
    }
}
