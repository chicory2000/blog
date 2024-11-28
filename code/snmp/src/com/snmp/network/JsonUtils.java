package com.snmp.network;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import android.os.Build;

import com.snmp.utils.LogUtils;
import com.snmp.utils.TimeUtils;
import com.snmp.utils.Utils;

public class JsonUtils {
	public static final String DATA_FAIL = "data_fail";
	private static final String APP_TOKEN_KEY = "GjNj8bc4jYqo9NRkQdfdaMtnW650Nkb";
	private static final String TAG = "JsonUtils";
	private static final String JSON_SIGN = "data";// need define
	private static final String NO_NETWORK = DATA_FAIL + "no network";
	private static final String NETWORK_DATA_FAIL = DATA_FAIL
			+ "network data fail";

	public static String post(String actionUrl) {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(actionUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(TimeUtils.SECOND_10);
			conn.setReadTimeout(TimeUtils.SECOND_10);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Accept-Encoding", "gzip");

			int code = conn.getResponseCode();
			if (code == HttpURLConnection.HTTP_OK) {
				return getConnectResult(conn);
			}
		} catch (IOException e) {
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return NETWORK_DATA_FAIL;
	}

	public static String postData(String actionUrl) {
		return postData(actionUrl, new HashMap<String, String>());
	}

	public static String postData(String actionUrl, Map<String, String> params) {
		if (!Utils.hasNetwork()) {
			return NO_NETWORK;
		}
		HttpURLConnection connection = null;
		InputStream inputStream = null;
		try {
			String finalUrl = actionUrl;// getRequestData(params, actionUrl);
			URL url = new URL(finalUrl);

			connection = (HttpURLConnection) url.openConnection();

			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(false);
			connection.setConnectTimeout(TimeUtils.SECOND_3);
			connection.setReadTimeout(TimeUtils.SECOND_3);
			connection.setRequestMethod("GET");

			inputStream = connection.getInputStream();
			int response = connection.getResponseCode();
			if (response == HttpURLConnection.HTTP_OK) {
				return getConnectResult(connection);
			}
		} catch (Exception e) {
			LogUtils.e(TAG, "postData error " + actionUrl, e);
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (connection != null) {
					connection.disconnect();
				}
			} catch (IOException e) {
			}
		}
		return NETWORK_DATA_FAIL;
	}

	private static String getConnectResult(URLConnection conn) {
		InputStream finalInputStream = null;
		try {
			InputStream is = conn.getInputStream();
			if ("gzip".equals(conn.getContentEncoding())) {
				finalInputStream = new BufferedInputStream(new GZIPInputStream(
						is));
			} else {
				finalInputStream = is;
			}
			StringBuilder res = new StringBuilder();
			int ch;
			while ((ch = finalInputStream.read()) != -1) {
				res.append((char) ch);
			}
			return res.toString();
		} catch (Exception e) {
			LogUtils.e(TAG, "getConnectResult error ", e);
		} finally {
			try {
				if (finalInputStream != null) {
					finalInputStream.close();
				}
			} catch (IOException e) {
			}
		}
		return NETWORK_DATA_FAIL;
	}

	private static String getRequestData(Map<String, String> params, String url) {
		addCommonParam(params);
		StringBuffer stringBuffer = new StringBuffer();
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(getUTF8Code(entry.getValue())).append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);
		} catch (Exception e) {
		}
		String finalUrl = url + "?" + stringBuffer.toString();
		LogUtils.d(TAG, "postData " + finalUrl);
		return finalUrl;
	}

	private static void addCommonParam(Map<String, String> params) {
		try {
			String time = "" + System.currentTimeMillis() / 1000;
			String softVersion = Utils.getAppVersion();
			String imei = Utils.getImei();
			String deviceModel = Build.DEVICE;

			params.put("imei", imei);
			params.put("device_model", deviceModel);
			params.put("phone_soft_version", softVersion);
			params.put("time", time);

			String sign = getSign(params, APP_TOKEN_KEY);
			params.put("sign", sign);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String getSign(Map<String, String> params, String tokenKey)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String[] keys = (String[]) params.keySet().toArray(
				new String[params.keySet().size()]);
		Arrays.sort(keys);

		StringBuffer paramBuffer = new StringBuffer();
		for (String key : keys) {
			paramBuffer.append(key + "=" + params.get(key));
		}
		paramBuffer.append(tokenKey);
		String param = paramBuffer.toString();

		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(param.getBytes("UTF-8"));
		byte[] b = md.digest();
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int val = ((int) b[i]) & 0xff;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}
		String sign = hexValue.toString();
		return sign;
	}

	public static boolean isRequestDataSuccess(String data) {
		if (data == null) {
			return false;
		}
		return data.isEmpty() || hasSign(data);
	}

	public static boolean isRequestDataFail(String data) {
		return NETWORK_DATA_FAIL.equals(data);
	}

	public static boolean hasSign(String jsonInfo) {
		if (jsonInfo == null) {
			return false;
		}
		return jsonInfo.contains(JSON_SIGN);
	}

	public static String getUTF8Code(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		} catch (Exception e) {
			return "";
		}
	}
}
