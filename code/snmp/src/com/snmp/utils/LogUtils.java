package com.snmp.utils;

import java.io.PrintWriter;

import android.text.TextUtils;
import android.util.Log;

public class LogUtils {
    private static final String TAG = "WindowManager";
    private static final int INVALID = -1;

    private static int CURRENT_LEVEL = Log.VERBOSE;

    private static void setCurrentLevel(int level, PrintWriter pw) {
        pw.println("log level: " + level);
        CURRENT_LEVEL = level;
    }

    public static void adjustCurrentLevel(String level, PrintWriter pw) {
        if (TextUtils.isEmpty(level)) {
            setCurrentLevel(Log.INFO, pw);
            return;
        }
        if (level.toUpperCase().startsWith("V")) {
            setCurrentLevel(Log.VERBOSE, pw);
        } else if (level.toUpperCase().startsWith("D")) {
            setCurrentLevel(Log.DEBUG, pw);
        } else if (level.toUpperCase().startsWith("W")) {
            setCurrentLevel(Log.WARN, pw);
        } else if (level.toUpperCase().startsWith("E")) {
            setCurrentLevel(Log.ERROR, pw);
        } else {
            setCurrentLevel(Log.INFO, pw);
        }
    }

    public static int v(String tag, String msg) {
        if (CURRENT_LEVEL <= Log.VERBOSE)
            return Log.v(TAG, "[" + tag + "] " + msg);
        return INVALID;
    }

    public static int v(String tag, String msg, Throwable throwable) {
        if (CURRENT_LEVEL <= Log.VERBOSE)
            return Log.v(TAG, "[" + tag + "] " + msg, throwable);
        return INVALID;
    }

    public static int d(String tag, String msg) {
        if (CURRENT_LEVEL <= Log.DEBUG)
            return Log.i(TAG, "[" + tag + "] " + msg);
        return INVALID;
    }

    public static int d(String tag, String msg, Throwable throwable) {
        if (CURRENT_LEVEL <= Log.DEBUG)
            return Log.i(TAG, "[" + tag + "] " + msg, throwable);
        return INVALID;
    }

    public static int i(String tag, String msg) {
        if (CURRENT_LEVEL <= Log.INFO)
            return Log.i(TAG, "[" + tag + "] " + msg);
        return INVALID;
    }

    public static int i(String tag, String msg, Throwable throwable) {
        if (CURRENT_LEVEL <= Log.INFO)
            return Log.i(TAG, "[" + tag + "] " + msg, throwable);
        return INVALID;
    }

    public static int w(String tag, String msg) {
        if (CURRENT_LEVEL <= Log.WARN)
            return Log.w(TAG, "[" + tag + "] " + msg);
        return INVALID;
    }

    public static int w(String tag, String msg, Throwable throwable) {
        if (CURRENT_LEVEL <= Log.WARN)
            return Log.w(TAG, "[" + tag + "] " + msg, throwable);
        return INVALID;
    }

    public static int e(String tag, String msg) {
        if (CURRENT_LEVEL <= Log.ERROR)
            return Log.e(TAG, "[" + tag + "] " + msg);
        return INVALID;
    }

    public static int e(String tag, String msg, Throwable throwable) {
        if (CURRENT_LEVEL <= Log.ERROR)
            return Log.e(TAG, "[" + tag + "] " + msg, throwable);
        return INVALID;
    }

    public static int debugv(String tag, String msg) {
        // if (CardService.USER_DEBUG)
        return Log.v(TAG, "[CardDebug " + tag + "] " + msg);
        // return INVALID;
    }
}
