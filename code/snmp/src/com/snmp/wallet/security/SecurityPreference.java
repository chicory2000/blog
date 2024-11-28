package com.snmp.wallet.security;

import android.text.TextUtils;

import com.mrd.bitlib.util.HexUtils;
import com.mycelium.wapi.wallet.SecureKeyValueStoreBacking;
import com.snmp.utils.PreferenceManager;

public class SecurityPreference implements SecureKeyValueStoreBacking {
    private static final String TAG = SecurityPreference.class.getSimpleName();
    private static final String WALLET_SECURE_STORE = "wallet_secure_store";

    public SecurityPreference() {
    }

    @Override
    public byte[] getValue(byte[] id) {
        String idKey = byteToString(id);
        String value = PreferenceManager.getString(WALLET_SECURE_STORE + idKey);
        if (!TextUtils.isEmpty(value)) {
            return stringToByte(value);
        }
        return null;
    }

    @Override
    public byte[] getValue(byte[] id, int subId) {
        return null;
    }

    @Override
    public void setValue(byte[] id, byte[] plaintextValue) {
        String idKey = byteToString(id);
        String value = byteToString(plaintextValue);
        PreferenceManager.putString(WALLET_SECURE_STORE + idKey, value);
    }

    @Override
    public int getMaxSubId() {
        return 0;
    }

    @Override
    public void setValue(byte[] key, int subId, byte[] value) {

    }

    @Override
    public void deleteValue(byte[] id) {
        String idKey = byteToString(id);

        PreferenceManager.putString(WALLET_SECURE_STORE + idKey, "");
    }

    @Override
    public void deleteSubStorageId(int subId) {

    }

    private String byteToString(byte[] in) {
        return HexUtils.toHex(in);
    }

    private byte[] stringToByte(String in) {
        return HexUtils.toBytes(in);
    }

    private String idToString(byte[] id, int subId) {
        return "sub" + subId + "." + HexUtils.toHex(id);
    }

}
