package com.payby.pos.ecr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.bluetooth.ClassicBTManager;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.connect.ConnectionListener;
import com.payby.pos.ecr.internal.processor.Processor;
import com.uaepay.pos.ecr.Ecr;
import com.uaepay.pos.ecr.acquire.Acquire;
import com.uaepay.pos.ecr.acquire.Void;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class VoidActivity extends BaseActivity {

    private CheckBox ckMerchant;
    private CheckBox ckCustomer;
    private TextView textReceive;
    private EditText editTextOrderNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_void_layout);
        initView();
    }

    private void initView() {
        ckCustomer = findViewById(R.id.widget_customer);
        ckMerchant = findViewById(R.id.widget_merchant);
        textReceive = findViewById(R.id.widget_txt_receive);
        editTextOrderNo = findViewById(R.id.edit_input_order_no);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.widget_scan_icon).setOnClickListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ClassicBTManager.getInstance().removeListener(connectionListener);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_ok) {
            doVoid();
        } else if (view.getId() == R.id.widget_scan_icon) {
            doScan();
        }
    }
    private void doScan() {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setViewType(1).setErrorCheck(true).create();
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options);
    }
    private void doVoid() {
        String acquireOrderNo = editTextOrderNo.getText().toString();
        if (acquireOrderNo == null || acquireOrderNo.length() == 0) {
            showToast("Please input order no");
            return;
        }

        WaitDialog.show("Loading...");
        textReceive.setText("Receive:\n");

        List<Acquire.Receipt> list = new ArrayList<>();
        boolean checked = ckMerchant.isChecked();
        if (checked) {
            list.add(Acquire.Receipt.MERCHANT_RECEIPT);
        }
        checked = ckCustomer.isChecked();
        if (checked) {
            list.add(Acquire.Receipt.CUSTOMER_RECEIPT);
        }
        Void.VoidRequest voidRequest = Void.VoidRequest.newBuilder().setAcquireOrderNo(acquireOrderNo).addAllPrintReceipts(list).build();
        Any body = Any.pack(voidRequest);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Request request = Ecr.Request.newBuilder().setMessageId(2).setTimestamp(timestamp).setServiceName(Processor.VOID_PLACE_ORDER).setBody(body).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build();
        byte[] byteArray = envelope.toByteArray();
        ConnectionKernel.getInstance().send(byteArray);
    }

    private final ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void onConnected() {

        }

        @Override
        public void onDisconnected() {
            runOnUiThread(WaitDialog::dismiss);
        }

        @Override
        public void onMessage(byte[] bytes) {
            runOnUiThread(WaitDialog::dismiss);
            String message = parser(bytes);
            runOnUiThread(
                () -> {
                    String string = textReceive.getText().toString();
                    textReceive.setText(string + "\n" + message);
                }
            );
        }

    };
    private int REQUEST_CODE_SCAN_ONE = 0x0101;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode!= RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            int errorCode = data.getIntExtra(ScanUtil.RESULT_CODE, ScanUtil.SUCCESS);
            if (errorCode == ScanUtil.SUCCESS) {
                HmsScan scan = data.getParcelableExtra(ScanUtil.RESULT);
                if (scan != null) {
                    // 展示扫码结果
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            EditText editText =  findViewById(R.id.edit_input_order_no);
                            editText.setText(scan.originalValue.toString());
                        }
                    });
                }
            }
        }
    }

    @Override
    public void onReceiveMessage(byte[] bytes) {
        super.onReceiveMessage(bytes);
        runOnUiThread(WaitDialog::dismiss);
        try {
            Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.parseFrom(bytes);
            Ecr.Response response = envelope.getResponse();
            String s = parserResponse(response);
            showToast(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}