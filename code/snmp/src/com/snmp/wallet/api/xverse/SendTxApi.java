package com.snmp.wallet.api.xverse;

import java.util.Map;

import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.LogUtils;
import com.snmp.utils.ToastUtils;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.ApiMgr;
import com.snmp.wallet.api.BaseSendTxApi;

public class SendTxApi extends BaseSendTxApi {
    private static final String TAG = SendTxApi.class.getSimpleName();
    private static final String SEND_API = "https://btc-1.xverse.app/tx";
    private static final String SEND_API_TESTNET = "https://btc-testnet.xverse.app/tx";

    public SendTxApi() {
        super();
    }

    @Override
    public String sendTxNewThread(String tx) {
        String _url = "";

        LogUtils.i(TAG, "sendTx " + tx);

        String response = "init";
        try {
            String _base = Constant.IS_PRODUCTION ? SEND_API : SEND_API_TESTNET;
            response = postURL(null, _base + _url + "?at=" + "", "" + tx, (Map<String, String>) null);

            ToastUtils.show(response);
        } catch (Exception e) {
            e.printStackTrace();
            response = e.toString();
        }

        LogUtils.i(TAG, "sendTx " + response);
        ToastUtils.show(ApiMgr.TYPE_XVERSE + response);
        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE, ApiMgr.TYPE_XVERSE + response);
        return response;
    }

}
