package com.payby.pos.ecr.bluetooth;

public interface ClassicBTClientListener {

    void onConnected();

    void onConnecting();

    void onDisconnected();

    void onMessage(byte[] bytes);

}
