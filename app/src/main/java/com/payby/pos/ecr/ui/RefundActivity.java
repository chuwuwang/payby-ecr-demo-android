package com.payby.pos.ecr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.internal.processor.Processor;
import com.uaepay.pos.ecr.Ecr;
import com.uaepay.pos.ecr.acquire.Acquire;
import com.uaepay.pos.ecr.acquire.Refund;
import com.uaepay.pos.ecr.common.Common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RefundActivity extends BaseActivity {

    private CheckBox ckMerchant;
    private CheckBox ckCustomer;
    private EditText editTextAmount;
    private EditText editTextOrderNo;
    private EditText editTextMerchantID;

    private TextView textReceive;
    private int scanType = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refund_layout);
        initView();
    }

    protected void initView() {
        ckCustomer = findViewById(R.id.widget_customer);
        ckMerchant = findViewById(R.id.widget_merchant);
        textReceive = findViewById(R.id.widget_txt_receive);
        editTextAmount = findViewById(R.id.edit_input_money);
        editTextOrderNo = findViewById(R.id.edit_input_order_no);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.widget_scan_icon).setOnClickListener(this);
        findViewById(R.id.widget_scan_icon_original_refund).setOnClickListener(this);
        editTextMerchantID = findViewById(R.id.edit_input_original_merchant_order_no_refund);
        InputFilter inputFilter = (source, start, end, dest, dstart, dend) -> {
            String input = source.toString();
            if (input.isEmpty() || input.matches("[A-Za-z0-9]*")) {
                return null; // 接受输入
            }
            return "";
        };
        editTextMerchantID.setFilters(new InputFilter[]{inputFilter});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_ok) {
            doRefund();
        } else if (view.getId() == R.id.widget_scan_icon) {
            scanType = 1;
            doScan();
        } else if (view.getId() == R.id.widget_scan_icon_original_refund) {
            scanType = 2;
            doScan();
        }
    }

    private void doScan() {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE).setViewType(1).setErrorCheck(true).create();
        ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, options);
    }

    private void doRefund() {
        String amount = editTextAmount.getText().toString();
        if (amount == null || amount.length() == 0) {
            showToast("Please input amount");
            return;
        }
        String acquireOrderNo = editTextOrderNo.getText().toString();
        if (acquireOrderNo == null || acquireOrderNo.length() == 0) {
            showToast("Please input order no");
            WaitDialog.dismiss();
            return;
        }

        String merchantOrderNo = editTextMerchantID.getText().toString();
        WaitDialog.show("Loading...");
        textReceive.setText("Receive:\n");

        Long amountValue = Long.valueOf(amount);
        String refundAmount = longCent2String(amountValue);
        Common.Money money = Common.Money.newBuilder().setAmount(refundAmount).setCurrencyCode("AED").build();
        List<Acquire.Receipt> list = new ArrayList<>();
        boolean checked = ckMerchant.isChecked();
        if (checked) {
            list.add(Acquire.Receipt.MERCHANT_RECEIPT);
        }
        checked = ckCustomer.isChecked();
        if (checked) {
            list.add(Acquire.Receipt.MERCHANT_RECEIPT);
        }

        Refund.RefundRequest refundRequest = Refund.RefundRequest.newBuilder()
                .setRefundAmount(money)
                .setAcquireOrderNo(acquireOrderNo)
                .setRefundMerchantOrderNo(merchantOrderNo)
                .addAllPrintReceipts(list)
                .build();
        Any body = Any.pack(refundRequest);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Request request = Ecr.Request.newBuilder().setMessageId(3).setTimestamp(timestamp).setServiceName(Processor.REFUND_PLACE_ORDER).setBody(body).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build();
        byte[] byteArray = envelope.toByteArray();
        ConnectionKernel.getInstance().send(byteArray);
    }



    private String longCent2String(long amount) {
        BigDecimal decimal = new BigDecimal(amount);
        BigDecimal bigDecimal = new BigDecimal("100");
        double doubleValue = decimal.divide(bigDecimal).doubleValue();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
        return decimalFormat.format(doubleValue);
    }

    private long string2LongCent(String amount) {
        BigDecimal decimal = new BigDecimal(amount);
        BigDecimal bigDecimal = new BigDecimal("100");
        return decimal.multiply(bigDecimal).longValue();
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
                    // 展示扫码结果
                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           if (scanType == 1) {
                               EditText editText =  findViewById(R.id.edit_input_order_no);
                               editText.setText(scan.originalValue.toString());
                           } else if (scanType == 2){
                                 // scanType == 2
                                 EditText editText =  findViewById(R.id.edit_input_original_merchant_order_no_refund);
                                 editText.setText(scan.originalValue.toString());
                           }
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textReceive.setText(s);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
