package com.payby.pos.ecr.inapp;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.payby.pos.ecr.internal.InAppCallback;
import com.payby.pos.ecr.internal.InAppServiceEngine;
import com.payby.pos.ecr.sdk.BuildConfig;


public class InAppManager {
    private Context mContext;
    private String packageName;
    private int count = 0;
    private InAppServiceEngine inAppServiceEngine;
    private ConnectCallback connectCallback = null;
    private static final String TAG = "InApp-Client";

    private InAppManager() {
    }

    private static InAppManager instance;

    public static InAppManager getInstance() {
        if (instance == null) {
            instance = new InAppManager();
        }
        return instance;
    }

    public void bindInAppService (Context context) {
        mContext = context;
        if (BuildConfig.BUILD_TYPE == "release") {
            packageName = "com.pay" + "by.pos.acquirer";
        } else {
            packageName = "com.pay" + "by.pos.acquirer.uat";
        }
        boolean isAppExist = isAppExist(context, packageName);
        if (isAppExist == false) {
            Toast.makeText(context, "PayBy POS is not installed", Toast.LENGTH_SHORT).show();
            return;
        }
        bindService();
        handler.removeCallbacksAndMessages(null);
        handler.sendEmptyMessageDelayed(0, 2000);
    }
    private void  bindService() {
        Intent intent = new Intent();
        intent.setPackage(packageName);
        intent.setAction("com.pay" + "by.pos.ecr.ACTION_IN" + "APP_BRIDGE");
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        try {
            if (mContext == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(intent);
            } else {
                mContext.startService(intent);
            }
            mContext.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        } catch (Throwable e){
            e.printStackTrace();
        }
    }
    public void setConnectCallback(ConnectCallback callback) {
        connectCallback = callback;
    }
    public  void register(InAppCallback callback) {
        try {
            if (inAppServiceEngine != null) {
                inAppServiceEngine.register(callback);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public  void unregister() {
        try {
            if (inAppServiceEngine != null) {
                inAppServiceEngine.unregister();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            inAppServiceEngine = InAppServiceEngine.Stub.asInterface(service);
            if (connectCallback != null) {
                connectCallback.onConnect();
            }
            if (service != null) {
                linkToDeath(service);
            }
            handler.removeMessages(0);
            count = 0;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            inAppServiceEngine = null;
            handler.sendEmptyMessage(0);
        }
    };
    private  void linkToDeath(IBinder service) {
        try {
            service.linkToDeath(() -> {
                inAppServiceEngine = null;
                bindInAppService(mContext);
            }, 0);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public  void send(byte[] byteArray, InAppCallback callback)  {
        try {
            if (inAppServiceEngine != null) {
                inAppServiceEngine.send(byteArray, callback);
            }
        } catch (RemoteException e){
            e.printStackTrace();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            count++;
            removeMessages(0);
            if (count >= 5) {
                Toast.makeText(
                        mContext,
                        "Failed to connect to PayBy POS, please try again",
                        Toast.LENGTH_SHORT
                ).show();
            } else {
                bindService();
                sendEmptyMessageDelayed(0, 2000);
            }
        }
    };
    public boolean isConnected() {
        return inAppServiceEngine != null;
    }

      public boolean isAppExist (Context context, String packageName) {
            try {
              context.getPackageManager().getApplicationInfo(packageName, 0);
              return true;
            } catch (Exception e) {
              return false;
            }
      }

      public interface ConnectCallback {
          void onConnect();
      }
}
