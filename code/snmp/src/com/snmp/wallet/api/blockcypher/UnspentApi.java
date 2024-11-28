package com.snmp.wallet.api.blockcypher;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.ScriptOutput;
import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.BtcUtxo;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.BaseQueryApi;

public class UnspentApi extends BaseQueryApi {
    private static final String TAG = UnspentApi.class.getSimpleName();
    // https://api.blockcypher.com/v1/btc/main/addrs/1KFHE7w8BhaENAswwryaoccDb6qcT6DbYY
    private static final String GET_ADDRS_PRODUCTION_URL = "https://api.blockcypher.com/v1/btc/main/addrs/";
    private static final String GET_ADDRS_TEST_URL = "https://api.blockcypher.com/v1/btc/test3/addrs/";
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
        JSONArray txrefsArray = jsonObject.getJSONArray("txrefs");

        for (int i = 0; i < txrefsArray.length(); i++) {
            JSONObject txref = (JSONObject) txrefsArray.get(i);
            parseItem(txref);
        }

        hasUnconfirmed = jsondata.contains("unconfirmed_txrefs");
        PreferenceManager.putString("wallet_bc_utxos_json", jsondata);

        BtcAccount.getInstance().mHasUnconfirmed = hasUnconfirmed;
        //postIndexs(hasUnconfirmed);
    }

    private void parseItem(JSONObject txref) throws JSONException {
        if (!txref.has("spent")) {
            return;
        }

        boolean spent = txref.getBoolean("spent");
        if (!spent) {
            int height = txref.getInt("block_height");
            long value = txref.getLong("value");
            String txHashId = txref.getString("tx_hash");
            long index = txref.getLong("tx_output_n");

            BitcoinAddress btcAddress = BitcoinAddress.fromString(mAddress);
            ScriptOutput script = BtcUtxo.createScript(btcAddress);

//            long lastIndex = -1;
//            for (BtcUtxo utxo : mUtxoList) {
//                if (utxo.getHash().equals(txHashId)) {
//                    lastIndex = utxo.getIndex();
//                    mUtxoList.remove(utxo);
//                }
//            }
            BtcUtxo utxo = new BtcUtxo(txHashId, index, value, height, script);
            mUtxoList.add(utxo);
        }
    }

    private void postIndexs(boolean hasUnconfirmed) {
        if (mUtxoList.size() == 0) {
            EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE);
            return;
        }

        for (BtcUtxo utxo : mUtxoList) {
            if (!TextUtils.isEmpty(utxo.getHash()) && utxo.getIndex() < 0) {
                TxsApi txsApi = new TxsApi(mAddress, utxo);
                txsApi.postApi();
            }
        }
    }

}
