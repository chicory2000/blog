package com.snmp.wallet;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import org.json.JSONException;
import org.json.JSONObject;

import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.OutPoint;
import com.mrd.bitlib.model.ScriptOutput;
import com.mrd.bitlib.model.ScriptOutputP2PKH;
import com.mrd.bitlib.model.ScriptOutputP2SH;
import com.mrd.bitlib.model.ScriptOutputP2WPKH;
import com.mrd.bitlib.model.UnspentTransactionOutput;
import com.snmp.utils.LogUtils;

public class BtcUtxo {
    private static final String TAG = BtcUtxo.class.getSimpleName();
    private long mValue;
    private final ScriptOutput mScript;
    private final String mHash;
    private long mIndex = -1;
    private final int mHeight;

    public BtcUtxo(String hash, long index, long value, int height, ScriptOutput script) {
        this.mHash = checkNotNull(hash);
        this.mIndex = index;
        this.mValue = checkNotNull(value);
        this.mHeight = height;
        this.mScript = script;
    }

    public void setupdate(long value) {
        this.mValue = checkNotNull(value);
    }

    public void setIndex(long index) {
        mIndex = index;
    }

    public long getValue() {
        return mValue;
    }

    public ScriptOutput getScript() {
        return mScript;
    }

    public String getHash() {
        return mHash;
    }

    public long getIndex() {
        return mIndex;
    }

    public int getHeight() {
        return mHeight;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "Stored TxOut of %d (%s:%d)", mValue, mHash, mIndex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndex(), getHash(), getValue());
    }

    public static ScriptOutput createScript(BitcoinAddress address) {
        ScriptOutput script;
        switch (address.getType()) {
           case P2SH_P2WPKH:
              script = new ScriptOutputP2SH(address.getTypeSpecificBytes());
              break;
           case P2PKH:
              script = new ScriptOutputP2PKH(address.getTypeSpecificBytes());
              break;
           case P2WPKH:
              script = new ScriptOutputP2WPKH(address.getTypeSpecificBytes());
              break;
           default:
               return null;
        }
        return script;
     }

    public JSONObject getJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("value", mValue);
            jsonObject.put("tx_output_n", mIndex);
            jsonObject.put("tx_hash", mHash);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<UnspentTransactionOutput> getUtxoList(ArrayList<BtcUtxo> utxoList) {
        ArrayList<UnspentTransactionOutput> outputs = new ArrayList<UnspentTransactionOutput>();
        for (BtcUtxo utxo : utxoList) {
            if (utxo.getIndex() < 0) {
                continue;
            }

            OutPoint outPoint = OutPoint.fromString(utxo.getHash() + ":" + utxo.getIndex());

            UnspentTransactionOutput output = new UnspentTransactionOutput(outPoint, utxo.getHeight(), utxo.getValue(),
                    utxo.getScript());
            outputs.add(output);
            LogUtils.i(TAG, "getUtxos= " + output);
        }
        return outputs;
    }
}
