package com.westboy.servers;

public class RequestHandler extends Thread {

    private final RequestChannel requestChannel;
    private final ZhangApi api;

    public RequestHandler(RequestChannel requestChannel, ZhangApi api) {
        this.requestChannel = requestChannel;
        this.api = api;
    }

    @Override
    public void run() {
        while (true) {
            try {
                RequestChannel.Request request = requestChannel.pollRequest(300);
                api.handle(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
