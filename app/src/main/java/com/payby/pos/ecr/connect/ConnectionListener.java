package com.payby.pos.ecr.connect;

public interface ConnectionListener {

    void onConnected();

    void onDisconnected();

    void onMessage(byte[] bytes);

}
