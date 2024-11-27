package com.snmp.api.price;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.snmp.api.price.PriceApi.onApiCallback;
import com.snmp.crypto.CyptoMgr;
import com.snmp.network.JsonUtils;
import com.snmp.network.TimeoutChecker;
import com.snmp.utils.LogUtils;
import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ThreadPoolUtil;
import com.snmp.utils.ToastUtils;

public class WorldCoinApi {
    private static final String TAG = "PriceApi";
    private static final String GET_PRICE_URL = "https://www.worldcoinindex.com/apiservice/ticker?key=0D7qOG1CUPWCOT8aS8cqLWHgwgz7UH9feer"
            + "&label=ethbtc-btcbtc&fiat=usd";
    private double mPrice = 0;
    private double mEthPrice = 0;

    public WorldCoinApi() {
    }

    public String getUrl() {
        return GET_PRICE_URL;
    }

    public double getPrice() {
        return mPrice;
    }

    public double parsePrice(final onApiCallback watch, String jsondata) {
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            // JSONObject data = jsonObject.getJSONObject("data");

            JSONArray array = jsonObject.getJSONArray("Markets");
            if (array.length() > 0) {
                JSONObject item = array.getJSONObject(0);
                mPrice = item.getDouble("Price");
            }
            if (array.length() > 1) {
                JSONObject item = array.getJSONObject(1);
                mEthPrice = item.getDouble("Price");
            }
            LogUtils.d(TAG, "mPrice " + mPrice);

        } catch (JSONException e) {
            LogUtils.e(TAG, "parsePrice " + e);
            e.printStackTrace();
        }
        SnmpApplication.post(new Runnable() {
            @Override
            public void run() {
                String show = (int) mPrice + "   " + (int) mEthPrice;
                watch.onPriceResponce((int)mPrice, show, 0);
            }
        });
        return mPrice;
    }

    public void postApi(final onApiCallback watch) {
        ThreadPoolUtil.post(new Runnable() {
            @Override
            public void run() {
                TimeoutChecker checker = new TimeoutChecker() {

                    @Override
                    public String getData() {
                        LogUtils.d(TAG, "postData " + getUrl());
                        return JsonUtils.postData(getUrl());
                    }

                    @Override
                    public void onSuccess(final String data) {
                        parsePrice(watch, data);
                    }

                    @Override
                    public void onTimeout() {
                        ToastUtils.showLimited("network timeout");
                    }

                };
                checker.start();
            }
        });
    }

}
