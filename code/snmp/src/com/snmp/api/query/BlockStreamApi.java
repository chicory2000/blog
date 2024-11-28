package com.snmp.api.query;

import org.json.JSONObject;


public class BlockStreamApi extends BaseQueryApi {
	private final String API_NAME = "blockstream.com";
	private static final String GET_BALANCE_URL = "https://blockstream.info/api/address/";

	public BlockStreamApi() {
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

            String result = data.getString("funded_txo_sum");
            balance = (long)(Double.parseDouble(result) * 1);

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
