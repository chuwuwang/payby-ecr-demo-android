package com.payby.pos.ecr.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.payby.pos.ecr.R;

public class CustomDialog extends Dialog {

    protected ImageView ivClose;
    protected TextView tvMessage;

    public CustomDialog(@NonNull Context context) {
        super(context, R.style.IosDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dailog_customer_layout, null);
        ivClose = view.findViewById(R.id.widget_iv_close);
        tvMessage = view.findViewById(R.id.widget_txt_receive_dg);
        ivClose.setOnClickListener(v -> dismiss());
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
        }
    }

    public CustomDialog setMessage(String message) {
        tvMessage.setText(message);
        return this;
    }

}
