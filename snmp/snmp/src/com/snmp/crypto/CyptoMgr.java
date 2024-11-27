package com.snmp.crypto;

import com.snmp.utils.PreferenceManager;

public class CyptoMgr {
    private static final String API_WALLET_TYPE = "crypto_wallet_api_type";
    private static final String API_WATCH_TYPE = "crypto_watch_api_type";
    private static final String ADDR_ENCRYPT = "crypto_add_encrypt";
    private static final String API_BLOCKCHAIR = "crypto_api_blockchair";
    private static CyptoMgr sCyptoMgr;

    public static CyptoMgr getInstance() {
        if (sCyptoMgr == null) {
            synchronized (CyptoMgr.class) {
                if (sCyptoMgr == null) {
                    sCyptoMgr = new CyptoMgr();
                }
            }
        }
        return sCyptoMgr;
    }

    private CyptoMgr() {
    }

    public String getWalletType() {
        String type = PreferenceManager.getString(API_WALLET_TYPE, "blockcypher");
        return type;
    }

    public void setWalletType(String type) {
        PreferenceManager.putString(API_WALLET_TYPE, type);
    }

    public String getWatchType() {
        String type = PreferenceManager.getString(API_WATCH_TYPE, "");
        return type;
    }

    public String setWatchDefaultType(String defaultType) {
        PreferenceManager.putString(API_WATCH_TYPE, defaultType);
        return defaultType;
    }

    public void setWatchType(String type) {
        String lastType = getWatchType();

        lastType += type + ";";
        PreferenceManager.putString(API_WATCH_TYPE, lastType);
    }

    public void removeWatchType(String type) {
        String lastType = getWatchType();

        lastType = lastType.replaceAll(type + ";", "");
        PreferenceManager.putString(API_WATCH_TYPE, lastType);
    }

    public void setEncrypt(boolean encrypt) {
        PreferenceManager.putBoolean(ADDR_ENCRYPT, encrypt);
    }

    public boolean isEncrypt() {
        return PreferenceManager.getBoolean(ADDR_ENCRYPT, false);
    }

    public void setBlockChair(boolean encrypt) {
        PreferenceManager.putBoolean(API_BLOCKCHAIR, encrypt);
    }

    public boolean isBlockChair() {
        return PreferenceManager.getBoolean(API_BLOCKCHAIR, false);
    }
}
