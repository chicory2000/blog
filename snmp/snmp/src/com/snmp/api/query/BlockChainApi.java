package com.snmp.api.query;



public class BlockChainApi extends BaseQueryApi {
	private final String API_NAME = "blockchain.info";
	private static final String GET_BALANCE_URL = "https://blockchain.info/q/addressbalance/";

	public BlockChainApi() {
		super();
	}

	@Override
	public String getUrl() {
		return GET_BALANCE_URL;
	}

	@Override
	public long getBalance(String data) {
		long balance = -1;
		try {
			balance = Long.parseLong(data);

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
		return 8 - 1;
	}
}
