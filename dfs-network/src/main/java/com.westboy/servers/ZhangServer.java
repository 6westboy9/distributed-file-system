package com.westboy.servers;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class ZhangServer {

    private ZhangServerConfig config;

    private SocketServer socketServer;
    private RequestHandlerPool requestHandlerPool;
    private ZhangApi zhangApi;

    public ZhangServer(ZhangServerConfig config) throws IOException {
        this.config = config;

    }

    public void startup() throws IOException {
        synchronized (ZhangServer.class) {
            socketServer = new SocketServer(config);
            socketServer.startup();
            zhangApi = new ZhangApi();
            requestHandlerPool = new RequestHandlerPool(socketServer.getRequestChannel(), config.getNumIoThreads(), zhangApi);
        }
    }

}
