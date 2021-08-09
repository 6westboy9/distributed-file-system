package com.westboy.servers;

import cn.hutool.core.util.ObjectUtil;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 统计每个服务端地址可以连接多少个客户端
 */
public class ConnectionQuotas {

    private final Integer maxConnectionsPerIp = 5;
    private final Map<InetAddress, Integer> counts = new HashMap<>();

    public ConnectionQuotas() {
    }

    public void increase(InetAddress address) {
        synchronized (counts) {
            int count = counts.getOrDefault(address, 0);
            counts.put(address, count + 1);
            if (count >= maxConnectionsPerIp) {
                throw new RuntimeException(String.format("Too many connections from %s (maximum = %d)", address, count));
            }
        }
    }

    public void decrease(InetAddress address) {
        synchronized (counts) {
            Integer count = counts.get(address);
            if (ObjectUtil.isNotNull(count)) {
                throw new RuntimeException("Attempted to decrease connection count for address with no connections, address: " + address);
            }
            if (count == 1) {
                counts.remove(address);
            } else {
                counts.put(address, count - 1);
            }
        }
    }
}
