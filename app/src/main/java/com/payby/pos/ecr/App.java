package com.payby.pos.ecr;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.toast.Toaster;
import com.payby.pos.ecr.utils.ActivityManager;

public class App extends Application {

  public static App instance;
  @org.jetbrains.annotations.Nullable
  public static final String TAG = "ECR";

  @Override
  public void onCreate() {
    super.onCreate();
    instance = this;
    Toaster.init(this);
    registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
  }

  private final ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
      ActivityManager.pushActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
      ActivityManager.popActivity(activity);
    }

  };

  public static void showToast(String message) {
    ActivityManager.getTopActivity().runOnUiThread(
        () -> Toaster.show(message)
    );
  }

}
