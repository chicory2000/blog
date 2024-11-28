package com.snmp.wallet.api.blockcypher;

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
import com.snmp.wallet.api.ApiMgr;
import com.snmp.wallet.api.BaseSendTxApi;

public class SendTxApi extends BaseSendTxApi {
    private static final String TAG = SendTxApi.class.getSimpleName();

    public SendTxApi() {
        super();
    }

    @Override
    public String sendTxNewThread(String tx) {
        String url = "";
        if (Constant.IS_PRODUCTION) {
            url = "https://api.blockcypher.com/v1/btc/main/txs/push";
        } else {
            url = "https://api.blockcypher.com/v1/btc/test3/txs/push";
        }

        OkHttpClient client = new OkHttpClient();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("tx", tx);
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
        ToastUtils.show(ApiMgr.TYPE_BLOCKCYPHER + response);
        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE, ApiMgr.TYPE_BLOCKCYPHER + response);
        return response;
    }
}
