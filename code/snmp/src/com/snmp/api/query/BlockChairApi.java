package com.snmp.api.query;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.snmp.utils.LogUtils;

public class BlockChairApi extends BaseQueryApi {
    private final String API_NAME = "blockchair.com";
    private static final String GET_BALANCE_URL = "https://api.blockchair.com/bitcoin/addresses/balances?addresses=";

    public BlockChairApi() {
        super();
    }

    @Override
    public String getUrl() {
        return GET_BALANCE_URL;
    }

    @Override
    public long getBalance(String jsondata) {
        long balance = -1;
        try {
            JSONObject jsonObject = new JSONObject(jsondata);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            Iterator<String> keys = dataObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                balance = dataObject.getLong(key);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.e("BlockChairApi", "getBalance e=" + e);
        }
        return balance;
    }

    @Override
    public String getApiName() {
        return API_NAME;
    }

    @Override
    public int getCountLimitPerSecond() {
        return 6 - 1;
    }
}
