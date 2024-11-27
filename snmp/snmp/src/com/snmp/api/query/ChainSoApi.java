package com.snmp.api.query;

import org.json.JSONException;
import org.json.JSONObject;


public class ChainSoApi extends BaseQueryApi {
	private final String API_NAME = "chain.so";
	private static final String GET_BALANCE_URL = "https://chain.so/api/v2/get_address_balance/BITCOIN/";

	public ChainSoApi() {
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

			String result = data.getString("confirmed_balance");
			balance = (long)(Double.parseDouble(result) * 100000000);

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
