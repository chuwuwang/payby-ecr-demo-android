package com.payby.pos.ecr.ble;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class BLEService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            handleIntent(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground();
    }

    private void handleIntent(Intent intent) {
        int action = intent.getIntExtra("extra_action", 0);
        BluetoothDevice bluetoothDevice = intent.getParcelableExtra("extra_bluetoothDevice");
        if (action == ACTION_CONNECT) {
            BLEManager.getInstance().connect(this, bluetoothDevice);
        } else if (action == ACTION_DISCONNECT) {
            BLEManager.getInstance().disconnect();
        }
    }

    private void startForeground() {
        Notification notification = createNotification();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(200, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE);
        } else {
            startForeground(200, notification);
        }
    }

    private void stopForeground() {
        stopForeground(true);
    }

    private Notification createNotification() {
        String channelId = "ECRDemo Service";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentText("BLE Service is running")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("The data information exchange for BLE");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        return builder.build();
    }

    public static final int ACTION_CONNECT = 1;
    public static final int ACTION_DISCONNECT = 2;

    public static void startAction(Context context, int action, BluetoothDevice device) {
        Intent intent = new Intent(context, BLEService.class);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("extra_action", action);
        intent.putExtra("extra_bluetoothDevice", device);
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

}
