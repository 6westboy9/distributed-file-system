package com.westboy.client.network.newversion;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.westboy.common.entity.Node;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 网络连接池子
 *
 * @author zhonghuashishan
 */
@Slf4j
public class NetworkManager {

    // 正在连接中
    public static final Integer CONNECTING = 1;
    // 已经建立连接
    public static final Integer CONNECTED = 2;
    // 断开连接
    public static final Integer DISCONNECTED = 3;
    // 响应状态：成功
    public static final Integer RESPONSE_SUCCESS = 1;
    // 响应状态：失败
    public static final Integer RESPONSE_FAILURE = 2;
    // 网络 poll 操作的超时时间
    public static final Long POLL_TIMEOUT = 500L;
    // 请求超时检测间隔
    public static final long REQUEST_TIMEOUT_CHECK_INTERVAL = 1000;
    // 请求超时时长
    public static final long REQUEST_TIMEOUT = 30 * 1000;

    // 多路复用Selector
    private Selector selector;
    // 所有的连接
    private final Map<String /* ip:port */, SelectionKey> connections;
    // 每个数据节点的连接状态
    private final Map<String /* ip:port */ , Integer> connectState;
    // 等待建立连接的机器
    private final ConcurrentLinkedQueue<Node> waitingConnectHosts;
    // 排队等待发送的网络请求
    private final Map<String /* ip:port */, ConcurrentLinkedQueue<NetworkRequest>> waitingRequests;
    // 马上准备要发送的网络请求
    private final Map<String /* ip:port */, NetworkRequest> toSendRequests;

    // 已经完成请求的响应（用于解决下载文件时阻塞等待响应结果）
    private final Map<String /* requestId */, NetworkResponse> finishedResponses;
    // 还没读取完毕的响应（用户解决读取下载文件响应结果粘包拆包问题）
    private final Map<String /* ip:port */ , NetworkResponse> unfinishedResponses;

    public NetworkManager() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.connections = new ConcurrentHashMap<>();
        this.connectState = new ConcurrentHashMap<>();
        this.waitingConnectHosts = new ConcurrentLinkedQueue<>();
        this.waitingRequests = new ConcurrentHashMap<>();
        this.toSendRequests = new ConcurrentHashMap<>();
        this.finishedResponses = new ConcurrentHashMap<>();
        this.unfinishedResponses = new ConcurrentHashMap<>();

