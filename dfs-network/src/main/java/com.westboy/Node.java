package com.westboy;

public class Node {

    private final int id;
    private final String idStr;
    private final String host;
    private final int port;

    public Node(int id, String host, int port) {
        this.id = id;
        this.idStr = Integer.toString(id);
        this.host = host;
        this.port = port;
    }

    public boolean isEmpty() {
        return host == null || host.isEmpty() || port < 0;
    }

    public String idStr() {
        return idStr;
    }

    public int port() {
        return port;
    }

    public String host() {
        return host;
    }

    @Override
    public String toString() {
        return host + ":" + port + " (id: " + idStr + ")";
    }
}
