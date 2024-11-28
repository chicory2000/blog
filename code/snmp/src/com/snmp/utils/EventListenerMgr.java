package com.snmp.utils;

import java.util.ArrayList;

import android.util.SparseArray;

public class EventListenerMgr {
    public static final int EVENT_BASE = 0x1;
    public static final int EVENT_UI_UPDATE = EVENT_BASE + 1;
    public static final int EVENT_COLD_UI_UPDATE = EVENT_UI_UPDATE + 1;

    private static SparseArray<ArrayList<EventListener>> sListeners = new SparseArray<ArrayList<EventListener>>();
    private static SparseArray<ArrayList<EventListener>> sStaticListeners = new SparseArray<ArrayList<EventListener>>();

    public static synchronized void addListener(EventListener listener,
            int... keys) {
        for (int key : keys) {
            addListener(listener, key);
        }
    }

    public static synchronized void addListener(EventListener listener, int key) {
        add(listener, key, sListeners);
    }

    public static synchronized void addStaticListener(EventListener listener,
            int... keys) {
        for (int key : keys) {
            addStaticListener(listener, key);
        }
    }

    public static synchronized void addStaticListener(EventListener listener,
            int key) {
        add(listener, key, sStaticListeners);
    }

    public static synchronized void removeListener(EventListener listener) {
        remove(listener, sListeners);
        remove(listener, sStaticListeners);
    }

    private static void add(EventListener listener, int key,
            SparseArray<ArrayList<EventListener>> listeners) {
        if (listener == null) { // notify
            return;
        }
        ArrayList<EventListener> list = listeners.get(key);
        if (list != null) {
            if (!list.contains(listener)) {
                list.add(listener);
            }
        } else {
            list = new ArrayList<EventListener>();
            list.add(listener);
            listeners.put(key, list);
        }
    }

    private static void remove(EventListener listener,
            SparseArray<ArrayList<EventListener>> listeners) {
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            ArrayList<EventListener> list = listeners.valueAt(i);
            list.remove(listener);
        }
    }

    public static synchronized void onEvent(int key) {
        onEvent(key, new Object());
    }

    public static synchronized void onEvent(int key, Object... params) {
        onEvent(key, sListeners.get(key), params);
        onEvent(key, sStaticListeners.get(key), params);
    }

    private static void onEvent(int key, ArrayList<EventListener> target,
            Object... params) {

        if (target == null) {
            return;
        }
        for (EventListener listener : target) {
            listener.onEvent(key, params);
        }
    }

    public static synchronized void exit() {
        recycle(sListeners);
    }

    private static void recycle(SparseArray<ArrayList<EventListener>> listeners) {
        int size = listeners.size();
        for (int i = 0; i < size; i++) {
            ArrayList<EventListener> list = listeners.valueAt(i);
            list.clear();
        }
        listeners.clear();
    }
}
