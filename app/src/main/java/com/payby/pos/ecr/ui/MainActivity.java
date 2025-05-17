package com.payby.pos.ecr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.protobuf.Timestamp;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.utils.Utils;
import com.uaepay.pos.ecr.Ecr;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);
        initView();
    }

    private void initView() {
        findViewById(R.id.widget_btn_sale).setOnClickListener(this);
        findViewById(R.id.widget_btn_void).setOnClickListener(this);
        findViewById(R.id.widget_btn_refund).setOnClickListener(this);
        findViewById(R.id.widget_btn_query).setOnClickListener(this);
        findViewById(R.id.widget_btn_get_refund).setOnClickListener(this);
        findViewById(R.id.widget_btn_get_order_receipt).setOnClickListener(this);
        findViewById(R.id.widget_btn_print_order_receipt).setOnClickListener(this);
        findViewById(R.id.widget_btn_get_refund_receipt).setOnClickListener(this);
        findViewById(R.id.widget_btn_print_refund_receipt).setOnClickListener(this);
        findViewById(R.id.getDeviceInfo).setOnClickListener(this);
        findViewById(R.id.settlement).setOnClickListener(this);
        findViewById(R.id.common_btn_ping).setOnClickListener(this);
        findViewById(R.id.common_btn_pong).setOnClickListener(this);
        findViewById(R.id.common_btn_disconnect).setOnClickListener(this);
        findViewById(R.id.widget_btn_get_pages).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        boolean doubleClick = Utils.isDoubleClick();
        if (doubleClick) return;
        if (view.getId() == R.id.widget_btn_sale) {
            doSale();
        } else if (view.getId() == R.id.widget_btn_void) {
            Intent intent = new Intent(MainActivity.this, VoidActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.widget_btn_refund) {
            Intent intent = new Intent(MainActivity.this, RefundActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.widget_btn_query) {
            getOrder(0);
        } else if (view.getId() == R.id.widget_btn_get_refund) {
            getOrder(1);
        } else if (view.getId() == R.id.widget_btn_get_order_receipt) {
            doReceipt(0);
        } else if (view.getId() == R.id.widget_btn_print_order_receipt) {
            doReceipt(1);
        } else if (view.getId() == R.id.widget_btn_get_refund_receipt) {
            doReceipt(2);
        } else if (view.getId() == R.id.widget_btn_print_refund_receipt) {
            doReceipt(3);
        } else if (view.getId() == R.id.getDeviceInfo) {
            getDeviceInfo();
        } else if (view.getId() == R.id.settlement) {
            Intent intent = new Intent(MainActivity.this, SettlementActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.common_btn_ping) {
            doPING();
        } else if (view.getId() == R.id.common_btn_pong) {
            doPONG();
        } else if (view.getId() == R.id.common_btn_disconnect) {
          ConnectionKernel.getInstance().disconnect();
          onDeviceDisconnected();
        } else if (view.getId() == R.id.widget_btn_get_pages) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        }
    }

    private void getDeviceInfo() {
        Intent intent = new Intent(this, DeviceInfoActivity.class);
        startActivity(intent);
    }

    private void doSale() {
        Intent intent = new Intent(this, SaleActivity.class);
        startActivity(intent);
    }

    private void getOrder(int type) {
        Intent intent = new Intent(this, GetOrderActivity.class);
        intent.putExtra("order_type", type);
        startActivity(intent);
    }

    private void doReceipt(int type) {
        Intent intent = new Intent(this, ReceiptsActivity.class);
        intent.putExtra("receipts_type", type);
        startActivity(intent);
    }

    private void doPING() {
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Ping ping = Ecr.Ping.newBuilder().setMessageId(5).setTimestamp(timestamp).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setPing(ping).build();
        byte[] byteArray = envelope.toByteArray();
        ConnectionKernel.getInstance().send(byteArray);
    }

    private void doPONG() {
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Pong pong = Ecr.Pong.newBuilder().setMessageId(5).setTimestamp(timestamp).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setPong(pong).build();
        byte[] byteArray = envelope.toByteArray();
        ConnectionKernel.getInstance().send(byteArray);
    }

    @Override
    public void onReceiveMessage(byte[] bytes) {
        super.onReceiveMessage(bytes);
        try {
            StringBuffer sb = new StringBuffer();
            Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.parseFrom(bytes);
            Ecr.EcrEnvelope.ContentCase contentCase = envelope.getContentCase();
            switch (contentCase) {
                case PING:
                    Ecr.Ping ping = envelope.getPing();
                    sb.append(ping);
                    break;
                case PONG:
                    Ecr.Pong pong = envelope.getPong();
                    sb.append(pong);
                    break;
                case RESPONSE:
                    Ecr.Response response = envelope.getResponse();
                    String parserResponse = parserResponse(response);
                    sb.append(parserResponse);
                    break;
                default:
                    break;
            }
            Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeviceDisconnected() {
        super.onDeviceDisconnected();
        gotoConnectActivity();
    }

    private void gotoConnectActivity() {
        Intent intent = new Intent(this, ConnectionActivity.class);
        startActivity(intent);
        finish();
    }

}
