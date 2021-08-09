package com.westboy.servers;

public class RequestHandlerPool {

    private RequestChannel requestChannel;
    private int numThreads;
    private ZhangApi api;

    public RequestHandlerPool(RequestChannel requestChannel, int numThreads, ZhangApi api) {
        this.requestChannel = requestChannel;
        this.numThreads = numThreads;
        this.api = api;

        for (int i = 0; i < numThreads; i++) {
            RequestHandler handler = new RequestHandler(requestChannel, api);
            handler.setDaemon(true);
            handler.setName("request-handler-" + i);
            handler.start();
        }
    }

}
