package com.payby.pos.ecr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.bluetooth.ClassicBTManager;
import com.payby.pos.ecr.bluetooth.ConnectionListener;
import com.payby.pos.ecr.connect.ConnectType;
import com.payby.pos.ecr.inapp.InAppServiceBinder;
import com.payby.pos.ecr.connect.ConnectService;
import com.permissionx.guolindev.callback.RequestCallback;

import java.util.List;

public class ConnectionActivity extends BaseActivity {

  private Button inAppConnectBtn;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_connect_layout);

    boolean connected = ConnectService.INSTANCE.isConnected();
    if (connected) {
      Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
      startActivity(intent);
      finish();
    } else {
      initView();
      ConnectService.INSTANCE.initPermission(this,requestCallback);
    }
    initView();
  }

  private void initView() {
    findViewById(R.id.classic_bluetooth_connect_btn).setOnClickListener(this);
    inAppConnectBtn = (Button) findViewById(R.id.inAppConnectBtn);
    inAppConnectBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        ConnectService.INSTANCE.setConnectCallback((it) -> {
          if (it) {
            runOnUiThread(WaitDialog::dismiss);

            Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
          }
          return null;
        });
        ConnectService.INSTANCE.connect(ConnectionActivity.this, ConnectType.IN_APP);

      }
    });
  }

  @Override
  public void onClick(View view) {
    final int id = view.getId();
    if (id == R.id.classic_bluetooth_connect_btn) {
      ConnectService.INSTANCE.setConnectCallback((it) -> {
        if (it) {
          runOnUiThread(WaitDialog::dismiss);

          Intent intent = new Intent(ConnectionActivity.this, MainActivity.class);
          startActivity(intent);
          finish();
        }
        return null;
      });
      ConnectService.INSTANCE.connect(this, ConnectType.BLUETOOTH);
    }
  }

  private final RequestCallback requestCallback = new RequestCallback() {

    @Override
    public void onResult(boolean allGranted, @NonNull List<String> grantedList, @NonNull List<String> deniedList) {
      Log.e("Demo", "onResult: allGranted = " + allGranted);
      for (String permission : grantedList) {
        Log.e("Demo", permission + " Granted");
      }
    }

  };

}
