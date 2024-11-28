package com.payby.pos.ecr.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClassicBTManager {

    private ClassicBTManager() {
    }

    private static ClassicBTManager instance;

    public static ClassicBTManager getInstance() {
        if (instance == null) {
            instance = new ClassicBTManager();
        }
        return instance;
    }

    private ClassicBTClient classicBTClient;

    private final List<ConnectionListener> callbacks = new ArrayList<>();

    public void connect(BluetoothDevice device) {
        boolean connected = isConnected();
        if (connected) {
            Log.e("Demo", "The device is already connected");
            for (ConnectionListener listener : callbacks) {
                listener.onConnected();
            }
        } else {
            classicBTClient = new ClassicBTClient(device);
            classicBTClient.setListener(listener);
            classicBTClient.connect();
        }
    }

    public void disconnect() {
        if (classicBTClient != null) {
            classicBTClient.disconnect();
            classicBTClient.setListener(null);
        }
        classicBTClient = null;
    }

    public void send(byte[] data) {
        if (classicBTClient != null) {
            classicBTClient.send(data);
        }
    }

    public boolean isConnected() {
        return classicBTClient != null && classicBTClient.isConnected();
    }

    public void addListener(ConnectionListener listener) {
        callbacks.add(listener);
    }

    public void removeListener(ConnectionListener listener) {
        callbacks.remove(listener);
    }

    private final ClassicBTClientListener listener = new ClassicBTClientListener() {

        @Override
        public void onConnected() {
            Log.e("Demo", "Connected");
            List<ConnectionListener> synchronizededList = Collections.synchronizedList(callbacks);
            for (ConnectionListener listener : synchronizededList) {
                listener.onConnected();
            }
        }

        @Override
        public void onConnecting() {
            Log.e("Demo", "Connecting");
        }

        @Override
        public void onDisconnected() {
            Log.e("Demo", "Disconnected");
            List<ConnectionListener> synchronizededList = Collections.synchronizedList(callbacks);
            for (ConnectionListener listener : synchronizededList) {
                listener.onDisconnected();
            }
        }

        @Override
        public void onMessage(byte[] bytes) {
            List<ConnectionListener> synchronizededList = Collections.synchronizedList(callbacks);
            for (ConnectionListener listener : synchronizededList) {
                listener.onMessage(bytes);
            }
        }

    };

}