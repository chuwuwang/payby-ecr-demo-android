package com.payby.pos.ecr.bluetooth;

public interface ConnectionListener {

    void onConnected();

    void onDisconnected();

    void onMessage(byte[] bytes);

}
