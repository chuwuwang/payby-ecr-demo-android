package com.payby.pos.ecr.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.payby.pos.ecr.R;

public class BLEConnectDialog extends Dialog {

    public static final String TAG = "BLEConnectDialog";

    public BLEConnectDialog(@NonNull Context context) {
        super(context, R.style.IosDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_ble_connect_layout, null);
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
    }

}
