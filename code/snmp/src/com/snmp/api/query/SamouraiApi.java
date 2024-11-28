package com.snmp.api.query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SamouraiApi extends BaseQueryApi {
    private final String API_NAME = "samourai";
    private static final String GET_BALANCE_URL = "https://api.samouraiwallet.com/v2/unspent?active=";

    public SamouraiApi() {
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
            JSONArray outputs = jsonObject.getJSONArray("unspent_outputs");

            for (int i = 0; i < outputs.length(); i++) {
                JSONObject output = (JSONObject) outputs.get(i);
                balance += output.getLong("value");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return balance;
    }

    @Override
    public String getApiName() {
        return API_NAME;
    }

    @Override
    public int getCountLimitPerSecond() {
        return 5 - 1;
    }
}
