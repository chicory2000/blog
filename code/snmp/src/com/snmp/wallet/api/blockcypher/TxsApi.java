package com.snmp.wallet.api.blockcypher;

import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.BtcUtxo;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.BaseQueryApi;

public class TxsApi extends BaseQueryApi {
    private static final String TAG = TxsApi.class.getSimpleName();
    private static final String GET_TXS_PRODUCTION_URL = "https://api.blockcypher.com/v1/btc/main/txs/";
    private static final String GET_TXS_TEST_URL = "https://api.blockcypher.com/v1/btc/test3/txs/";
    private String mAddress = "";
    private BtcUtxo mUtxo;

    public TxsApi(String address, BtcUtxo utxo) {
        mAddress = address;
        mUtxo = utxo;
        initPre();
    }

    private void initPre() {
        String txsPre = PreferenceManager.getString("wallet_utxo_txs" + mUtxo.getHash(), "");
        if (!TextUtils.isEmpty(txsPre)) {
            parseRawData(txsPre);
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }

    public String getUrl() {
        if (Constant.IS_PRODUCTION) {
            return GET_TXS_PRODUCTION_URL + mUtxo.getHash();
        }
        return GET_TXS_TEST_URL + mUtxo.getHash();
    }

    @Override
    public void parseData(String jsondata) throws Exception {
        JSONObject jsonObject = new JSONObject(jsondata);

        JSONArray outputs = jsonObject.getJSONArray("outputs");
        for (int i = 0; i < outputs.length(); i++) {
            JSONObject output = (JSONObject) outputs.get(i);
            JSONArray addresses = output.getJSONArray("addresses");
            // LogUtils.i(TAG, "parseData" + " " + mUtxo.getAddress() + " "
            // + addresses.toString());
            if (addresses.toString().contains(mAddress)) {
                mUtxo.setIndex(i);
                break;
            }
        }

        PreferenceManager.putString("wallet_utxo_txs" + mUtxo.getHash(), jsondata);
    }

    @Override
    public int getCountLimitPerSecond() {
        return 6 - 1;
    }

    @Override
    public void postApi() {
        if (mUtxo.getIndex() >= 0) {
            EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE);
            return;
        }

        super.postApi();
    }
}
