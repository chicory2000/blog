package com.snmp.utils;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


public class SnmpApplication extends Application {
    private final String TAG = "SnmpApplication";
    private static SnmpApplication sInstance;
    private List<WeakReference<Activity>> mActivity = new CopyOnWriteArrayList<WeakReference<Activity>>();
    public Handler mMainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.obj instanceof Runnable) {
                ThreadPoolUtil.post((Runnable) msg.obj);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        LogUtils.i(TAG, "isMainProcess() = " + isMainProcess());
        PreferenceManager.init(this);
        if (isMainProcess()) {
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        finishAll();
    }

    @Override
    protected void finalize() throws Throwable {
        LogUtils.i(TAG, "finalize()");
        super.finalize();
    }

    public static SnmpApplication getInstance() {
        return sInstance;
    }

    public static void removeCallbacks(Runnable runnable) {
        sInstance.mMainHandler.removeCallbacks(runnable);
    }

    public static void post(Runnable runnable) {
        sInstance.mMainHandler.post(runnable);
    }

    public static void postDelayed(Runnable runnable, long delayMs) {
        sInstance.mMainHandler.postDelayed(runnable, delayMs);
    }

    public void addActivity(Activity activity) {
        mActivity.add(new WeakReference<Activity>(activity));
    }

    public void removeActivity(Activity activity) {
        Iterator<WeakReference<Activity>> iterator = mActivity.iterator();
        while (iterator.hasNext()) {
            WeakReference<Activity> a = iterator.next();
            if (a.get() == activity) {
                mActivity.remove(a);
                a.get().finish();
            }
        }
    }

    public void finishAll() {
        Iterator<WeakReference<Activity>> iterator = mActivity.iterator();
        while (iterator.hasNext()) {
            WeakReference<Activity> activity = iterator.next();
            mActivity.remove(activity);
            Activity realActivity = activity.get();
            if (realActivity != null) {
                realActivity.finish();
            }
        }
    }

    private boolean isMainProcess() {
        String processName = "";
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> allProcesses = activityManager
                .getRunningAppProcesses();
        for (RunningAppProcessInfo process : allProcesses) {
            if (process.pid == pid) {
                processName = process.processName;
                android.util.Log.e(TAG, "main process name = " + processName);
                break;
            }
        }
        return getMainProcessName().equals(processName);
    }

    private String getMainProcessName() {
        return "com.snmp";
    }
}
