package com.payby.pos.ecr.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;

import com.kongzue.dialogx.dialogs.BottomMenu;
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener;
import com.payby.pos.ecr.App;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BTOperate {

    private final Activity activity;

    public BTOperate(Activity activity) {
        this.activity = activity;
    }

    private final Map<String, BluetoothDevice> mapBTDevice = new HashMap<>();

    public void findPairedBTDevices() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            App.showToast("Bluetooth is not supported on this device");
            return;
        }
        boolean enabled = adapter.isEnabled();
        if (enabled == false) {
            App.showToast("Please turn on Bluetooth");
            return;
        }
        Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
        if (bondedDevices != null && bondedDevices.size() > 0) {
            List<CharSequence> bluetoothList = new ArrayList<>();
            for (BluetoothDevice device : bondedDevices) {
                String name = device.getName();
                String address = device.getAddress();
                Log.e("Demo", "name: " + name + ", address: " + address);
                String key = name + " : " + address;
                bluetoothList.add(key);
                mapBTDevice.put(key, device);
            }
            showBTSelectionDialog(bluetoothList);
        } else {
            App.showToast("No paired Bluetooth devices found");
        }
    }

    private void showBTSelectionDialog(List<CharSequence> list) {
        OnMenuItemClickListener<BottomMenu> listener = new OnMenuItemClickListener<BottomMenu>() {

            @Override
            public boolean onClick(BottomMenu dialog, CharSequence text, int index) {
                BluetoothDevice device = mapBTDevice.get(text);
                if (selectionListener != null) {
                    selectionListener.onSelectionBluetoothDevice(device);
                }
                return false;
            }

        };
        BottomMenu.show("Selection Bluetooth Device", list, listener);
    }

    public interface SelectionBluetoothDeviceListener {

        void onSelectionBluetoothDevice(BluetoothDevice device);

    }

    private SelectionBluetoothDeviceListener selectionListener;

    public void setSelectionListener(SelectionBluetoothDeviceListener listener) {
        selectionListener = listener;
    }

}
