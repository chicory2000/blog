package com.snmp.api.query;

import org.json.JSONObject;

public class BitapsApi extends BaseQueryApi {
    private final String API_NAME = "bitaps.com";
    private static final String GET_BALANCE_URL = "https://api.bitaps.com/btc/v1/blockchain/address/state/";
    // https://api.bitaps.com/market/v1/ticker/btcusd
    // https://api.bitaps.com/btc/v1/blockchain/address/transactions/1KFHE7w8BhaENAswwryaoccDb6qcT6DbYY
    // https://api.bitaps.com/btc/v1/blockchain/transaction/aa968fdf575a752a461d96d03ba82c976b56369b5e036aacca6eced8bafbc21f

    public BitapsApi() {
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
            JSONObject data = jsonObject.getJSONObject("data");

            balance = data.getLong("balance");

        } catch (Exception e) {
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
        return 6 - 1;
    }
}
