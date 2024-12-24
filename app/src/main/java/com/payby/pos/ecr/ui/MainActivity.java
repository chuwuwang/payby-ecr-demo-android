package com.payby.pos.ecr.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.payby.pos.ecr.R;
import com.payby.pos.ecr.utils.Utils;

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

}
