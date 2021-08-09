package com.westboy.datanode.network.newversion;

import cn.hutool.core.util.ObjectUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class NetworkResponseQueues {

    private static volatile NetworkResponseQueues instance = null;

    public static NetworkResponseQueues get() {
        if(ObjectUtil.isNull(instance)) {
            synchronized(NetworkResponseQueues.class) {
                if(ObjectUtil.isNull(instance)) {
                    instance = new NetworkResponseQueues();
                }
            }
        }
        return instance;
    }

    private final Map<Integer /* processorId */, ConcurrentLinkedQueue<NetworkResponse> /* responseQueue */> responseQueues = new HashMap<>();

    public void initResponseQueue(Integer processorId) {
        ConcurrentLinkedQueue<NetworkResponse> responseQueue = new ConcurrentLinkedQueue<>();
        responseQueues.put(processorId, responseQueue);
    }

    public void offer(Integer processorId, NetworkResponse response) {
        responseQueues.get(processorId).offer(response);
    }

    public NetworkResponse poll(Integer processorId) {
        return responseQueues.get(processorId).poll();
    }
}
