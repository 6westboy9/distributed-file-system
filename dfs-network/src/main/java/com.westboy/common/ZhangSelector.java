package com.westboy.common;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.westboy.Selectable;
import com.westboy.Send;
import com.westboy.servers.ZhangChannel;
import com.westboy.servers.NetworkReceive;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ZhangSelector implements Selectable {

    private final Selector selector;
    private final Map<String /* connectionId */, ZhangChannel> channels;
    // 连接已经件建立成功的 connectionId 集合
    private final List<String /* connectionId */> connected;
    // 暂存的每个客户端发送过来的数据
    private final Map<ZhangChannel, Deque<NetworkReceive>> stagedReceives;
    // 暂存所有客户端发送过来的数据
    private final List<NetworkReceive> completedReceives;

    public ZhangSelector() throws IOException {
        selector = Selector.open();
        channels = new ConcurrentHashMap<>();
        connected = new LinkedList<>();
        stagedReceives = new ConcurrentHashMap<>();
        completedReceives = new LinkedList<>();
    }

    public List<NetworkReceive> getCompletedReceives() {
        return completedReceives;
    }

    public ZhangChannel getChannel(String connectionId) {
        return channels.get(connectionId);
    }

    public void mute(String connectionId) {
        ZhangChannel channel = getChannel(connectionId);
        channel.mute();
    }

    public void register(String connectionId, SocketChannel socketChannel) throws IOException {
        ensureNotRegister(connectionId);
        registerChannel(connectionId, socketChannel, SelectionKey.OP_READ);
    }

    private void ensureNotRegister(String connectionId) {
        if (channels.containsKey(connectionId)) {
            throw new IllegalStateException("There is already a connection for id " + connectionId);
        }
    }

    private void registerChannel(String connectionId, SocketChannel socketChannel, int interestedOps) throws IOException {
        SelectionKey key = socketChannel.register(selector, interestedOps);
        ZhangChannel channel = new ZhangChannel(connectionId, key);
        key.attach(channel);
        channels.put(connectionId, channel);
    }

    @Override
    public void connect(String id, String host, int port, int socketSendBuffer, int socketReceiveBuffer) {

    }

    @Override
    public void send(Send send) {

    }

    public void poll(long timeout) throws IOException {
        int ready = selector.select(timeout);
        if (ready > 0) {
            Set<SelectionKey> keys = selector.selectedKeys();
            pollKeys(keys);
            keys.clear(); // 处理完要清楚（必须）
        }


        addToCompletedReceives();
    }

    @Override
    public void close() {

    }

    private void pollKeys(Set<SelectionKey> keys) {
        for (SelectionKey key : keys) {
            ZhangChannel channel = channel(key);
            try {
                if (key.isConnectable() && channel.finishConnect()) {
                    connected.add(channel.connectionId());
                }

                if (key.isReadable()) {
                    handleRead(channel);
                }

                if (key.isWritable()) {
                    handleWrite(channel);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private ZhangChannel channel(SelectionKey key) {
        return (ZhangChannel) key.attachment();
    }

    private void handleWrite(ZhangChannel channel) {

    }

    private void addToCompletedReceives() {
        if (CollUtil.isNotEmpty(stagedReceives)) {
            Iterator<Map.Entry<ZhangChannel, Deque<NetworkReceive>>> iterator = stagedReceives.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<ZhangChannel, Deque<NetworkReceive>> entry = iterator.next();
                Deque<NetworkReceive> deque = entry.getValue();

                NetworkReceive receive = deque.poll();
                if (ObjectUtil.isNotNull(receive)) {
                    completedReceives.add(receive);
                }

                if (deque.isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }


    /**
     * 处理读请求
     */
    private void handleRead(ZhangChannel channel) throws IOException {
        NetworkReceive receive;
        while (ObjectUtil.isNotNull(receive = channel.read())) {
            addToStagedReceives(channel, receive);
        }
    }

    private void addToStagedReceives(ZhangChannel channel, NetworkReceive receive) {
        Deque<NetworkReceive> deque = stagedReceives.getOrDefault(channel, new ArrayDeque<>());
        deque.add(receive);
        stagedReceives.put(channel, deque);
    }


}
