package com.snmp.cold;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.ScriptOutput;
import com.snmp.utils.LogUtils;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.BtcUtxo;

public class ColdMgr {
    private static final String TAG = ColdMgr.class.getSimpleName();
    private static ColdMgr sCyptoMgr;
    private boolean mColdServer = true;
    public boolean mSigned = false;

    public static ColdMgr getInstance() {
        if (sCyptoMgr == null) {
            synchronized (ColdMgr.class) {
                if (sCyptoMgr == null) {
                    sCyptoMgr = new ColdMgr();
                }
            }
        }
        return sCyptoMgr;
    }

    private ColdMgr() {
        mColdServer = PreferenceManager.getBoolean("cold_is_server", true);
    }

    public boolean isServer() {
        return mColdServer;
    }

    public void switchServer() {
        mColdServer = !mColdServer;
        PreferenceManager.putBoolean("cold_is_server", mColdServer);
    }

    public static boolean parseJsonString(String localAddr, String jsondata, TransactionData transactionData) {
        ArrayList<BtcUtxo> utxoList = transactionData.mUtxoList;
        transactionData.mLocalAddr = localAddr;

        if (TextUtils.isEmpty(jsondata)) {
            return false;
        }

        try {
            JSONObject dataObject = new JSONObject(jsondata);

            utxoList.clear();

            transactionData.mFee = dataObject.getInt("fee");
            transactionData.mPrice = dataObject.getLong("price");
            transactionData.mRecipientAddr = dataObject.getString("recipientaddr");
            transactionData.mRecipientValue = dataObject.getLong("recipientvalue");

            JSONArray utxos = dataObject.getJSONArray("utxos");
            for (int i = 0; i < utxos.length(); i++) {
                JSONObject output = (JSONObject) utxos.get(i);
                parseUnsignItem(localAddr, utxoList, output);
            }
            transactionData.mSendTx = dataObject.getString("sendtx");
            transactionData.mSendCost = dataObject.getString("sendcost");
            transactionData.mSendFee = dataObject.getString("sendfee");
            transactionData.mSendDetail = dataObject.getString("senddetail");
            ColdMgr.getInstance().mSigned = !TextUtils.isEmpty(transactionData.mSendTx);
            LogUtils.i(TAG, "parseJsonString " + " " + jsondata);
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            LogUtils.i(TAG, "parseJsonString " + " " + e);
        }
        return false;
    }

    private static void parseUnsignItem(String localAddr, ArrayList<BtcUtxo> utxosList, JSONObject output)
            throws JSONException {
        int height = 1;
        long value = output.getLong("value");
        long index = output.getLong("tx_output_n");
        String txHashId = output.getString("tx_hash");

        BitcoinAddress btcAddress = BitcoinAddress.fromString(localAddr);
        ScriptOutput script = BtcUtxo.createScript(btcAddress);

        BtcUtxo utxo = new BtcUtxo(txHashId, index, value, height, script);
        utxosList.add(utxo);
    }

    public static String composeJsonString(ArrayList<BtcUtxo> utxosList, int fee, long price, String recipientaddr,
            long recipientvalue, String sendTx, String sendCost, String sendFee, String sendDetail) {
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("fee", fee);
            jsonObject.put("price", price);
            jsonObject.put("recipientaddr", recipientaddr);
            jsonObject.put("recipientvalue", recipientvalue);

            JSONArray array = new JSONArray();
            for (int i = 0; i < utxosList.size(); i++) {
                array.put(i, utxosList.get(i).getJson());
            }
            jsonObject.put("utxos", array);
            jsonObject.put("sendtx", sendTx);
            jsonObject.put("sendcost", sendCost);
            jsonObject.put("sendfee", sendFee);
            jsonObject.put("senddetail", sendDetail);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
