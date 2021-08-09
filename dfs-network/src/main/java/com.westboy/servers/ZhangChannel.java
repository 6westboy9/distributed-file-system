package com.westboy.servers;

import cn.hutool.core.util.ObjectUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Objects;

public class ZhangChannel {

    private final String connectionId;
    private final SelectionKey key;
    private final SocketChannel socketChannel;
    private NetworkReceive receive;

    public ZhangChannel(String connectionId, SelectionKey key) {
        this.connectionId = connectionId;
        this.key = key;
        this.socketChannel = (SocketChannel) key.channel();
    }

    public String connectionId() {
        return connectionId;
    }

    public InetAddress address() {
       return socketChannel.socket().getInetAddress();
    }

    public boolean finishConnect() throws IOException {
        // socketChannel 处于非阻塞模式时，那么如果连接过程尚未完成，则此方法将返回 false
        // socketChannel 处于阻塞模式时，则此方法将阻塞，直到连接完成或失败为止，并且将始终返回 true 或抛出 IOException 异常
        boolean connected = socketChannel.finishConnect();
        if (connected) {
            key.interestOps(key.interestOps() & ~SelectionKey.OP_CONNECT | SelectionKey.OP_READ);
        }
        return connected;
    }

    public void mute() {
        removeInterestOps(SelectionKey.OP_READ);
    }

    public void removeInterestOps(int ops) {
        key.interestOps(key.interestOps() & ~ops);
    }

    public NetworkReceive read() throws IOException {
        NetworkReceive result = null;
        if (ObjectUtil.isNull(receive)) {
            receive = new NetworkReceive(connectionId);
        }

        receive.readFromChannel(socketChannel);
        // 此处返回 false 时，说明发生了拆包，一次没有读取完成，直接返回 null，等待下次读取操作
        // 此处返回 true 时，说明至少兑取到了一个数据包，当发生粘包时，先将第一个数据包保存到 result 中，并返回，暂存到 stagedReceives 中，并清空 receive
        // 将后续粘在一起的数据，重新分配 receive，继续接着处理，处理完成，同样的逻辑，暂存到 stagedReceives 中
        if (receive.complete()) {
            receive.payloadRewind();
            result = receive;
            receive = null;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ZhangChannel that = (ZhangChannel) o;
        return Objects.equals(connectionId, that.connectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(connectionId);
    }

}
