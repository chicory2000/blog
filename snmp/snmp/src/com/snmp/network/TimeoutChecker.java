package com.snmp.network;



public abstract class TimeoutChecker {

    private static final String INITIAL_DATA = "initial_data";
	private static final int SECOND_10 = 10000;

    private String mData;

    public String start() {
        return start(SECOND_10);
    }

    public String start(int timeoutMs) {
        mData = INITIAL_DATA;

        Thread thread = new Thread() {
            public void run() {
                mData = getData();
            }
        };

        thread.start();
        try {
            thread.join(timeoutMs);
        } catch (InterruptedException e) {
        }

        if (INITIAL_DATA.equals(mData)) {
            onTimeout();
            return null;
        } else {
            onSuccess(mData);
            return mData;
        }
    }

    public void onSuccess(String data) {

    }

    public void onTimeout() {

    }

    public abstract String getData();

}
