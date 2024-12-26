package com.payby.pos.ecr.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.payby.pos.ecr.connect.ConnectionKernel;

public class ClassicBTManager {

    private static final String TAG = "ClassicBT-Client";

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

    public void connect(BluetoothDevice device) {
        if (classicBTClient == null) {
            classicBTClient = new ClassicBTClient(device);
            classicBTClient.setListener(listener);
        }
        classicBTClient.connect();
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

    private final ClassicBTClientListener listener = new ClassicBTClientListener() {

        @Override
        public void onConnected() {
            Log.e(TAG, "ClassicBT client onConnected");
            ConnectionKernel.getInstance().onConnected();
        }

        @Override
        public void onConnecting() {
            Log.e(TAG, "ClassicBT client onConnecting");
        }

        @Override
        public void onDisconnected() {
            Log.e(TAG, "ClassicBT client onDisconnected");
            ConnectionKernel.getInstance().onDisconnected();
        }

        @Override
        public void onMessage(byte[] bytes) {
            ConnectionKernel.getInstance().onReceived(bytes);
        }

    };

}
