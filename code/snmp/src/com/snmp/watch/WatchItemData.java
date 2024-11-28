package com.snmp.watch;

public class WatchItemData {
	public String mAddress = "";
	public long mBalance = 0;
	public String mApiName = "";
	public String mQueryResult = "querying";

	public WatchItemData(String address) {
		mAddress = address;
	}

	public String getAddress() {
		return mAddress;
	}

	public long getBalance() {
		return mBalance;
	}

}
