package com.westboy.datanode.network.newversion;

import cn.hutool.core.util.ObjectUtil;

import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkRequestQueue {

    private static volatile NetworkRequestQueue instance = null;

    public static NetworkRequestQueue get() {
        if(ObjectUtil.isNull(instance)) {
            synchronized(NetworkRequestQueue.class) {
                if(ObjectUtil.isNull(instance)) {
                    instance = new NetworkRequestQueue();
                }
            }
        }
        return instance;
    }

    private final ConcurrentLinkedQueue<NetworkRequest> requestQueue = new ConcurrentLinkedQueue<>();

    public void offer(NetworkRequest request) {
        requestQueue.offer(request);
    }

    public NetworkRequest poll() {
        return requestQueue.poll();
    }
}
