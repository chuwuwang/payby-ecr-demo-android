package com.payby.pos.ecr.ui;

import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.hjq.toast.Toaster;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.App;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectType;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.internal.processor.Processor;
import com.uaepay.pos.ecr.Ecr;
import com.uaepay.pos.ecr.acquire.Acquire;
import com.uaepay.pos.ecr.common.Common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SaleActivity extends BaseActivity {

    private EditText editTextAmount;
    private EditText editTextSubject;
    private EditText editTextReserved;
    private CheckBox ckMerchant;
    private CheckBox ckCustomer;
    private RadioGroup groupSyncType;
    private RadioGroup groupNotification;

    private TextView textReceive;

    private int syncType = 0;
    private Set<Acquire.PaymentMethod> paymentMethods = new ArraySet<>();
    private int notificationType = -1;
    private CheckBox rbBankCard;
    private CheckBox rbCustomCode;
    private CheckBox rbScanCode;
    private CheckBox displayResultPage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_layout);
        initView();

    }

    private void initView() {
        editTextAmount = findViewById(R.id.edit_input_money);
        editTextSubject = findViewById(R.id.edit_input_subject);
        editTextReserved = findViewById(R.id.edit_input_reserved);

        groupSyncType = findViewById(R.id.radio_group_sync_type);
        groupNotification = findViewById(R.id.radio_group_notification_type);

        ckCustomer = findViewById(R.id.widget_customer);
        ckMerchant = findViewById(R.id.widget_merchant);
        textReceive = findViewById(R.id.widget_txt_receive);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        rbBankCard = (CheckBox) findViewById(R.id.rb_bank_card);
        rbCustomCode = (CheckBox) findViewById(R.id.rb_custom_code);
        rbScanCode = (CheckBox) findViewById(R.id.rb_scan_code);
        displayResultPage = (CheckBox) findViewById(R.id.display_result_page);
        if (ConnectionKernel.getInstance().getConnectType() == ConnectType.IN_APP) {
            displayResultPage.setChecked(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (view.getId() == R.id.btn_ok) {
            packSale();
        }
    }

    private void packSale() {
        String amount = editTextAmount.getText().toString();
        if (amount == null || amount.length() == 0) {
            showToast("Please input amount");
            return;
        }
        String subject = editTextSubject.getText().toString();
        if (rbBankCard.isChecked()) {
            paymentMethods.add(Acquire.PaymentMethod.BANKCARD);
        } else {
            paymentMethods.remove(Acquire.PaymentMethod.BANKCARD);
        }
        if (rbCustomCode.isChecked()) {
            paymentMethods.add(Acquire.PaymentMethod.CUSTOMER_PRESENT_CODE);
        } else {
            paymentMethods.remove(Acquire.PaymentMethod.CUSTOMER_PRESENT_CODE);
        }
        if (rbScanCode.isChecked()) {
            paymentMethods.add(Acquire.PaymentMethod.POS_PRESENT_CODE);
        } else {
            paymentMethods.remove(Acquire.PaymentMethod.POS_PRESENT_CODE);
        }

        int syncTypeId = groupSyncType.getCheckedRadioButtonId();
        if (syncTypeId == R.id.rb_sync_notification) {
            syncType = 0;
        } else {
            syncType = 1;
        }
        int notificationTypeId = groupNotification.getCheckedRadioButtonId();
        if (notificationTypeId == R.id.rb_notification_request) {
            notificationType = 0;
        } else if (notificationTypeId == R.id.rb_notification_event) {
            notificationType = 1;
        } else {
            notificationType = -1;
        }

        WaitDialog.show("Loading...");
        textReceive.setText("Receive:\n");

        Long amountValue = Long.valueOf(amount);
        String amountString = longCent2String(amountValue);
        Common.Money money = Common.Money.newBuilder().setAmount(amountString).setCurrencyCode("AED").build();
        Acquire.AcquiredResultNotification notification;
        if (notificationType == 0) {
            notification = Acquire.AcquiredResultNotification.REQUEST;
        } else {
            notification = Acquire.AcquiredResultNotification.EVENT;
        }
        List<Acquire.Receipt> list = new ArrayList<>();
        boolean checked = ckMerchant.isChecked();
        if (checked) {
            list.add(Acquire.Receipt.MERCHANT_RECEIPT);
        }
        checked = ckCustomer.isChecked();
        if (checked) {
            list.add(Acquire.Receipt.CUSTOMER_RECEIPT);
        }
        Acquire.CashierParams cashierParams = Acquire.CashierParams.newBuilder()
                .addAllPaymentMethods(paymentMethods)
                .addAllPrintReceipts(list)
                .setDisplayResultPage(displayResultPage.isChecked())
                .build();


        Acquire.PlaceOrderRequest placeOrderRequest = Acquire.PlaceOrderRequest.newBuilder()
                .setAmount(money)
                .setReserved(editTextReserved.getText().toString())
                .setSubject(subject)
                .setCashierParams(cashierParams)
                .setInvokeParams(Acquire.InvokeParams
                        .newBuilder().setNotification(notification)
                        .setInvokeType(syncType == 0 ? Acquire.InvokeType.SYNCHRONIZATION : Acquire.InvokeType.ASYNCHRONIZATION)
                        .build()
                )
                .build();

        Any body = Any.pack(placeOrderRequest);
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Request request = Ecr.Request.newBuilder().setMessageId(1).setTimestamp(timestamp).setServiceName(Processor.ACQUIRE_PLACE_ORDER).setBody(body).build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setRequest(request).build();
        byte[] byteArray = envelope.toByteArray();
        ConnectionKernel.getInstance().send(byteArray);
    }

    private void parseEnvelopeRequest(byte[] bytes) {
        try {
            Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.parseFrom(bytes);
            Ecr.EcrEnvelope.ContentCase contentCase = envelope.getContentCase();
            if (contentCase == Ecr.EcrEnvelope.ContentCase.REQUEST) {
                Ecr.Request request = envelope.getRequest();
                String serviceName = request.getServiceName();
                switch (serviceName) {
                    case Processor.ACQUIRE_NOTIFICATION:
                        sendNotification();
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendNotification() {
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(System.currentTimeMillis() / 1000).build();
        Ecr.Response response = Ecr.Response.newBuilder()
                .setMessageId(6)
                .setTimestamp(timestamp)
                .setServiceName(Processor.ACQUIRE_NOTIFICATION)
                .setResponseCode("SUCCESS")
                .build();
        Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.newBuilder().setVersion(1).setResponse(response).build();
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

    @Override
    public void onReceiveMessage(byte[] bytes) {
        super.onReceiveMessage(bytes);
        Log.e(App.TAG, "onReceiveMessage ---------------");
        runOnUiThread(WaitDialog::dismiss);
        try {
            Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.parseFrom(bytes);
            Ecr.Response response = envelope.getResponse();
            String s = parserResponse(response);
            runOnUiThread(
                    () -> textReceive.setText(s)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
