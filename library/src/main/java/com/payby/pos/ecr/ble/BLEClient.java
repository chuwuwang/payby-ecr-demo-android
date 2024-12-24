package com.payby.pos.ecr.ble;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.UUID;

import no.nordicsemi.android.ble.BleManager;
import no.nordicsemi.android.ble.WriteRequest;
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.data.Data;

public class BLEClient extends BleManager {

    private static final String TAG = "BLE-Client";

    private static final UUID SERVICE_UUID = UUID.fromString("FE72265A-0F16-4B45-B6B7-95889930140A");

    private static final UUID CHARACTERISTIC_UUID = UUID.fromString("FE72265B-0F16-4B45-B6B7-95889930140A");

    private BluetoothGattCharacteristic myCharacteristic;

    private boolean connecting = false;
    private final BluetoothDevice bluetoothDevice;

    private BLEClientListener listener;

    public void setListener(BLEClientListener listener) {
        this.listener = listener;
    }

    public BLEClient(@NonNull Context context, BluetoothDevice device) {
        super(context);
        bluetoothDevice = device;
    }

    public void connect() {
        boolean connected = isDeviceConnected();
        if (connected) {
            if (listener != null) {
                listener.onConnected();
            }
            return;
        }
        if (connecting) {
            if (listener != null) {
                listener.onConnecting();
            }
            return;
        }
        connecting = true;
        connect(bluetoothDevice).useAutoConnect(true).enqueue();
    }

    public boolean isDeviceConnected() {
        boolean ready = isReady();
        boolean connected = isConnected();
        Log.e(TAG, "isDeviceConnected --> isReady: " + ready + " isConnected:" + connected);
        return ready;
    }

    public void send(byte[] bytes) {
        try {
            boolean connected = isDeviceConnected();
            if (connected) {
                PacketSplitter splitter = new PacketSplitter();
                writeCharacteristic(myCharacteristic, bytes, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT).split(splitter);
            } else {
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initialize() {
        requestMtu(512).enqueue();

        setNotificationCallback(myCharacteristic).with(dataReceivedCallback);

        WriteRequest request = enableNotifications(myCharacteristic).fail(failCallback);
        beginAtomicRequestQueue().add(request).done(successCallback).enqueue();
    }

    @Override
    protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
        Log.e(TAG, "isRequiredServiceSupported");
        BluetoothGattService service = gatt.getService(SERVICE_UUID);
        if (service != null) {
            myCharacteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
        }
        return myCharacteristic != null;
    }

    @Override
    public void log(int priority, @NonNull String message) {
        Log.e(TAG, "log --> " + message);
    }

    @Override
    protected void onServicesInvalidated() {
        Log.e(TAG, "onServicesInvalidated");
        myCharacteristic = null;
    }

    private final DataReceivedCallback dataReceivedCallback = new DataReceivedCallback() {

        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            byte[] bytes = data.getValue();
            String string = new String(bytes);
            Log.e(TAG, "onDataReceived: " + string);

        }

    };

    private final FailCallback failCallback = new FailCallback() {

        @Override
        public void onRequestFailed(@NonNull BluetoothDevice device, int status) {
            String info = getDeviceInfo(device);
            Log.e(TAG, "onRequestFailed Could not subscribe: " + status + " " + info);
            disconnect().enqueue();
        }

    };

    private final SuccessCallback successCallback = new SuccessCallback() {

        @Override
        public void onRequestCompleted(@NonNull BluetoothDevice device) {
            String info = getDeviceInfo(device);
            Log.e(TAG, "onRequestCompleted target initialized " + info);
            if (listener != null) {
                listener.onConnected();
            }
        }

    };

    private String getDeviceInfo(BluetoothDevice device) {
        String address = device.getAddress();
        try {
            return device.getName() + ": " + address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return address;
    }

}
