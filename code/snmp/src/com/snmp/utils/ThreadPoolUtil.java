package com.snmp.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import android.os.Message;
import android.util.Log;


public class ThreadPoolUtil {
    private static final String TAG = "ThreadPoolUtil";
    private static volatile ExecutorService sThreadPool;

    private static ExecutorService getThreadPool() {
        if (sThreadPool == null) {
            sThreadPool = Executors.newCachedThreadPool();
        }
        return sThreadPool;
    }

    public static void post(Runnable task) {
        try {
            getThreadPool().execute(task);
        } catch (RejectedExecutionException e) {
            Log.e(TAG, "ThreadPool name = " + task.getClass() + e);
        }
    }

    public static void postDelayed(final Runnable task, long delayMillis) {
        Message msg = Message.obtain();
        msg.obj = task;
        SnmpApplication.getInstance().mMainHandler.sendMessageDelayed(msg,
                delayMillis);
    }

    public static void removeCallbacks(Runnable task) {
        SnmpApplication.getInstance().mMainHandler
                .removeCallbacksAndMessages(task);
    }

}
