package com.snmp.api.query;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.snmp.utils.LogUtils;

public class BlockChainApi extends BaseQueryApi {
    private final boolean V2 = true;
    private final String API_NAME = "blockchain.info";
    private static final String GET_BALANCE_URL = "https://blockchain.info/q/addressbalance/";
    private static final String GET_BALANCE_URL2 = "https://blockchain.info/unspent?active=";

    public BlockChainApi() {
        super();
    }

    @Override
    public String getUrl() {
        if (V2) {
            return GET_BALANCE_URL2;
        }
        return GET_BALANCE_URL;
    }

    @Override
    public long getBalance(String data) {
        long balance = 0;
        try {
            if (V2) {
                JSONObject jsonObject = new JSONObject(data);
                JSONArray arrayDataObject = jsonObject.getJSONArray("unspent_outputs");

                for (int i = 0; i < arrayDataObject.length(); i++) {
                    JSONObject txref = (JSONObject) arrayDataObject.get(i);
                    balance += txref.getLong("value");
                }
            } else {
                balance = Long.parseLong(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e("BlockChainApi", "getBalance e=" + e);
        }
        return balance;
    }

    @Override
    public String getApiName() {
        return API_NAME;
    }

    @Override
    public int getCountLimitPerSecond() {
        return 8 - 1;
    }
}
