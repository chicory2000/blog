package com.snmp.api.query;

import org.json.JSONObject;


public class MempoolApi extends BaseQueryApi {
	private final String API_NAME = "mempool.space";
	private static final String GET_BALANCE_URL = "https://mempool.space/api/address/";

	public MempoolApi() {
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
            JSONObject data = jsonObject.getJSONObject("chain_stats");

            String funded = data.getString("funded_txo_sum");
            String spent = data.getString("spent_txo_sum");
            balance = (long)(Double.parseDouble(funded) - Double.parseDouble(spent));

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
