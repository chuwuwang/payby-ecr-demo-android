package com.payby.pos.ecr.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.internal.processor.Processor;
import com.payby.pos.ecr.utils.ThreadPoolManager;
import com.uaepay.pos.ecr.Ecr;
import com.uaepay.pos.ecr.acquire.Acquire;
import com.uaepay.pos.ecr.common.Common;

public class HistoryActivity extends  BaseActivity{
    private TextView textReceive;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pages_layout);
        findViewById(R.id.widget_btn_get_pages_layout).setOnClickListener(this);
        textReceive = findViewById(R.id.widget_txt_receive);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.widget_btn_get_pages_layout) {
            getPages();
        }
    }

    private void getPages() {

        Timestamp startTimestamp = Timestamp.newBuilder()
                .setSeconds(System.currentTimeMillis() / 1000 - 60 * 60 * 24) // 24 hours ago
                .build();

        Timestamp endTimestamp = Timestamp.newBuilder()
                .setSeconds(System.currentTimeMillis() / 1000)
                .build();
        Common.TimeScope timeScope = Common.TimeScope.newBuilder()
                .setFrom(startTimestamp)
                .setTo(endTimestamp)
                .build();
        Common.PageParam pageParam = Common.PageParam.newBuilder()
                .setSize(50)  // page size , default 50
                .setNumber(0) // index of page
                .build();
        Acquire.QueryAcquireOrderPageRequest pageRequest = Acquire.QueryAcquireOrderPageRequest.newBuilder()
                .setOrderScope(Acquire.OrderScope.DEVICE_SCOPE)
                .setTimeScope(timeScope)
                .setPageParam(pageParam)
                .build();

        Any body = Any.pack(pageRequest);
        Timestamp timestamp = Timestamp.newBuilder()
                .setSeconds(System.currentTimeMillis() / 1000)
                .build();
        Ecr.Request request = Ecr.Request.newBuilder()
                .setMessageId(4)
                .setTimestamp(timestamp)
                .setServiceName(Processor.ACQUIRE_GET_ORDER_LIST)
                .setBody(body)
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

            String s = parser(bytes);
            runOnUiThread(() ->textReceive.setText(s));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
