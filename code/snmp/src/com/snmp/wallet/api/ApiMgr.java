package com.snmp.wallet.api;

import java.util.ArrayList;
import java.util.HashMap;

import com.snmp.crypto.CyptoMgr;
import com.snmp.wallet.BtcUtxo;

public class ApiMgr {
    public static final String TYPE_BLOCKCYPHER = "blockcypher";
    public static final String TYPE_BLOCKSTREAM = "blockstream";
    public static final String TYPE_BTCCOM = "btccom";
    public static final String TYPE_CHAINSO = "chainso";
    public static final String TYPE_SAMOURAI = "samourai";
    public static final String TYPE_MEMPOOL = "mempool";
    public static final String TYPE_XVERSE = "xverse";

    private static ApiMgr sWalletMgr;
    private static HashMap<String, BaseSendTxApi> mSendTxList = new HashMap<String, BaseSendTxApi>();
    private static HashMap<String, BaseQueryApi> mUnspentList = new HashMap<String, BaseQueryApi>();

    public static ApiMgr getInstance() {
        if (sWalletMgr == null) {
            synchronized (ApiMgr.class) {
                if (sWalletMgr == null) {
                    sWalletMgr = new ApiMgr();
                }
            }
        }
        return sWalletMgr;
    }

    private ApiMgr() {
        mSendTxList.put(TYPE_BLOCKCYPHER, new com.snmp.wallet.api.blockcypher.SendTxApi());
        mSendTxList.put(TYPE_BLOCKSTREAM, new com.snmp.wallet.api.blockstream.SendTxApi());
        mSendTxList.put(TYPE_BTCCOM, new com.snmp.wallet.api.btccom.SendTxApi());
        mSendTxList.put(TYPE_CHAINSO, new com.snmp.wallet.api.chainso.SendTxApi());
        mSendTxList.put(TYPE_SAMOURAI, new com.snmp.wallet.api.samourai.SendTxApi());
        mSendTxList.put(TYPE_MEMPOOL, new com.snmp.wallet.api.mempool.SendTxApi());
        mSendTxList.put(TYPE_XVERSE, new com.snmp.wallet.api.xverse.SendTxApi());

        mUnspentList.put(TYPE_BLOCKCYPHER, new com.snmp.wallet.api.blockcypher.UnspentApi());
        mUnspentList.put(TYPE_BLOCKSTREAM, new com.snmp.wallet.api.blockstream.UnspentApi());
        mUnspentList.put(TYPE_BTCCOM, new com.snmp.wallet.api.btccom.UnspentApi());
        mUnspentList.put(TYPE_CHAINSO, new com.snmp.wallet.api.chainso.UnspentApi());
        mUnspentList.put(TYPE_SAMOURAI, new com.snmp.wallet.api.samourai.UnspentApi());
        mUnspentList.put(TYPE_MEMPOOL, new com.snmp.wallet.api.mempool.UnspentApi());
        mUnspentList.put(TYPE_XVERSE, new com.snmp.wallet.api.xverse.UnspentApi());
    }

    public String getType() {
        return CyptoMgr.getInstance().getWalletType();
    }

    public void sendTx(String tx) {
        mSendTxList.get(getType()).sendTx(tx);
    }

    public void postUnspent(String address, ArrayList<BtcUtxo> utxoList) {
        BaseQueryApi unspentApi = mUnspentList.get(getType());
        unspentApi.init(address, utxoList);
        unspentApi.postApi();
    }

}
