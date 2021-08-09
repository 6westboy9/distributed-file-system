package com.westboy.common.utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicLong;

public class SocketSupport {

    private static final AtomicLong nextConnectionIndex = new AtomicLong(1);

    public static String connectionId(SocketChannel socketChannel) throws IOException {
        InetSocketAddress localAddress = (InetSocketAddress) socketChannel.getLocalAddress();
        InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
        String localHost = localAddress.getHostString();
        int localPort = localAddress.getPort();
        String remoteHost = remoteAddress.getHostString();
        int remotePort = remoteAddress.getPort();
        return localHost + ":" + localPort + "-" + remoteHost + ":" + remotePort + "-" + nextConnectionIndex.getAndIncrement();
    }

}
