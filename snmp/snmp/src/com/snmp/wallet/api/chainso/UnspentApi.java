package com.snmp.wallet.api.chainso;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.ScriptOutput;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.BtcUtxo;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.BaseQueryApi;

public class UnspentApi extends BaseQueryApi {
    private static final String TAG = UnspentApi.class.getSimpleName();
    private static final String GET_ADDRS_PRODUCTION_URL = "https://chain.so/api/v2/get_tx_unspent/BITCOIN/";
    private static final String GET_ADDRS_TEST_URL = "https://chain.so/api/v2/get_tx_unspent/BTCTEST/";
    private String mAddress = "";
    private ArrayList<BtcUtxo> mUtxoList;

    @Override
    public void init(String address, java.util.ArrayList<BtcUtxo> utxoList) {
        mAddress = address;
        mUtxoList = utxoList;

        initPre();
    }

    private void initPre() {
        String utxosPre = PreferenceManager.getString("wallet_chainso_utxos_json", "");
        if (!TextUtils.isEmpty(utxosPre)) {
            parseRawData(utxosPre);
        }
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getUrl() {
        if (Constant.IS_PRODUCTION) {
            return GET_ADDRS_PRODUCTION_URL + mAddress;
        }
        return GET_ADDRS_TEST_URL + mAddress;
    }

    @Override
    public void parseData(String jsondata) throws Exception {
        // LogUtils.i(TAG, "parseData" + " " + jsondata);
        mUtxoList.clear();
        boolean hasUnconfirmed = false;

        JSONObject jsonObject = new JSONObject(jsondata);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        JSONArray outputs = dataObject.getJSONArray("txs");

        //LogUtils.i(TAG, "parseDataaaaaa " + " " + outputs.length());
        for (int i = 0; i < outputs.length(); i++) {
            JSONObject output = (JSONObject) outputs.get(i);
            parseItem(output);
        }

        hasUnconfirmed = false;// jsondata.contains("unconfirmed_txrefs");
        PreferenceManager.putString("wallet_chainso_utxos_json", jsondata);

        BtcAccount.getInstance().mHasUnconfirmed = hasUnconfirmed;
    }

    private void parseItem(JSONObject output) throws JSONException {
        int height = 1;
        double value = output.getDouble("value") * Constant.SATOSHIS_PER_BITCOIN;
        long index = output.getLong("output_no");
        String txHashId = output.getString("txid");

        BitcoinAddress btcAddress = BitcoinAddress.fromString(mAddress);
        ScriptOutput script = BtcUtxo.createScript(btcAddress);

        BtcUtxo utxo = new BtcUtxo(txHashId, index, (long)value, height, script);
        mUtxoList.add(utxo);
    }

}
