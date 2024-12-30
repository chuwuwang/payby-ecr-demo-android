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
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectService;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.internal.processor.Processor;
import com.uaepay.pos.ecr.Ecr;
import com.uaepay.pos.ecr.acquire.Acquire;
import com.uaepay.pos.ecr.acquire.Receipt;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class ReceiptsActivity extends BaseActivity {

    private CheckBox ckMerchant;
    private CheckBox ckCustomer;
    private TextView textReceive;
    private EditText editTextOrderNo;

    private int type = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipts_layout);
        initView();
    }

    private void initView() {
        type = getIntent().getIntExtra("receipts_type", -1);
        TextView title = findViewById(R.id.widget_tv_title);
        TextView receiptType = findViewById(R.id.widget_txt_receipt_title);
        if (type == 0) {
            title.setText("Get AcquireOrder Receipts");
            receiptType.setText("Receipt Type (optional, select one type)");
        } else if (type == 1) {
            title.setText("Print AcquireOrder Receipts");
        } else if (type == 2) {
            title.setText("Get RefundOrder Receipts");
            receiptType.setText("Receipt Type (optional, select one type)");
        } else if (type == 3) {
            title.setText("Print RefundOrder Receipts");
        }
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
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_ok) {
            doReceipt();
        } else if (view.getId() == R.id.widget_scan_icon) {
            doScan();
        }
    }
    private void doScan() {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setViewType(1).setErrorCheck(true).create();
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options);
    }

    private void doReceipt() {
        String orderNo = editTextOrderNo.getText().toString();
        if (orderNo == null || orderNo.length() == 0) {
            showToast("Please input order no");
            return;
        }
        Any body = null;
        String serviceName = "";
        Receipt.ReceiptRequest receiptRequest;
      Receipt.ReceiptsRequest receiptsRequest;
        Log.e("测试", "type:" + type);
        boolean customerChecked = ckCustomer.isChecked();
        boolean merchantChecked = ckMerchant.isChecked();
        if (type == 0) {
            serviceName = Processor.ACQUIRE_GET_ORDER_RECEIPT;
            if (merchantChecked) {
                receiptRequest = Receipt.ReceiptRequest.newBuilder().setOrderNo(orderNo).setReceipt(Acquire.Receipt.MERCHANT_RECEIPT).build();
            } else if (customerChecked) {
                receiptRequest = Receipt.ReceiptRequest.newBuilder().setOrderNo(orderNo).setReceipt(Acquire.Receipt.CUSTOMER_RECEIPT).build();
            } else {
                receiptRequest = Receipt.ReceiptRequest.newBuilder().setOrderNo(orderNo).build();
            }
            body = Any.pack(receiptRequest);
        } else if (type == 1) {
            serviceName = Processor.ACQUIRE_PRINT_RECEIPTS ;
            List<Acquire.Receipt> list = new ArrayList<>();
            if (customerChecked) {
                list.add(Acquire.Receipt.CUSTOMER_RECEIPT);
            }
            if (merchantChecked) {
                list.add(Acquire.Receipt.MERCHANT_RECEIPT);
            }
            receiptsRequest = Receipt.ReceiptsRequest.newBuilder().setOrderNo(orderNo).addAllReceipts(list).build();
            body = Any.pack(receiptsRequest);
        } else if (type == 2) {
            serviceName = "/acquire/refund/receipt/get";
            if (merchantChecked) {
                receiptRequest = Receipt.ReceiptRequest.newBuilder().setOrderNo(orderNo).setReceipt(Acquire.Receipt.MERCHANT_RECEIPT).build();
            } else if (customerChecked) {
                receiptRequest = Receipt.ReceiptRequest.newBuilder().setOrderNo(orderNo).setReceipt(Acquire.Receipt.CUSTOMER_RECEIPT).build();
            } else {
                receiptRequest = Receipt.ReceiptRequest.newBuilder().setOrderNo(orderNo).build();
            }
            body = Any.pack(receiptRequest);
        } else if (type == 3) {
            serviceName = Processor.REFUND_PRINT_RECEIPTS ;
            List<Acquire.Receipt> list = new ArrayList<>();
            if (customerChecked) {
                list.add(Acquire.Receipt.CUSTOMER_RECEIPT);
            }
            if (merchantChecked) {
                list.add(Acquire.Receipt.MERCHANT_RECEIPT);
            }
            receiptsRequest = Receipt.ReceiptsRequest.newBuilder().setOrderNo(orderNo).addAllReceipts(list).build();
            body = Any.pack(receiptsRequest);
        }
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Request request = Ecr.Request.newBuilder().setMessageId(5).setTimestamp(timestamp).setServiceName(serviceName).setBody(body).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build();
        byte[] byteArray = envelope.toByteArray();
        ConnectionKernel.getInstance().send(byteArray);
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

    @Override
    public void onReceiveMessage(byte[] bytes) {
        super.onReceiveMessage(bytes);
        runOnUiThread(WaitDialog::dismiss);
        Log.e(App.TAG, "onReceiveMessage ---------------");
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
