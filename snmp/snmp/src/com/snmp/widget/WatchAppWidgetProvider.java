package com.snmp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.snmp.utils.LogUtils;
import com.snmp.crypto.R;
import com.snmp.watch.WatchActivity;

public class WatchAppWidgetProvider extends AppWidgetProvider {
    private static String TAG = "WatchAppWidgetProvider";
    private ComponentName mComponentName;

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int updateAppWidget) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_provider_watch);

        remoteViews.setTextViewText(R.id.appwidget_text_watch, "0\n0");

        remoteViews.setOnClickPendingIntent(R.id.appwidget_watch_view,
                PendingIntent.getActivity(context, 0, new Intent(context, WatchActivity.class), 201326592));

        appWidgetManager.updateAppWidget(updateAppWidget, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        LogUtils.i(TAG, "intent.getAction()" + intent.getAction());
        if (judgeAction(intent.getAction())) {
            updateAppWidgetManager(context);
        }
    }

    private void updateAppWidgetManager(Context context) {
        AppWidgetManager instance = AppWidgetManager.getInstance(context);
        if (instance != null) {
            for (int updateAppWidget : instance.getAppWidgetIds(getComponentName(context))) {
                updateAppWidget(context, instance, updateAppWidget);
            }
        }
    }

    private ComponentName getComponentName(Context context) {
        if (mComponentName == null) {
            mComponentName = new ComponentName(context, getClass());
        }
        return mComponentName;
    }

    private boolean judgeAction(String str) {
        return "android.intent.action.SCREEN_ON".equals(str) || "android.intent.action.DATE_CHANGED".equals(str)
                || "android.intent.action.TIMEZONE_CHANGED".equals(str) || "android.intent.action.TIME_SET".equals(str)
                || "android.intent.action.LOCALE_CHANGED".equals(str)
                || "android.intent.action.WALLPAPER_CHANGED".equals(str);
    }

    @Override
    public void onEnabled(Context context) {
        context.startService(new Intent(context, WidgetTimeService.class));
    }

    @Override
    public void onDisabled(Context context) {
        context.stopService(new Intent(context, WidgetTimeService.class));
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int updateAppWidget : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, updateAppWidget);
        }
    }

}