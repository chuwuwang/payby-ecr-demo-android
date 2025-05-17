package com.payby.pos.ecr.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.util.Log;
import android.util.SparseArray;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.ui.dialog.BLEConnectDialog;
import com.payby.pos.ecr.ui.dialog.ble.DeviceAdapter;
import com.payby.pos.ecr.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BLEOperate {

    private final Handler handler;
    private final Activity activity;
    private final BluetoothLeScanner bluetoothLeScanner;

    private DeviceAdapter deviceAdapter;
    private List<BluetoothDevice> deviceList;
    private BLEConnectDialog bleConnectDialog;

    public BLEOperate(Activity activity) {
        this.activity = activity;
        Looper mainLooper = Looper.getMainLooper();
        handler = new Handler(mainLooper);
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        ArrayList<BluetoothDevice> list = new ArrayList<>();
        deviceList = Collections.synchronizedList(list);
        initDialog(activity);
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
            String deviceName = "";
            String address = device.getAddress();
            try {
                deviceName = device.getName();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e(App.TAG, "onScanResult: " + address + " deviceName: " + deviceName);
            ScanRecord scanRecord = result.getScanRecord();
            if (scanRecord != null) {
                SparseArray<byte[]> manufacturerSpecificData = scanRecord.getManufacturerSpecificData();
                for (int i = 0; i < manufacturerSpecificData.size(); i++) {
                    int manufacturerId = manufacturerSpecificData.keyAt(i);
                    String id = Integer.toHexString(manufacturerId);
                    byte[] data = manufacturerSpecificData.get(manufacturerId);
                    String hexString = Utils.bytes2HexString(data);
                    Log.e(App.TAG, "onScanResult: 厂商ID: 0x" + id + " Manufacturer Data: " + hexString);
                }
            }
            boolean contains = deviceList.contains(device);
            if (contains) return;
            synchronized (deviceList) {
                deviceList.add(device);
                deviceAdapter.setData(deviceList);
            }
            handler.postDelayed( () -> stopSearch(), 10000);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(App.TAG, "onScanFailed: " + errorCode);
            activity.runOnUiThread(WaitDialog::dismiss);
        }

    };

    public interface SelectionBluetoothDeviceListener {

        void onSelectionBluetoothDevice(BluetoothDevice device);

    }

    private SelectionBluetoothDeviceListener selectionListener;

    public void setSelectionListener(SelectionBluetoothDeviceListener listener) {
        selectionListener = listener;
    }

    private void initDialog(Activity activity) {
        deviceAdapter = new DeviceAdapter();
        deviceAdapter.setData(deviceList);
        bleConnectDialog = new BLEConnectDialog(activity);
        RecyclerView recyclerView = bleConnectDialog.findViewById(R.id.widget_recycler_ble_device_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(deviceAdapter);
        deviceAdapter.setOnDeviceClickListener(
            position -> {
                BluetoothDevice device = deviceList.get(position);
                if (selectionListener != null) {
                    boolean bool = bleConnectDialog != null && bleConnectDialog.isShowing();
                    if (bool) {
                        bleConnectDialog.dismiss();
                    }
                    stopSearch();
                    selectionListener.onSelectionBluetoothDevice(device);
                }
            }
        );
        bleConnectDialog.show();
    }

}