        new NetworkPollThread().startup();
    }

    private String remoteNodeId(Node node) {
        return node.getIp() + ":" + node.getPort();
    }

    private String remoteNodeId(NetworkRequest request) {
        return request.getNode().getIp() + ":" + request.getNode().getPort();
    }

    private String remoteNodeId(InetSocketAddress address) {
        return address.getHostString() + ":" + address.getPort();
    }

    /**
     * 尝试连接到数据节点的端口上去
     */
    public Boolean maybeConnect(Node host) {
        synchronized (this) {
            String nodeId = remoteNodeId(host);
            if (!connectState.containsKey(nodeId) || connectState.get(nodeId).equals(DISCONNECTED)) {
                connectState.put(nodeId, CONNECTING);
                waitingConnectHosts.offer(host);
            }

            while (connectState.get(nodeId).equals(CONNECTING)) {
                try {
                    log.info("一直在等待发送请求，nodeId={}", nodeId);
                    wait(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return !connectState.get(nodeId).equals(DISCONNECTED);
        }
    }

    /**
     * 发送网络请求
     */
    public void sendRequest(NetworkRequest request) {
        String nodeId = remoteNodeId(request);
        ConcurrentLinkedQueue<NetworkRequest> requestQueue = waitingRequests.getOrDefault(nodeId, new ConcurrentLinkedQueue<>());
        waitingRequests.put(nodeId, requestQueue);
        requestQueue.offer(request);
    }

    /**
     * 等待指定请求的响应
     */
    public NetworkResponse waitResponse(String requestId) throws Exception {
        NetworkResponse response;

        // 等待知道获取响应结果
        while (ObjectUtil.isNull((response = finishedResponses.get(requestId)) )) {
            Thread.sleep(50);
            log.warn("等待响应结果");
        }

        // 获取到响应之后，移除请求
        toSendRequests.remove(response.getNodeId());
        finishedResponses.remove(requestId);
        return response;
    }

    /**
     * 网络连接的核心线程
     */
    class NetworkPollThread extends Thread {

        public void startup() {
            this.start();
            new RequestTimeoutCheckThread().start();
        }

        @Override
        public void run() {
            while (true) {
                // 建立连接
                tryConnect();
                // 准备发送请求
                prepareRequests();
                // 完成底层网络通信
                poll();
            }
        }

        /**
         * 尝试把排队中的机器发起连接的请求
         */
        private void tryConnect() {
            Node node;
            SocketChannel channel;
            while (ObjectUtil.isNotNull((node = waitingConnectHosts.poll()))) {
                try {
                    channel = SocketChannel.open();
                    channel.configureBlocking(false);
                    channel.connect(new InetSocketAddress(node.getIp(), node.getPort()));
                    channel.register(selector, SelectionKey.OP_CONNECT);
                } catch (Exception e) {
                    e.printStackTrace();
                    String nodeId = remoteNodeId(node);
                    connectState.put(nodeId, DISCONNECTED);
                }
            }
        }

        /**
         * 准备好要发送的请求
         */
        private void prepareRequests() {
            // 遍历发往多个 DataNode 节点的请求队列
            for (String nodeId : waitingRequests.keySet()) {
                // 看一下这台机器当前是否还没有请求马上就要发送出去了
                ConcurrentLinkedQueue<NetworkRequest> requestQueue = waitingRequests.get(nodeId);
                // 队列不为空 && 还暂存在待发送队列 toSendRequests 中
                if (!requestQueue.isEmpty() && !toSendRequests.containsKey(nodeId)) {
                    // 对这台机器获取一个派对的请求出来
                    NetworkRequest request = requestQueue.poll();
                    // 将这个请求暂存起来，接下来 就可以等待发送出去
                    toSendRequests.put(nodeId, request);
                    // 让这台机器对应的连接关注的事件为 OP_WRITE
                    SelectionKey key = connections.get(nodeId);
                    key.interestOps(SelectionKey.OP_WRITE);
                }
            }
        }

        /**
         * 尝试完成网络连接、请求发送、响应读取
         */
        private void poll() {
            SocketChannel channel = null;
            try {
                int selectedKeys = selector.select(100);
                if (selectedKeys <= 0) {
                    return;
                }

                Iterator<SelectionKey> keysIterator = selector.selectedKeys().iterator();
                while (keysIterator.hasNext()) {
                    SelectionKey key = keysIterator.next();
                    keysIterator.remove();
                    channel = (SocketChannel) key.channel();
                    if (key.isConnectable()) {
                        finishConnect(key, channel);
                    } else if (key.isWritable()) {
                        sendRequest(key, channel);
                    } else if (key.isReadable()) {
                        log.info("监听到服务端响应数据");
                        readResponse(key, channel);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                IoUtil.close(channel);
            }
        }

        /**
         * 完成跟机器的连接
         */
        private void finishConnect(SelectionKey key, SocketChannel channel) {
            String nodeId = null;
            try {
                InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
                nodeId = remoteNodeId(remoteAddress);

                if (channel.isConnectionPending()) {
                    while (!channel.finishConnect()) {
                        Thread.sleep(100);
                    }
                }
                log.info("完成与服务器【{}】的连接建立", nodeId);


                waitingRequests.put(nodeId, new ConcurrentLinkedQueue<>());
                connections.put(nodeId, key);
                connectState.put(nodeId, CONNECTED);
            } catch (Exception e) {
                e.printStackTrace();
                if (StrUtil.isNotEmpty(nodeId)) {
                    connectState.put(nodeId, DISCONNECTED);
                }
            }
        }

        /**
         * 发送请求
         */
        private void sendRequest(SelectionKey key, SocketChannel channel) {
            String nodeId = null;
            try {
                InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
                nodeId = remoteNodeId(remoteAddress);

                NetworkRequest request = toSendRequests.get(nodeId);
                ByteBuffer buffer = request.getBuffer();

                channel.write(buffer);
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }

                log.info("本次向【{}】的请求数据发送完毕", nodeId);
                request.setSendTime(System.currentTimeMillis());
                key.interestOps(SelectionKey.OP_READ);
            } catch (Exception e) {
                // 发送失败时处理逻辑
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                if (StrUtil.isNotEmpty(nodeId)) {
                    NetworkRequest request = toSendRequests.get(nodeId);
                    sendRequestError(request);
                }
                e.printStackTrace();
            }
        }

        public void sendRequestError(NetworkRequest request) {
            String nodeId = remoteNodeId(request);
            NetworkResponse response = new NetworkResponse();
            response.setNodeId(nodeId);
            response.setRequestId(request.getId());
            response.setNode(request.getNode());
            response.setError(true);
            response.setFinished(true);
            if (request.getNeedResponse()) {
                finishedResponses.put(request.getId(), response);
            } else {
                if (ObjectUtil.isNotNull(request.getCallback())) {
                    request.getCallback().process(response);
                }
                toSendRequests.remove(nodeId);
            }
        }

        /**
         * 读取响应信息
         */
        private void readResponse(SelectionKey key, SocketChannel channel) throws Exception {
            InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
            String nodeId = remoteNodeId(remoteAddress);
            NetworkRequest request = toSendRequests.get(nodeId);
            NetworkResponse response = null;

            if (request.getRequestType().equals(NetworkRequest.REQUEST_SEND_FILE)) {
                response = getSendFileResponse(request.getId(), nodeId, channel);
            } else if (request.getRequestType().equals(NetworkRequest.REQUEST_READ_FILE)) {
                response = getReadFileResponse(request.getId(), nodeId, channel);
            }

            if (ObjectUtil.isNull(response) || !response.getFinished()) {
                return;
            }

            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);

            // 需要响应结果，移除请求在同步获取到结果之后
            if (request.getNeedResponse()) {
                finishedResponses.put(request.getId(), response);
            } else {
                toSendRequests.remove(nodeId);
            }

            // 不需要响应结果，收到响应结果时，直接移除请求
            if (ObjectUtil.isNotNull(request.getCallback())) {
                request.getCallback().process(response);
            }
        }

        /**
         * 读取下载的文件响应
         */
        private NetworkResponse getReadFileResponse(String requestId, String nodeId, SocketChannel channel) throws Exception {
            NetworkResponse response;
            if (!unfinishedResponses.containsKey(nodeId)) {
                response = new NetworkResponse();
                response.setRequestId(requestId);
                response.setNodeId(nodeId);
                response.setError(false);
                response.setFinished(false);
            } else {
                response = unfinishedResponses.get(nodeId);
            }

            long fileLength = 0L;

            if (ObjectUtil.isNull(response.getBuffer())) {
                ByteBuffer lengthBuffer;
                if (ObjectUtil.isNull(response.getLengthBuffer())) {
                    lengthBuffer = ByteBuffer.allocate(NetworkRequest.FILE_LENGTH);
                    response.setLengthBuffer(lengthBuffer);
                } else {
                    lengthBuffer = response.getLengthBuffer();
                }

                channel.read(lengthBuffer);

                if (!lengthBuffer.hasRemaining()) {
                    lengthBuffer.rewind();
                    fileLength = lengthBuffer.getLong();
                } else {
                    unfinishedResponses.put(nodeId, response);
                }
            }

            if (fileLength > 0L || ObjectUtil.isNotNull(response.getBuffer())) {
                ByteBuffer buffer;

                if (ObjectUtil.isNull(response.getBuffer())) {
                    buffer = ByteBuffer.allocate(Math.toIntExact(fileLength));
                    response.setBuffer(buffer);
                } else {
                    buffer = response.getBuffer();
                }

                channel.read(buffer);

                if (!buffer.hasRemaining()) {
                    buffer.rewind();
                    response.setFinished(true);
                    unfinishedResponses.remove(nodeId);
                } else {
                    unfinishedResponses.put(nodeId, response);
                }
            }
            return response;
        }

        /**
         * 读取上传文件的响应
         */
        private NetworkResponse getSendFileResponse(String requestId, String nodeId, SocketChannel channel) throws Exception {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            channel.read(buffer);
            buffer.flip();
            NetworkResponse response = new NetworkResponse();
            response.setRequestId(requestId);
            response.setNodeId(nodeId);
            response.setBuffer(buffer);
            response.setError(false);
            response.setFinished(true);
            return response;
        }

        /**
         * 超时检测线程
         */
        class RequestTimeoutCheckThread extends Thread {
            @Override
            public void run() {
                while (true) {
                    try {
                        long now = System.currentTimeMillis();
                        for (NetworkRequest request : toSendRequests.values()) {
                            if (now - request.getSendTime() > REQUEST_TIMEOUT) {
                                log.error("超时检测{}s未发送数据出去，移除发送数据请求，request={}", REQUEST_TIMEOUT, request);
                                sendRequestError(request);
                            }
                        }
                        Thread.sleep(REQUEST_TIMEOUT_CHECK_INTERVAL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }
}
