package com.payby.pos.ecr.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.protobuf.Timestamp;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.internal.processor.Processor;
import com.payby.pos.ecr.utils.ThreadPoolManager;
import com.uaepay.pos.ecr.Ecr;

public class DeviceInfoActivity extends BaseActivity {
    private TextView textReceive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        initView();
    }

    private void initView() {
        findViewById(R.id.getDeviceInfo).setOnClickListener(this);
        textReceive = findViewById(R.id.widget_txt_receive);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.getDeviceInfo) {
            getDeviceInfo();
        }
    }

    private void getDeviceInfo() {
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(System.currentTimeMillis() / 1000)
                .build();
        Ecr.Request request = Ecr.Request.newBuilder()
                .setMessageId(4)
                .setTimestamp(timestamp)
                .setServiceName(Processor.DEVICE_GET_THIS)
                .build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder()
                .setVersion(1)
                .setRequest(request)
                .build();
        byte[] bytes = envelope.toByteArray();
        WaitDialog.show("Precessing...");
        ThreadPoolManager.executeCacheTask(
                () -> ConnectionKernel.getInstance().send(bytes)
        );
    }

    @Override
    public void onReceiveMessage(byte[] bytes) {
        runOnUiThread(WaitDialog::dismiss);
        Log.e(App.TAG, "onReceiveMessage ---------------");
        try {
            Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.parseFrom(bytes);
            Ecr.Response response = envelope.getResponse();
            if (response.getMessageId() == 4) {
                String s = parserResponse(response);
                showToast(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
