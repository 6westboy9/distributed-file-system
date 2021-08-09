package com.westboy.servers;

import lombok.Data;

@Data
public class ZhangServerConfig {

    public String host = "localhost";
    public int port = 8888;

    // 每个 acceptor 最大连接建立连接数量
    private int maxConnectionsPerIp = 5;
    // 设置 processor 的线程数量
    private int numProcessorThreads = 4;

    // 请求
    private int maxQueuedRequests = 50;
    //
    private int totalProcessorThreads;
    //
    private int numIoThreads = 8;


    private int sendBufferSize = -1;
    private int receiveBufferSize = -1;


}
