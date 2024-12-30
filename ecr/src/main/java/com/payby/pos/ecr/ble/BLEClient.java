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
import no.nordicsemi.android.ble.callback.DataReceivedCallback;
import no.nordicsemi.android.ble.callback.FailCallback;
import no.nordicsemi.android.ble.callback.SuccessCallback;
import no.nordicsemi.android.ble.data.Data;

public class BLEClient extends BleManager {

    private static final String TAG = "BLE-Client";

    public static final UUID SERVICE_UUID = UUID.fromString("80323644-3537-4F0B-A53B-CF494ECEAAB3");
    public static final UUID CHARACTERISTIC_UUID = UUID.fromString("80323645-3537-4F0B-A53B-CF494ECEAAB3");

    private boolean connecting = false;
    private final BluetoothDevice bluetoothDevice;
    private BluetoothGattCharacteristic characteristic;

    private BLEClientListener listener;

    public void setListener(BLEClientListener listener) {
        this.listener = listener;
    }

    public BLEClient(@NonNull Context context, BluetoothDevice device) {
        super(context);
        bluetoothDevice = device;
    }

    public void deviceConnect() {
        boolean connected = isDeviceConnected();
        if (connected) {
            connecting = false;
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

    public void deviceDisconnect() {
        cancelQueue();
        disconnect().enqueue();
        close();
    }

    /**
     * The data is split into MTU size packets using packet splitter [PacketSplitter.chunk] before sending it to the server.
     *
     * @param bytes
     */
    public void send(byte[] bytes) {
        try {
            boolean connected = isDeviceConnected();
            if (connected) {
                logging(bytes, "<--- BLE Client send");
                final SuccessCallback successCallback = device -> Log.e(TAG, "<--- BLE Client send success");
                final FailCallback failCallback = (device, status) -> Log.e(TAG, "<--- BLE Client send failure: " + status);
                PacketSplitter splitter = new PacketSplitter();
                writeCharacteristic(characteristic, bytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
                        .split(splitter)
                        .fail(failCallback)
                        .done(successCallback)
                        .enqueue();
            } else {
                if (listener != null) {
                    listener.onDisconnected();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDeviceConnected() {
        boolean ready = isReady();
        boolean connected = isConnected();
        Log.e(TAG, "isDeviceConnected --> isReady: " + ready + " isConnected:" + connected);
        return ready;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
        Log.e(TAG, "BLE Client isRequiredServiceSupported");
        BluetoothGattService service = gatt.getService(SERVICE_UUID);
        if (service != null) {
            characteristic = service.getCharacteristic(CHARACTERISTIC_UUID);
        }
        return characteristic != null;
    }

    @Override
    protected void initialize() {
        // request Mtu-512
        requestMtu(512).enqueue();

        // Merges packets until the entire text is present in the stream [PacketMerger.merge]
        PacketMerger merger = new PacketMerger();
        final DataReceivedCallback dataReceivedCallback = new DataReceivedCallback() {

            @Override
            public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
                byte[] bytes = data.getValue();
                logging(bytes, "---> BLE Client receive");
                if (listener != null && bytes != null) {
                    listener.onMessage(bytes);
                }
            }

        };
        setNotificationCallback(characteristic).merge(merger).with(dataReceivedCallback);

        final FailCallback failCallback = (device, status) -> {
            String info = getDeviceInfo(device);
            Log.e(TAG, "BLE Client onRequestFailed could not subscribe: " + status + " " + info);
            connecting = false;
            disconnect().enqueue();
        };
        final SuccessCallback successCallback = device -> {
            String info = getDeviceInfo(device);
            Log.e(TAG, "BLE Client onRequestCompleted target initialized " + info);
            if (listener != null) {
                listener.onConnected();
            }
            connecting = false;
        };
        enableNotifications(characteristic).fail(failCallback).done(successCallback).enqueue();
    }

    @Override
    public void log(int priority, @NonNull String message) {
        Log.e(TAG, "BLE Client log --> " + message);
    }

    @Override
    protected void onServicesInvalidated() {
        Log.e(TAG, "BLE Client onServicesInvalidated");
        characteristic = null;
        if (listener != null) {
            listener.onDisconnected();
        }
        connecting = false;
    }

    private String getDeviceInfo(BluetoothDevice device) {
        String address = device.getAddress();
        try {
            return device.getName() + ": " + address;
        } catch (Exception e) { }
        return address;
    }

    private void logging(byte[] bytes, String message) {
        try {
            String string = new String(bytes);
            Log.e(TAG, message + ": " + string);
        } catch (Exception e) { }
    }

}
