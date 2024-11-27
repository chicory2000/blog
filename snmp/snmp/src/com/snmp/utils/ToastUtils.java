package com.snmp.utils;

import android.widget.Toast;


public class ToastUtils {
    private static final String TAG = "ToastUtils";
    private static long sLastClickTime = 0;

    private static String getString(int resId) {
        return SnmpApplication.getInstance().getString(resId);
    }

    public static void showLimited(int resId) {
        showLimited(getString(resId));
    }

    public static void showLimited(String str) {
        long curTime = System.currentTimeMillis();
        if (Math.abs(curTime - sLastClickTime) > 2 * 1000) {
            sLastClickTime = curTime;
            show(str);
        }
    }

    public static void show(int resId) {
        show(getString(resId));
    }

    public static void show(final String str) {
    	SnmpApplication.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Toast.makeText(SnmpApplication.getInstance(), str, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    LogUtils.e(TAG, e.getLocalizedMessage(), e);
                }
            }
        });
    }
}
