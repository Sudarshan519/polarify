package com.example.iss_sdk.tool;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * Created by xuesong on 2022/10/11
 * Description:
 */
public class ActivityManager {

    private WeakReference<Activity> sCurrentActivityWeakRef;


    private static volatile ActivityManager singleton;
//
//    private ActivityManager () {
//    }

    public static ActivityManager getInstance() {
        if (singleton == null) {
            synchronized (ActivityManager.class) {
                if (singleton == null) {
                    singleton = new ActivityManager();
                }
            }
        }
        return singleton;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }
}
