package com.snmp.wallet.api.samourai;

import java.util.Map;

import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.LogUtils;
import com.snmp.utils.ToastUtils;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.ApiMgr;
import com.snmp.wallet.api.BaseSendTxApi;

public class SendTxApi extends BaseSendTxApi {
    private static final String TAG = SendTxApi.class.getSimpleName();

    private static final String SAMOURAI_API2 = "https://api.samouraiwallet.com/v2/";
    private static final String SAMOURAI_API2_TESTNET = "https://api.samouraiwallet.com/test/v2/";

    public SendTxApi() {
        super();
    }

    @Override
    public String sendTxNewThread(String tx) {
        String _url = "pushtx/";

        LogUtils.i(TAG, "sendTx " + tx);

        String response = "init";
        try {
            String _base = Constant.IS_PRODUCTION ? SAMOURAI_API2 : SAMOURAI_API2_TESTNET;
            response = postURL(null, _base + _url + "?at=" + "", "tx=" + tx, (Map<String, String>) null);

            ToastUtils.show(response);
        } catch (Exception e) {
            e.printStackTrace();
            response = e.toString();
        }

        LogUtils.i(TAG, "sendTx " + response);
        ToastUtils.show(ApiMgr.TYPE_SAMOURAI + response);
        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE, ApiMgr.TYPE_SAMOURAI + response);
        return response;
    }

}
