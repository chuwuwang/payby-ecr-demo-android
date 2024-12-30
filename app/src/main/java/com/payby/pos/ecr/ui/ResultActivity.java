package com.payby.pos.ecr.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import com.payby.pos.ecr.R;

public class ResultActivity extends BaseActivity{

    private TextView textReceive;
    public static void start(Activity activity, String receive) {
        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra("receive", receive);
        activity.startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_result);
        String receive = getIntent().getStringExtra("receive");
        textReceive = findViewById(R.id.receive);
        textReceive.setText(receive);
    }



}
