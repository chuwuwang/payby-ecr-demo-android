package com.payby.pos.ecr.ui;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.protobuf.Any;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;
import com.hjq.toast.Toaster;
import com.payby.pos.ecr.connect.ConnectionKernel;
import com.payby.pos.ecr.connect.ConnectionListener;
import com.payby.pos.ecr.internal.processor.Processor;
import com.uaepay.pos.ecr.Ecr;
import com.uaepay.pos.ecr.acquire.Acquire;
import com.uaepay.pos.ecr.acquire.Device;
import com.uaepay.pos.ecr.acquire.Refund;
import com.uaepay.pos.ecr.acquire.Settlement;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    public void showToast(String message) {
        runOnUiThread(() -> Toaster.show(message));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionKernel.getInstance().addListener(connectionListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectionKernel.getInstance().removeListener(connectionListener);
    }

    private final ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void onConnected() {
            onDeviceConnected();
        }

        @Override
        public void onDisconnected() {
            onDeviceDisconnected();
        }

        @Override
        public void onMessage(byte[] bytes) {
            onReceiveMessage(bytes);
        }

    };

    public void onDeviceConnected() {

    }

    public void onDeviceDisconnected() {

    }

    public void onReceiveMessage(byte[] bytes) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public String parserResponse(Ecr.Response response) {
        try {
            return response(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "message parser error";
    }

    public String parser(byte[] bytes) {
        try {
            Ecr.EcrEnvelope envelope = Ecr.EcrEnvelope.parseFrom(bytes);
            Ecr.EcrEnvelope.ContentCase contentCase = envelope.getContentCase();
            if (contentCase == Ecr.EcrEnvelope.ContentCase.RESPONSE) {
                return response(envelope.getResponse());
            } else if (contentCase == Ecr.EcrEnvelope.ContentCase.REQUEST) {
                return request(envelope);
            } else if (contentCase == Ecr.EcrEnvelope.ContentCase.EVENT) {
                return event(envelope);
            } else if (contentCase == Ecr.EcrEnvelope.ContentCase.PING) {

            } else if (contentCase == Ecr.EcrEnvelope.ContentCase.PONG) {

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "message parser error";
    }

    private String response(Ecr.Response response) throws Exception {
        int messageId = response.getMessageId();
        String serviceName = response.getServiceName();
        String responseCode = response.getResponseCode();
        String errorMessage = response.getErrorMessage();
        long timestamp = response.getTimestamp().getSeconds();
        String subResponseCode = response.getSubResponseCode();
        String bodyString = "";
        Any body = response.getBody();
        StringBuilder builder = new StringBuilder();
        builder.append("RESPONSE").append("\n");
        builder.append("messageId: ").append(messageId).append("\n");
        builder.append("timestamp: ").append(timestamp).append("\n");
        builder.append("serviceName: ").append(serviceName).append("\n");
        builder.append("responseCode: ").append(responseCode).append("\n");
        builder.append("subResponseCode: ").append(subResponseCode).append("\n");
        builder.append("errorMessage: ").append(errorMessage).append("\n");

        switch (serviceName) {
            case Processor.ACQUIRE_PLACE_ORDER: // purchase
                MessageOrBuilder message = body.unpack(Acquire.AcquireOrder.class);
                bodyString = JsonFormat.printer().print(message);
                break;
            case Processor.ACQUIRE_GET_ORDER:   // inquiry acquire order
                message = body.unpack(Acquire.AcquireOrder.class);
                bodyString = JsonFormat.printer().print(message);
                break;
            case Processor.ACQUIRE_PRINT_RECEIPTS: //
                break;
            case Processor.ACQUIRE_NOTIFICATION: //
                break;
            case Processor.VOID_PLACE_ORDER: //
                break;

            case Processor.REFUND_PLACE_ORDER: // refund
                message = body.unpack(Refund.RefundOrder.class);
                bodyString = JsonFormat.printer().print(message);
                break;
            case Processor.REFUND_GET_ORDER:   // inquiry refund order
                message = body.unpack(Refund.RefundOrder.class);
                bodyString = JsonFormat.printer().print(message);
                break;
            case Processor.REFUND_PRINT_RECEIPTS: // get refund receipt data
                break;
            case Processor.SETTLEMENT_CLOSE: //
                message = body.unpack(Settlement.TransactionReport.class);
                bodyString = JsonFormat.printer().print(message);
                break;
            case Processor.DEVICE_GET_THIS: //
                message = body.unpack(Device.DeviceInfo.class);
                bodyString = JsonFormat.printer().print(message);
                break;
        }
        builder.append("body: ").append(bodyString).append("\n");
        return builder.toString();
    }

    private String request(Ecr.EcrEnvelope envelope) throws Exception {
        Ecr.Request request = envelope.getRequest();
        int messageId = request.getMessageId();
        String serviceName = request.getServiceName();
        long timestamp = request.getTimestamp().getSeconds();
        Any body = request.getBody();
        MessageOrBuilder message = envelope;
        StringBuilder builder = new StringBuilder();
        builder.append("REQUEST").append("\n");
        builder.append("messageId: ").append(messageId).append("\n");
        builder.append("timestamp: ").append(timestamp).append("\n");
        builder.append("serviceName: ").append(serviceName).append("\n");
        switch (serviceName) {
            case "/acquire/notification":
                message = body.unpack(Acquire.AcquireOrder.class);
                break;
        }
        String bodyString = JsonFormat.printer().print(message);
        builder.append("body: ").append(bodyString).append("\n");
        return builder.toString();
    }

    private String event(Ecr.EcrEnvelope envelope) throws Exception {
        Ecr.Event event = envelope.getEvent();
        String serviceName = event.getServiceName();
        long timestamp = event.getTimestamp().getSeconds();
        Any body = event.getBody();
        MessageOrBuilder message = envelope;
        StringBuilder builder = new StringBuilder();
        builder.append("EVENT").append("\n");
        builder.append("timestamp: ").append(timestamp).append("\n");
        builder.append("serviceName: ").append(serviceName).append("\n");
        switch (serviceName) {
            case "/acquire/notification":
                message = body.unpack(Acquire.AcquireOrder.class);
                break;
        }
        String bodyString = JsonFormat.printer().print(message);
        builder.append("body: ").append(bodyString).append("\n");
        return builder.toString();
    }

}
