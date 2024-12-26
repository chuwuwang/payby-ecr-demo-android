package com.payby.pos.ecr.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.payby.pos.ecr.connect.ConnectionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BLEManager {

    private static final String TAG = "BLE-Client";

    private BLEManager() {
    }

    private static BLEManager instance;

    public static BLEManager getInstance() {
        if (instance == null) {
            instance = new BLEManager();
        }
        return instance;
    }

    private BLEClient bleClient;

    private final List<ConnectionListener> callbacks = new ArrayList<>();

    public void connect(Context context, BluetoothDevice device) {
        if (bleClient == null) {
            bleClient = new BLEClient(context, device);
            bleClient.setListener(listener);
        }
        bleClient.deviceConnect();
    }

    public void disconnect() {
        if (bleClient != null) {
            bleClient.deviceDisconnect();
            bleClient.setListener(null);
        }
        bleClient = null;
    }

    public void send(byte[] data) {
        if (bleClient != null) {
            bleClient.send(data);
        }
    }

    public boolean isConnected() {
        return bleClient != null && bleClient.isDeviceConnected();
    }

    public void addListener(ConnectionListener listener) {
        callbacks.add(listener);
    }

    public void removeListener(ConnectionListener listener) {
        callbacks.remove(listener);
    }

    private final BLEClientListener listener = new BLEClientListener() {

        @Override
        public void onConnected() {
            Log.e(TAG, "BLE client onConnected");
            List<ConnectionListener> synchronizededList = Collections.synchronizedList(callbacks);
            for (ConnectionListener listener : synchronizededList) {
                listener.onConnected();
            }
        }

        @Override
        public void onConnecting() {
            Log.e(TAG, "BLE client onConnecting");
        }

        @Override
        public void onDisconnected() {
            Log.e(TAG, "BLE client onDisconnected");
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
