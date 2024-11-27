package com.snmp.wallet.api.btccom;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.ScriptOutput;
import com.snmp.utils.LogUtils;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.BtcUtxo;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.BaseQueryApi;

public class UnspentApi extends BaseQueryApi {
    private static final String TAG = UnspentApi.class.getSimpleName();
    // https://chain.api.btc.com/v3/address/1KFHE7w8BhaENAswwryaoccDb6qcT6DbYY/unspent
    // https://explorer.btc.com/zh-CN/btc/adapter?type=api-doc
    private static final String GET_ADDRS_PRODUCTION_URL = "https://chain.api.btc.com/v3/address/";
    private static final String GET_ADDRS_TEST_URL = "https://chain.api.btc.com/v3/address/";
    private String mAddress = "";
    private ArrayList<BtcUtxo> mUtxoList;

    @Override
    public void init(String address, java.util.ArrayList<BtcUtxo> utxoList) {
        mAddress = address;
        mUtxoList = utxoList;

        initPre();
    }

    private void initPre() {
        String utxosPre = PreferenceManager.getString("wallet_bc_utxos_json", "");
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
            return GET_ADDRS_PRODUCTION_URL + mAddress + "/unspent";
        }
        return GET_ADDRS_TEST_URL + mAddress + "/unspent";
    }

    @Override
    public void parseData(String jsondata) throws Exception {
        LogUtils.i(TAG, "parseData" + " " + jsondata);
        mUtxoList.clear();

        JSONObject jsonObject = new JSONObject(jsondata);
        JSONObject dataObject = jsonObject.getJSONObject("data");
        JSONArray outputs = dataObject.getJSONArray("list");

        for (int i = 0; i < outputs.length(); i++) {
            JSONObject output = (JSONObject) outputs.get(i);
            parseItem(output);
        }

        PreferenceManager.putString("wallet_bc_utxos_json", jsondata);
    }

    private void parseItem(JSONObject txref) throws JSONException {
        int height = 1;
        long value = txref.getLong("value");
        String txHashId = txref.getString("tx_hash");
        long index = txref.getLong("tx_output_n");

        BitcoinAddress btcAddress = BitcoinAddress.fromString(mAddress);
        ScriptOutput script = BtcUtxo.createScript(btcAddress);

        BtcUtxo utxo = new BtcUtxo(txHashId, index, value, height, script);
        mUtxoList.add(utxo);
    }

}
