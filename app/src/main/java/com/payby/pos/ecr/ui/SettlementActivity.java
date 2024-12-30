package com.payby.pos.ecr.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.google.protobuf.Timestamp;
import com.kongzue.dialogx.dialogs.WaitDialog;
import com.payby.pos.ecr.R;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.internal.processor.Processor;
import com.payby.pos.ecr.utils.ThreadPoolManager;
import com.uaepay.pos.ecr.Ecr;

public class SettlementActivity extends BaseActivity {

    private EditText editInputOperatorID;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settlement);
        initView();
    }

    protected void initView() {
       findViewById(R.id.settlement).setOnClickListener(this);
        editInputOperatorID = findViewById(R.id.edit_input_operator_id);
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.getDeviceInfo) {
            String operatorId = editInputOperatorID.getText().toString();
            if (TextUtils.isEmpty(operatorId)) {
                showToast("Please input operatorId");
                return;
            }
            startSettlement();
        }
    }
   private void startSettlement() {
       Timestamp timestamp = Timestamp.newBuilder()
               .setSeconds(System.currentTimeMillis() / 1000)
               .build();
       Ecr.Request request = Ecr.Request.newBuilder()
               .setMessageId(4)
               .setTimestamp(timestamp)
               .setServiceName(Processor.SETTLEMENT_CLOSE)
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
    protected void onDestroy() {
        super.onDestroy();
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