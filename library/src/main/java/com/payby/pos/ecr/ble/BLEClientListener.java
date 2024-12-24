package com.payby.pos.ecr.ble;

public interface BLEClientListener {

    void onConnected();

    void onConnecting();

    void onDisconnected();

    void onMessage(byte[] bytes);

}
