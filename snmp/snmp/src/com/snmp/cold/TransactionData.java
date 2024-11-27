package com.snmp.cold;

import java.util.ArrayList;

import com.snmp.wallet.BtcUtxo;

public class TransactionData {
    public String mLocalAddr = "";
    public String mRecipientAddr = "";
    public long mRecipientValue = 0;
    public int mFee = 0;
    public long mPrice = 0;
    public ArrayList<BtcUtxo> mUtxoList = new ArrayList<BtcUtxo>();
    public String mSendTx = "";
    public String mSendCost = "";
    public String mSendFee = "";
    public String mSendDetail = "";

    public TransactionData() {
    }

    @Override
    public String toString() {
        return super.toString();
    }

}
