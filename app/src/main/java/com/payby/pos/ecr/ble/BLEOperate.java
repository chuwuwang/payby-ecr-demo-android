package com.payby.pos.ecr.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.ParcelUuid;
import android.util.Log;

import com.payby.pos.ecr.App;

import java.util.Collections;
import java.util.List;

public class BLEOperate {

    private final Activity activity;

    private final BluetoothLeScanner bluetoothLeScanner;

    public BLEOperate(Activity activity) {
        this.activity = activity;
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void startSearch() {
        try {
            ScanSettings settings = new ScanSettings.Builder()
                    .setReportDelay(0) // Set to 0 to be notified of scan results immediately.
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .build();
            ParcelUuid serviceUuid = new ParcelUuid(BLEClient.SERVICE_UUID);
            ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(serviceUuid).build();
            List<ScanFilter> filters = Collections.singletonList(scanFilter);
            bluetoothLeScanner.startScan(filters, settings, scanCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSearch() {
        try {
            bluetoothLeScanner.stopScan(scanCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();
            String address = device.getAddress();
            Log.e(App.TAG, "onScanResult: " + address);
            if (selectionListener != null) {
                selectionListener.onSelectionBluetoothDevice(device);
            }
            stopSearch();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(App.TAG, "onScanFailed: " + errorCode);
        }

    };

    public interface SelectionBluetoothDeviceListener {

        void onSelectionBluetoothDevice(BluetoothDevice device);

    }

    private SelectionBluetoothDeviceListener selectionListener;

    public void setSelectionListener(SelectionBluetoothDeviceListener listener) {
        selectionListener = listener;
    }

}
