package com.snmp.wallet.api.chainso;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.LogUtils;
import com.snmp.utils.ToastUtils;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.BaseSendTxApi;

public class SendTxApi extends BaseSendTxApi {
    private static final String TAG = SendTxApi.class.getSimpleName();

    public SendTxApi() {
        super();
    }

    @Override
    public String sendTxNewThread(final String tx) {
        String url = "";
        if (Constant.IS_PRODUCTION) {
            url = "https://chain.so/api/v2/send_tx/BITCOIN";
        } else {
            url = "https://chain.so/api/v2/send_tx/BTCTEST";
        }

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tx_hex", tx);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "sendTx " + tx);

        String response = "init";
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());

            Request rq2 = new Request.Builder().url(url).post(requestBody).build();

            response = client.newCall(rq2).execute().body().string();
        } catch (Exception e) {
            response = e.toString();
            e.printStackTrace();
        }

        LogUtils.i(TAG, "sendTx " + response);
        ToastUtils.show(response);
        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE, response);
        return response;
    }
}
