package com.westboy.servers;

import java.io.IOException;

public class ZhangServerMain {

    public static void main(String[] args) throws IOException {
        ZhangServerConfig config = new ZhangServerConfig();
        ZhangServer ZhangServer = new ZhangServer(config);

    }
}
