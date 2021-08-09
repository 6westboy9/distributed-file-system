package com.westboy;

import java.io.IOException;

public interface Selectable {

    void connect(String id, String host, int port, int socketSendBuffer, int socketReceiveBuffer) throws IOException;

    void send(Send send);

    void poll(long timeout) throws IOException;

    void close();

}
