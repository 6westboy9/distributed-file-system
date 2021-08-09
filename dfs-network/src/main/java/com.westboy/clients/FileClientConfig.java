package com.westboy.clients;

import lombok.Getter;

@Getter
public class FileClientConfig {
    private final int queueSize = 0;
    private final int reconnectBackoffMs = 0;
    private final int socketSendBuffer = 0;
    private final int socketReceiveBuffer = 0;
    private final int totalMemory = 100 * 1024; // 分配 100MB
}
