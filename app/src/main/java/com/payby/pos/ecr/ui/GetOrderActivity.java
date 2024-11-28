package com.payby.pos.ecr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.payby.pos.ecr.bluetooth.ConnectionListener;
import com.payby.pos.ecr.connect.ConnectService;
import com.payby.pos.ecr.internal.processor.Processor;
import com.uaepay.pos.ecr.Ecr;
import com.uaepay.pos.ecr.acquire.Acquire;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class GetOrderActivity extends BaseActivity {

    private TextView textReceive;
    private EditText editTextOrderNo;

    private int type = -1;
    Processor processor = new Processor();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_order_layout);
        initView();
    }

    private void initView() {
        type = getIntent().getIntExtra("order_type", -1);
        TextView titleView = findViewById(R.id.widget_tv_title);
        if (type == 0) {
            titleView.setText("Get AcquireOrder");
        } else if (type == 1) {
            titleView.setText("Get RefundOrder");
        }
        textReceive = findViewById(R.id.widget_txt_receive);
        editTextOrderNo = findViewById(R.id.edit_input_order_no);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.widget_scan_icon).setOnClickListener(this);

      processor.setOnRefundGetOrderComplete(new Function1<Ecr.Response, Unit>() {
        @Override
        public Unit invoke(Ecr.Response response) {
          runOnUiThread(
              () -> {
                String s =parserResponse(response);
                String string = textReceive.getText().toString();
                textReceive.setText(string + "\n" + s);
                ResultActivity.Companion.start(GetOrderActivity.this,textReceive.getText().toString());

              }
          );
          return null;
        }
      });
      processor.setOnInquiryAcquireOrderComplete(new Function1<Ecr.Response, Unit>() {
        @Override
        public Unit invoke(Ecr.Response response) {
          runOnUiThread(
              () -> {
                String s =parserResponse(response);
                String string = textReceive.getText().toString();
                textReceive.setText(string + "\n" + s);
                ResultActivity.Companion.start(GetOrderActivity.this,textReceive.getText().toString());

              }
          );
          return null;
        }
      });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_ok) {
            getOrder();
        } else if (view.getId() == R.id.widget_scan_icon) {
            doScan();
        }
    }
    private void doScan() {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setViewType(1).setErrorCheck(true).create();
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options);
    }
    private void getOrder() {
        String acquireOrderNo = editTextOrderNo.getText().toString();
        if (acquireOrderNo == null || acquireOrderNo.length() == 0) {
            showToast("Please input order no");
            return;
        }

        WaitDialog.show("Loading...");
        textReceive.setText("Receive:\n");

        if (type == 0) {
            getAcquireOrder(acquireOrderNo);
        } else if (type == 1) {
            getRefund(acquireOrderNo);
        }
    }

    private void getRefund(String acquireOrderNo) {
        Acquire.OrderNoWrapper orderNoWrapper = Acquire.OrderNoWrapper.newBuilder().setOrderNo(acquireOrderNo).build();
        Any body = Any.pack(orderNoWrapper);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Request request = Ecr.Request.newBuilder().setMessageId(5).setTimestamp(timestamp).setServiceName(Processor.REFUND_GET_ORDER).setBody(body).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build();
        byte[] byteArray = envelope.toByteArray();
        ConnectService.INSTANCE.send(byteArray, bytes -> {
          runOnUiThread(WaitDialog::dismiss);
          processor.messageHandle(bytes);

          return null;
        });
    }

    private void getAcquireOrder(String acquireOrderNo) {
        Acquire.OrderNoWrapper orderNoWrapper = Acquire.OrderNoWrapper.newBuilder().setOrderNo(acquireOrderNo).build();
        Any body = Any.pack(orderNoWrapper);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Request request = Ecr.Request.newBuilder().setMessageId(4).setTimestamp(timestamp).setServiceName(Processor.ACQUIRE_GET_ORDER).setBody(body).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build();
        byte[] byteArray = envelope.toByteArray();
      ConnectService.INSTANCE.send(byteArray, bytes -> {
        runOnUiThread(WaitDialog::dismiss);
        processor.messageHandle(bytes);

        return null;
      });
    }


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
                    Log.e("测试", "结果微；"+scan.originalValue);
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
}
