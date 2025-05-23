package com.payby.pos.ecr.ui;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectType;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.connect.ConnectionListener;
import com.payby.pos.ecr.inapp.InAppManager;
import com.payby.pos.ecr.internal.InAppCallback;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

public class ConnectionActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_layout);
        requestPermission();
        boolean connected = ConnectionKernel.getInstance().isConnected();
        if (connected) {
            gotoMainActivity();
        } else {
            initView();
        }
    }

    private void initView() {
        findViewById(R.id.bleConnectBtn).setOnClickListener(this);
        findViewById(R.id.inAppConnectBtn).setOnClickListener(this);
        findViewById(R.id.classicBTConnectBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        final int id = view.getId();
        if (id == R.id.classicBTConnectBtn) {
            ConnectionKernel.getInstance().connectWithClassicBT(this);
        } else if (id == R.id.bleConnectBtn) {
            ConnectionKernel.getInstance().connectWithBLE(this);
        } else if (id == R.id.inAppConnectBtn) {
            ConnectionKernel.getInstance().connectWithInApp(this);
        }
    }

    @Override
    public void onDeviceConnected() {
        runOnUiThread(WaitDialog::dismiss);
        gotoMainActivity();
    }

    @Override
    public void onDeviceDisconnected() {
        runOnUiThread(WaitDialog::dismiss);
        showToast("Device disconnected");
    }

    private void gotoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void requestPermission() {
        PermissionX.init(this)
            .permissions(
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_ADMIN,
                    android.Manifest.permission.BLUETOOTH_ADVERTISE,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .request(requestCallback);
    }

    private final RequestCallback requestCallback = new RequestCallback() {

        @Override
        public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
            Log.e(App.TAG, "onResult: allGranted = " + allGranted);
            for (String permission : grantedList) {
                Log.e(App.TAG, permission + " Granted");
            }
        }

    };

}
