package com.snmp.widget;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.RemoteViews;

import com.snmp.api.price.PriceMgr;
import com.snmp.crypto.R;
import com.snmp.utils.LogUtils;
import com.snmp.watch.WatchActivity;

public class WidgetTimeService extends Service implements PriceMgr.onCallback {
    private static String TAG = "WidgetTimeService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtils.i(TAG, "onCreate");
        startClockListener();
        updateAppClockWidget();
        updateAppWatchWidget("0\n0");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(this.mTimeReceiver);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    private BroadcastReceiver mTimeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            LogUtils.i(TAG, "onReceive intent.getAction()" + intent.getAction());
            if ("android.intent.action.TIMEZONE_CHANGED".equals(intent.getAction())
                    || "android.intent.action.TIME_TICK".equals(intent.getAction())
                    || "android.intent.action.TIME_CHANGED".equals(intent.getAction())
                    || "android.intent.action.TIME_SET".equals(intent.getAction())) {

                PriceMgr.getInstance().postApi(WidgetTimeService.this);
                updateAppClockWidget();
            }

        }
    };

    public void startClockListener() {
        LogUtils.i(TAG, "startClockListener");
        IntentFilter localIntentFilter = new IntentFilter();
        localIntentFilter.addAction("android.intent.action.TIME_TICK");
        localIntentFilter.addAction("android.intent.action.TIME_CHANGED");
        localIntentFilter.addAction("android.intent.action.TIMEZONE_CHANGED");
        localIntentFilter.addAction("android.intent.action.TIME_SET");
        localIntentFilter.addAction("android.intent.action.SCREEN_ON");
        registerReceiver(this.mTimeReceiver, localIntentFilter);

    }

    public void updateAppWatchWidget(String price) {
        LogUtils.i(TAG, "updateAppWatchWidget " + price);
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.appwidget_provider_watch);

        remoteViews.setTextViewText(R.id.appwidget_text_watch, price);

        remoteViews.setOnClickPendingIntent(R.id.appwidget_watch_view,
                PendingIntent.getActivity(this, 0, new Intent(this, WatchActivity.class), 201326592));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName componentName = new ComponentName(getApplicationContext(), WatchAppWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    public void updateAppClockWidget() {
        LogUtils.i(TAG, "updateAppClockWidget");
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.appwidget_provider_clock);

        String string = getString(R.string.full_wday_month_day_no_year);
        remoteViews.setCharSequence(R.id.appwidget_text_day_week_mfvclr, "setFormat12Hour", string);
        remoteViews.setCharSequence(R.id.appwidget_text_day_week_mfvclr, "setFormat24Hour", string);

        remoteViews.setOnClickPendingIntent(R.id.appwidget_view,
                PendingIntent.getActivity(this, 0, new Intent(this, WatchActivity.class), 201326592));

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        ComponentName componentName = new ComponentName(getApplicationContext(), ClockAppWidgetProvider.class);
        appWidgetManager.updateAppWidget(componentName, remoteViews);
    }

    @Override
    public void onPriceResponce() {
        updateAppWatchWidget(PriceMgr.getInstance().getShow());
    }


}