package com.snmp.api.price;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.snmp.network.JsonUtils;
import com.snmp.network.TimeoutChecker;
import com.snmp.utils.LogUtils;
import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ThreadPoolUtil;
import com.snmp.utils.ToastUtils;

public class ChainSoPriceApi {
	private static final String TAG = "PriceApi";
	private static final String GET_PRICE_URL = "https://chain.so/api/v2/get_price/BIT/USD";
	private double mPrice = 0;

	public ChainSoPriceApi() {
	}

	public String getUrl() {
		return GET_PRICE_URL;
	}

	public double getPrice() {
		return mPrice;
	}

	public double parsePrice(String jsondata) {
		try {
			JSONObject jsonObject = new JSONObject(jsondata);
			JSONObject data = jsonObject.getJSONObject("data");

			JSONArray array = data.getJSONArray("prices");
			if (array.length() > 0) {
				JSONObject item = array.getJSONObject(0);
				mPrice = item.getDouble("price");
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return mPrice;
	}

	public void postPrice() {
		ThreadPoolUtil.post(new Runnable() {
			@Override
			public void run() {
				TimeoutChecker checker = new TimeoutChecker() {

					@Override
					public String getData() {
						LogUtils.d(TAG, "postData " + getUrl());
						return JsonUtils.postData(getUrl());
					}

					@Override
					public void onSuccess(final String data) {
						parsePrice(data);
					}

					@Override
					public void onTimeout() {
						ToastUtils.showLimited("network timeout");
					}

				};
				checker.start();
			}
		});
	}
}
