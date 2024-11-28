package com.snmp.wallet.api;

import java.util.ArrayList;

import com.snmp.network.JsonUtils;
import com.snmp.network.TimeoutChecker;
import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.LogUtils;
import com.snmp.utils.ThreadPoolUtil;
import com.snmp.utils.ToastUtils;
import com.snmp.wallet.BtcUtxo;
import com.snmp.wallet.Constant;

public abstract class BaseQueryApi {

    public BaseQueryApi() {
    }

    public void init(String address, ArrayList<BtcUtxo> utxoList) {

    }

    public abstract String getTag();

    public abstract String getUrl();

    public abstract void parseData(String jsondata) throws Exception;

    public void parseRawData(String jsondata) {
        String result = "";
        try {
            parseData(jsondata);
        } catch (Exception e) {
            result = getTag() + " parse failed";
            ToastUtils.show(result);

            e.printStackTrace();
        }
        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE, result);
    }

    public void postApi() {
        ThreadPoolUtil.post(new Runnable() {
            @Override
            public void run() {
                TimeoutChecker checker = new TimeoutChecker() {

                    @Override
                    public String getData() {
                        LogUtils.d(getTag(), "postData " + getUrl());
                        return JsonUtils.postData(getUrl());
                    }

                    @Override
                    public void onSuccess(final String data) {
                        parseRawData(data);
                    }

                    @Override
                    public void onTimeout() {
                        String timeout = getTag() + " network timeout";
                        ToastUtils.show(timeout);
                        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE, timeout);
                    }

                };
                checker.start();
            }
        });
        sleepForApi();
    }

    private void sleepForApi() {
        int countLimitPerSecond = getCountLimitPerSecond();
        if (countLimitPerSecond == 0) {
            return;
        }

        try {
            int sleepTime = Constant.SECOND_1 / countLimitPerSecond;
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getCountLimitPerSecond() {
        return 0;
    }
}
