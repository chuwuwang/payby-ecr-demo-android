package com.payby.pos.ecr.connect;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;

import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.ble.BLEManager;
import com.payby.pos.ecr.ble.BLEOperate;
import com.payby.pos.ecr.ble.BLEService;
import com.payby.pos.ecr.bluetooth.BTOperate;
import com.payby.pos.ecr.bluetooth.ClassicBTManager;
import com.payby.pos.ecr.bluetooth.ClassicBTService;

public class ConnectionKernel {

    private ConnectType connectType;

    private ConnectionKernel() {
    }

    private static ConnectionKernel instance;

    public static ConnectionKernel getInstance() {
        if (instance == null) {
            instance = new ConnectionKernel();
        }
        return instance;
    }

    public void addListener(ConnectionListener listener) {
        if (connectType == ConnectType.BLUETOOTH) {
            ClassicBTManager.getInstance().addListener(listener);
        } else if (connectType == ConnectType.BLE) {
            BLEManager.getInstance().addListener(listener);
        }
    }

    public void removeListener(ConnectionListener listener) {
        if (connectType == ConnectType.BLUETOOTH) {
            ClassicBTManager.getInstance().removeListener(listener);
        } else if (connectType == ConnectType.BLE) {
            BLEManager.getInstance().removeListener(listener);
        }
    }

    public void connectWithClassicBT(Activity activity) {
        connectType = ConnectType.BLUETOOTH;
        final BTOperate.SelectionBluetoothDeviceListener selectionListener = new BTOperate.SelectionBluetoothDeviceListener() {

            @Override
            public void onSelectionBluetoothDevice(BluetoothDevice device) {
                WaitDialog.show("Connecting...");
                ClassicBTService.startAction(activity, ClassicBTService.ACTION_CONNECT, device);
            }

        };
        BTOperate operate = new BTOperate(activity);
        operate.setSelectionListener(selectionListener);
        operate.findPairedBTDevices();
    }

    public void connectWithBLE(Activity activity) {
        connectType = ConnectType.BLE;
        final BLEOperate.SelectionBluetoothDeviceListener selectionListener = new BLEOperate.SelectionBluetoothDeviceListener() {

            @Override
            public void onSelectionBluetoothDevice(BluetoothDevice device) {
                WaitDialog.show("Connecting...");
                BLEService.startAction(activity, BLEService.ACTION_CONNECT, device);
            }

        };
        BLEOperate operate = new BLEOperate(activity);
        operate.setSelectionListener(selectionListener);
        operate.startSearch();
    }

    public void disconnect() {
        if (connectType == ConnectType.BLUETOOTH) {
            ClassicBTManager.getInstance().disconnect();
            ClassicBTService.startAction(App.instance, ClassicBTService.ACTION_DISCONNECT, null);
        } else if (connectType == ConnectType.BLE) {
            BLEManager.getInstance().disconnect();
            BLEService.startAction(App.instance, BLEService.ACTION_DISCONNECT, null);
        }
    }

    public void send(byte[] data) {
        if (connectType == ConnectType.BLUETOOTH) {
            ClassicBTManager.getInstance().send(data);
        } else if (connectType == ConnectType.BLE) {
            BLEManager.getInstance().send(data);
        }
    }

    public boolean isConnected() {
        if (connectType == ConnectType.BLUETOOTH) {
            return ClassicBTManager.getInstance().isConnected();
        } else if (connectType == ConnectType.BLE) {
            return BLEManager.getInstance().isConnected();
        }
        return false;
    }

}
