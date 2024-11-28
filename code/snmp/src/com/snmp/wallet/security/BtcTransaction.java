package com.snmp.wallet.security;

import java.util.ArrayList;
import java.util.List;

import com.mrd.bitlib.StandardTransactionBuilder;
import com.mrd.bitlib.UnsignedTransaction;
import com.mrd.bitlib.crypto.BitcoinSigner;
import com.mrd.bitlib.crypto.IPrivateKeyRing;
import com.mrd.bitlib.crypto.IPublicKeyRing;
import com.mrd.bitlib.crypto.PublicKey;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.BitcoinTransaction;
import com.mrd.bitlib.model.NetworkParameters;
import com.mrd.bitlib.model.TransactionInput;
import com.mrd.bitlib.model.TransactionOutput;
import com.mrd.bitlib.model.UnspentTransactionOutput;
import com.mrd.bitlib.util.HexUtils;
import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.LogUtils;
import com.snmp.utils.ToastUtils;
import com.snmp.utils.Utils;
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.BtcUtxo;
import com.snmp.wallet.Constant;

public class BtcTransaction {
    private static final String TAG = BtcTransaction.class.getSimpleName();
    private UnsignedTransaction mUnsignedTransaction = null;
    private BitcoinTransaction mTransaction;
    public String mRecipientAddress = "";
    public long mRecipientValue = 0;
    public String mDetail = "";

    private final IPublicKeyRing KEY_RING = new IPublicKeyRing() {
        @Override
        public PublicKey findPublicKeyByAddress(BitcoinAddress address) {
            return BtcAccount.getInstance().getPublicKey();
        }
    };

    private final IPrivateKeyRing PRIVATE_KEY_RING = new IPrivateKeyRing() {
        @Override
        public BitcoinSigner findSignerByPublicKey(PublicKey publicKey) {
            return BtcAccount.getInstance().getPrivateKey();
        }
    };

    public BtcTransaction() {
    }

    public BtcTransaction createTransaction(String recipientAddress, long sendValue) {
        mRecipientAddress = recipientAddress;
        mRecipientValue = sendValue;

        BtcAccount btcAccount = BtcAccount.getInstance();
        NetworkParameters networkParameters = btcAccount.getNetworkParameters();
        BitcoinAddress btcSend = BitcoinAddress.fromString(btcAccount.getAddress());

        LogUtils.i(TAG, "send address= " + BtcAccount.getInstance().getPublicKey() + " ");

        try {
            StandardTransactionBuilder stb = new StandardTransactionBuilder(networkParameters);

            BitcoinAddress btcReceive = BitcoinAddress.fromString(recipientAddress);
            if (btcReceive == null) {
                ToastUtils.show("invalid btc address");
                return null;
            }

            // add output
            stb.addOutput(btcReceive, sendValue);

            ArrayList<UnspentTransactionOutput> utxos = BtcUtxo.getUtxoList(btcAccount.mUtxoList);

            int minerFeeKb = BtcAccount.getInstance().mFee * 1000 / 1;

            // create unsigned transaction
            mUnsignedTransaction = stb.createUnsignedTransaction(utxos, btcSend, KEY_RING, networkParameters,
                    minerFeeKb);
            LogUtils.i(TAG, "send transaction \n" + mUnsignedTransaction);

            // generate signatures
            List<byte[]> signatures = StandardTransactionBuilder.generateSignatures(
                    mUnsignedTransaction.getSigningRequests(), PRIVATE_KEY_RING);

            // generate btc transaction
            mTransaction = StandardTransactionBuilder.finalizeTransaction(mUnsignedTransaction, signatures);
            LogUtils.i(TAG, "send transaction \n" + mTransaction);

            onDetailLog(networkParameters);
        } catch (Exception e) {
            e.printStackTrace();

            String message = e.getMessage();
            ToastUtils.show(message);
            EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE, message);
            return null;
        }
        return this;
    }

    private void onDetailLog(NetworkParameters networkParameters) {
        TransactionInput[] inputs = mTransaction.inputs;
        mDetail = "inputs:(" + inputs.length + ")";
        for (TransactionInput input : inputs) {
            mDetail += "\n" + Utils.getBalance(input.getValue());
        }

        TransactionOutput[] outputs = mTransaction.outputs;
        mDetail += "\noutputs:(" + outputs.length + ")";
        for (TransactionOutput output : outputs) {
            mDetail += "\n" + output.script.getAddress(networkParameters) + ": " + Utils.getBalance(output.value);
        }
    }

    public double getRawFee(long sendValue) {
        if (mUnsignedTransaction == null) {
            return 0;
        }
        return ((double) mUnsignedTransaction.calculateFee());
    }

    public double getFee(long sendValue, long btcPrice) {
        if (mUnsignedTransaction == null) {
            return 0;
        }
        return ((double) mUnsignedTransaction.calculateFee()) * btcPrice / Constant.SATOSHIS_PER_BITCOIN;
    }

    public String getSendTx() {
        return HexUtils.toHex(mTransaction.toBytes(false));
    }

    public String getBtcTransactionDetail() {
        return mDetail;
    }

}
