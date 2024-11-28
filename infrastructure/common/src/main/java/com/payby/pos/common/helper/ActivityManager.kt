package com.payby.pos.common.helper

import android.app.Activity
import java.util.LinkedList

object ActivityManager {

    private val activityList = LinkedList<Activity>()

    /**
     * push the specified [activity] into the list
     */
    fun pushActivity(activity: Activity) {
        val contains = activityList.contains(activity)
        if (contains) {
            if (activityList.last != activity) {
                activityList.remove(activity)
                activityList.add(activity)
            }
        } else {
            activityList.add(activity)
        }
    }

    /**
     * pop the specified [activity] into the list
     */
    fun popActivity(activity: Activity) {
        activityList.remove(activity)
    }

    fun finishActivity(activity: Activity) {
        activityList.remove(activity)
        activity.finish()
    }

    fun finishAllActivities(packageName: String) {
        val iterator = activityList.iterator()
        var has = iterator.hasNext()
        while (has) {
            val activity = iterator.next()
            val bool = activity.javaClass.name.startsWith(packageName)
            if (bool) {
                iterator.remove()
                activity.finish()
            }
            has = iterator.hasNext()
        }
    }

    fun finishAllActivities() {
        for (activity in activityList) {
            activity.finish()
        }
        activityList.clear()
    }

}