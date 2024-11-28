package com.snmp.api.query;

import org.json.JSONObject;


public class BlockCypherApi extends BaseQueryApi {
	private final String API_NAME = "blockcypher.com";
	private static final String GET_BALANCE_URL = "https://api.blockcypher.com/v1/btc/main/addrs/";

	public BlockCypherApi() {
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

			balance = jsonObject.getLong("final_balance");

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
