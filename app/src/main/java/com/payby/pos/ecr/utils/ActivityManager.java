package com.payby.pos.ecr.utils;

import android.app.Activity;

import java.util.LinkedList;

public class ActivityManager {

    public static final LinkedList<Activity> activityList = new LinkedList<>();

    public static void pushActivity(Activity activity) {
        boolean contains = activityList.contains(activity);
        if (contains) {
            if (activityList.getLast() != activity) {
                activityList.remove(activity);
                activityList.add(activity);
            }
        } else {
            activityList.add(activity);
        }
    }

    public static void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    public static void finishActivity(Activity activity) {
        if (activity.isFinishing() == false) {
            activity.finish();
        }
        activityList.remove(activity);
    }

    public static void finishAll() {
        for (Activity activity : activityList) {
            if (activity.isFinishing() == false) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    public static Activity getTopActivity(){
      return activityList.getLast();
    }

}
