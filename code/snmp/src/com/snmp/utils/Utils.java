package com.snmp.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

public class Utils {
    private static final String TAG = "Utils";
    public static final long SATOSHIS_PER_BITCOIN = 100000000L;
    private static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols() {
        private static final long serialVersionUID = 1L;
        {
            setDecimalSeparator('.');
        }
    };
    public static DecimalFormat decimalFormat = new DecimalFormat("#.########", otherSymbols);
    private static int mDisplayWidth = 1080;
    private static int mDisplayHeight = 1920;
    private static int mRealDisplayHeight = 1920;
    private static String sOriginalImei = null;

    public static int getDisplayWidth() {
        return mDisplayWidth;
    }

    public static void setDisplayWidth(int w) {
        mDisplayWidth = w;
    }

    public static int getDisplayHeight() {
        return mDisplayHeight;
    }

    public static void setDisplayHeight(int h) {
        mDisplayHeight = h;
    }

    public static int getRealDisplayHeight() {
        return mRealDisplayHeight;
    }

    public static void setRealDisplayHeight(int h) {
        mRealDisplayHeight = h;
    }

    public static LayoutInflater getInflater() {
        return LayoutInflater.from(SnmpApplication.getInstance());
    }

    public static View inflate(int layoutId) {
        return LayoutInflater.from(SnmpApplication.getInstance()).inflate(layoutId, null);
    }

    public static String getAppVersion() {
        try {
            String packageName = SnmpApplication.getInstance().getPackageName();
            String versionName = getPackageInfo(packageName).versionName;
            versionName = versionName.toLowerCase(Locale.US);
            if (versionName.startsWith("v")) {
                versionName = versionName.substring(1);
            }
            return versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
        // return "1.0.0.a";
    }

    public static String swapEnd(String instring) {
        if (instring.length() < 4) {
            return instring;
        }

        char end1 = instring.charAt(instring.length() - 4);
        char end2 = instring.charAt(instring.length() - 3);
        char end3 = instring.charAt(instring.length() - 2);
        char end4 = instring.charAt(instring.length() - 1);
        String rawEnd = String.valueOf(end1) + String.valueOf(end2) + String.valueOf(end3) + String.valueOf(end4);
        String swapEnd = String.valueOf(end1) + String.valueOf(end2) + String.valueOf(end4) + String.valueOf(end3);
        return instring.replaceAll(rawEnd, swapEnd);
    }

    private static PackageInfo getPackageInfo(String packageName) throws NameNotFoundException {
        return SnmpApplication.getInstance().getPackageManager().getPackageInfo(packageName, 0);
    }

    private static Object getSystemService(String service) {
        return SnmpApplication.getInstance().getSystemService(service);
    }

    public static boolean hasNetwork() {
        return getActiveNetworkInfo() != null;
    }

    private static NetworkInfo getActiveNetworkInfo() {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            return connectivity.getActiveNetworkInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getBalance(double balance) {
        double total = (double) balance / SATOSHIS_PER_BITCOIN;
        return decimalFormat.format(total);
    }

    public static String getImei() {
        if (sOriginalImei == null) {
            try {
                TelephonyManager tm = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
                String deviceId = tm.getDeviceId();
                sOriginalImei = deviceId;
                if (TextUtils.isEmpty(sOriginalImei)) {
                    sOriginalImei = "000000000000000";
                }
                LogUtils.d(TAG, "get device imei:" + sOriginalImei);
            } catch (Exception e) {
                sOriginalImei = "000000000000000";
                LogUtils.e(TAG, "getImei " + e);
            }
        }
        return sOriginalImei;
    }

    public static void toast(final String str) {
        SnmpApplication.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SnmpApplication.getInstance(), str, Toast.LENGTH_LONG).show();
            }
        });

    }

}
