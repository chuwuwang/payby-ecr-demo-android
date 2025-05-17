package com.payby.pos.ecr.ui.dialog.ble;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.payby.pos.ecr.R;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private OnDeviceClickListener listener;
    private final List<BluetoothDevice> deviceList = new ArrayList<>();

    public interface OnDeviceClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ble_device_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BluetoothDevice device = deviceList.get(position);
        String name = "";
        String address = device.getAddress();
        try {
            name = device.getName();
        } catch (Exception e) {
            // Handle exception if needed
        }
        String message = "name: " + name + "; address: " + address;
        holder.deviceName.setText(message);
        holder.itemView.setOnClickListener(
            v -> {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        );
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView deviceName;

        ViewHolder(View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.widget_txt_ble_device_name);
        }

    }

    public void setData(List<BluetoothDevice> list) {
        deviceList.clear();
        deviceList.addAll(list);
        notifyDataSetChanged();
    }

    public void setOnDeviceClickListener(OnDeviceClickListener listener) {
        this.listener = listener;
    }

}
