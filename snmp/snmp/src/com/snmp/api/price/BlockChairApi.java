package com.snmp.api.price;

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
import com.snmp.wallet.BtcAccount;

public class BlockChairApi {
    private static final String TAG = "BlockChairApi";
    private static final String GET_URL = "https://api.blockchair.com/bitcoin/stats";
    private int mPrice = 0;
    private int mFee = 0;

    public BlockChairApi() {
    }

    public String getUrl() {
        return GET_URL;
    }

    public int getPrice() {
        return mPrice;
    }

    public double parseData(final onApiCallback watch, String jsondata) {
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONObject data = jsonObject.getJSONObject("data");

            mPrice = data.getInt("market_price_usd");
            mFee = data.getInt("suggested_transaction_fee_per_byte_sat");
            LogUtils.d(TAG, "mPrice " + mPrice);

        } catch (JSONException e) {
            LogUtils.e(TAG, "parsePrice " + e);
            e.printStackTrace();
        }
        SnmpApplication.post(new Runnable() {
            @Override
            public void run() {
                if (watch == null) {
                    BtcAccount.getInstance().mFee = mFee;
                    BtcAccount.getInstance().mPrice = mPrice;
                    return;
                }
                String show = (int) mPrice + "  " + mFee;
                watch.onPriceResponce(mPrice, show, mFee);
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
                        parseData(watch, data);
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
