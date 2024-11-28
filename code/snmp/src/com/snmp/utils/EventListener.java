package com.snmp.utils;

public interface EventListener {
    public void onEvent(int key, Object... params);
}
