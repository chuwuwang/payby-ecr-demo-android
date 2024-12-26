package com.payby.pos.ecr.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.payby.pos.ecr.connect.ConnectionKernel;

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

    private final BLEClientListener listener = new BLEClientListener() {

        @Override
        public void onConnected() {
            Log.e(TAG, "BLE client onConnected");
            ConnectionKernel.getInstance().onConnected();
        }

        @Override
        public void onConnecting() {
            Log.e(TAG, "BLE client onConnecting");
        }

        @Override
        public void onDisconnected() {
            Log.e(TAG, "BLE client onDisconnected");
            ConnectionKernel.getInstance().onDisconnected();
        }

        @Override
        public void onMessage(byte[] bytes) {
            ConnectionKernel.getInstance().onReceived(bytes);
        }

    };

}
