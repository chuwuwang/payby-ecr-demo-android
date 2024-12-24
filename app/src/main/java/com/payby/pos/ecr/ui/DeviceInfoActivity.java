package com.payby.pos.ecr.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.protobuf.Timestamp;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.internal.processor.Processor;
import com.uaepay.pos.ecr.Ecr;

public class DeviceInfoActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        initView();
    }

    private void initView() {
        findViewById(R.id.getDeviceInfo).setOnClickListener(this);
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

        ConnectionKernel.getInstance().send(bytes);
    }

}
