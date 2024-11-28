package com.snmp.api.query;

public abstract class BaseQueryApi {
	public String mAddress = "";

	public BaseQueryApi() {
	}

	public String getAddress() {
		return mAddress;
	}
	
	public abstract String getApiName();

	public abstract String getUrl();

	public abstract long getBalance(String responseData);
	
	public abstract int getCountLimitPerSecond();
}
