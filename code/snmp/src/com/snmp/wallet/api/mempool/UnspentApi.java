package com.snmp.wallet.api.mempool;

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
    // https://mempool.space/testnet/api/address/mooUJkBfR1NtD4djLsxdpDNK3r2tGMf8WT/utxo
    // https://mempool.space/api/address/1KFHE7w8BhaENAswwryaoccDb6qcT6DbYY/utxo
    private static final String GET_ADDRS_PRODUCTION_URL = "https://mempool.space/api/address/";
    private static final String GET_ADDRS_TEST_URL = "https://mempool.space/testnet/api/address/";
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
            return GET_ADDRS_PRODUCTION_URL + mAddress + "/utxo";
        }
        return GET_ADDRS_TEST_URL + mAddress + "/utxo";
    }

    @Override
    public void parseData(String jsondata) throws Exception {
        // LogUtils.i(TAG, "parseData" + " " + jsondata);
        mUtxoList.clear();

        JSONArray txrefsArray = new JSONArray(jsondata);

        for (int i = 0; i < txrefsArray.length(); i++) {
            JSONObject txref = (JSONObject) txrefsArray.get(i);
            parseItem(txref);
        }

        PreferenceManager.putString("wallet_bc_utxos_json", jsondata);
    }

    private void parseItem(JSONObject txref) throws JSONException {
        int height = 1;
        long value = txref.getLong("value");
        String txHashId = txref.getString("txid");
        long index = txref.getLong("vout");

        BitcoinAddress btcAddress = BitcoinAddress.fromString(mAddress);
        ScriptOutput script = BtcUtxo.createScript(btcAddress);

        BtcUtxo utxo = new BtcUtxo(txHashId, index, value, height, script);
        mUtxoList.add(utxo);
    }

}
