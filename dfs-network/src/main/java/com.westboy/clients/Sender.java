package com.westboy.clients;

import com.westboy.Node;

import java.util.concurrent.atomic.AtomicBoolean;

public class Sender extends Thread {

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    private NetworkClient client;
    private Accumulator accumulator;

    public Sender(NetworkClient client, Accumulator accumulator) {
        this.client = client;
        this.accumulator = accumulator;
    }

    @Override
    public void run() {
        if (isRunning.get()) {
            long now = System.currentTimeMillis();
            sendRequest(now);
        }
    }

    public void sendRequest(long now) {
        Node node = null;
        client.ready(node, now);


    }

}
