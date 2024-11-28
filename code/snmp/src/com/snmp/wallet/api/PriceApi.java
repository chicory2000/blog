package com.snmp.wallet.api;

import org.json.JSONArray;
import org.json.JSONObject;

import com.snmp.wallet.BtcAccount;

public class PriceApi extends BaseQueryApi {
	private static final String TAG = PriceApi.class.getSimpleName();
	private static final String GET_PRICE_URL = "https://www.worldcoinindex.com/apiservice/ticker?key=0D7qOG1CUPWCOT8aS8cqLWHgwgz7UH9feer&label=btcbtc&fiat=usd";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getUrl() {
        return GET_PRICE_URL;
    }

    @Override
    public void parseData(String jsondata) throws Exception {
        JSONObject jsonObject = new JSONObject(jsondata);

        JSONArray array = jsonObject.getJSONArray("Markets");
        if (array.length() > 0) {
            JSONObject item = array.getJSONObject(0);
            BtcAccount.getInstance().mPrice = item.getDouble("Price");
        }        
    }
}
