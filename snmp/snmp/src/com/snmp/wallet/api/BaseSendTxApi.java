package com.snmp.wallet.api;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.snmp.utils.ThreadPoolUtil;

public abstract class BaseSendTxApi {
    private static final String TAG = BaseSendTxApi.class.getSimpleName();
    private static final int DefaultRequestRetry = 2;
    private static final int DefaultRequestTimeout = 60000;

    public BaseSendTxApi() {
    }

    public void sendTx(final String tx) {
        ThreadPoolUtil.post(new Runnable() {
            @Override
            public void run() {
                sendTxNewThread(tx);
            }
        });
    }

    public abstract String sendTxNewThread(final String tx);

    public static String postURL(String contentType, String request, String urlParameters, Map<String, String> headers)
            throws Exception {
        // default headers
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        if (!headers.containsKey("charset")) {
            headers.put("charset", "utf-8");
        }
        if (!headers.containsKey("User-Agent")) {
            String agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36";
            headers.put("User-Agent", agent);
        }

        String error = null;

        for (int ii = 0; ii < DefaultRequestRetry; ++ii) {
            URL url = new URL(request);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", contentType == null ? "application/x-www-form-urlencoded"
                        : contentType);
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));

                // set headers
                for (Map.Entry<String, String> e : headers.entrySet()) {
                    connection.setRequestProperty(e.getKey(), e.getValue());
                }

                connection.setUseCaches(false);

                connection.setConnectTimeout(DefaultRequestTimeout);
                connection.setReadTimeout(DefaultRequestTimeout);

                connection.connect();

                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();

                connection.setInstanceFollowRedirects(false);

                if (connection.getResponseCode() == 200) {
                    // System.out.println("postURL:return code 200");
                    return "return code 200";
                } else {
                    error = "return code " + error;
                    // System.out.println("postURL:return code " + error);
                }

                Thread.sleep(5000);
            } finally {
                connection.disconnect();
            }
        }

        throw new Exception("Invalid Response " + error);
    }

}
