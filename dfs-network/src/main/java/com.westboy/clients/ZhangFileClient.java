package com.westboy.clients;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

@Slf4j
public class ZhangFileClient implements FileClient {

    private Accumulator accumulator;
    private Sender sender;

    public ZhangFileClient(FileClientConfig config) {
        // try {
            accumulator = new Accumulator(config.getTotalMemory());
            // NetworkClient client = new NetworkClient(config.getReconnectBackoffMs(), config.getSocketSendBuffer(), config.getSocketReceiveBuffer());
            // sender = new Sender(client, accumulator);
            // sender.setName("producer-sender");
            // sender.start();
            // log.info("Producer started.");
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }

    @Override
    public Future<Result> upload(byte[] fileBytes, String remotePath) throws IOException {
        return upload(fileBytes, remotePath, null);
    }

    @Override
    public Future<Result> upload(byte[] fileBytes, String remotePath, Callback callback) throws IOException {
        byte[] pathBytes = remotePath.getBytes(StandardCharsets.UTF_8);
        FutureResultMetadata future = accumulator.tryAppend(new FileEntity(fileBytes, pathBytes), callback);
        return future;
    }

    @Override
    public Future<Result> download() {
        return null;
    }
}
