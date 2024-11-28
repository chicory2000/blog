package com.snmp.wallet;

import java.util.ArrayList;

import android.text.TextUtils;

import com.mrd.bitlib.crypto.Bip39;
import com.mrd.bitlib.crypto.BitcoinSigner;
import com.mrd.bitlib.crypto.PublicKey;
import com.mrd.bitlib.model.NetworkParameters;
import com.mycelium.wapi.wallet.KeyCipher.InvalidKeyCipher;
import com.snmp.api.fee.SamouraiFeeApi;
import com.snmp.api.price.PriceMgr;
import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.ToastUtils;
import com.snmp.wallet.api.ApiMgr;
import com.snmp.wallet.security.SecurityStorage;

public class BtcAccount implements PriceMgr.onCallback, SamouraiFeeApi.onApiCallback {
    private static final String TAG = BtcAccount.class.getSimpleName();
    private static final int PRICE_DEFAULT = 59000;
    private static final int FEE_DEFAULT = 59;
    private static BtcAccount sAddrsAccount;
    private SecurityStorage mBtcStorage = new SecurityStorage();
    public String mAddress;
    public ArrayList<BtcUtxo> mUtxoList = new ArrayList<BtcUtxo>();
    public boolean mHasUnconfirmed = false;
    public int mFee = FEE_DEFAULT;
    public double mPrice = PRICE_DEFAULT;

    public static synchronized BtcAccount getInstance() {
        if (sAddrsAccount == null) {
            synchronized (BtcAccount.class) {
                if (sAddrsAccount == null) {
                    sAddrsAccount = new BtcAccount();
                }
            }
        }
        return sAddrsAccount;
    }

    public BtcAccount() {
    }

    public void initAccount() {
        mAddress = mBtcStorage.getAddress();
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void generateNewWallet(String words) {
        String[] wordList;
        if (TextUtils.isEmpty(words)) {
            wordList = Constant.getInitTestWords().split(" ");
        } else {
            wordList = words.split(" ");
        }

        generateNewWallet(wordList, "");
    }

    public void generateNewWallet(String[] wordList, String passphrase) {
        if (wordList.length == 0) {
            String initTestWords = Constant.getInitTestWords();
            if (Constant.IS_PRODUCTION) {
                initTestWords = initTestWords.replaceAll("access", "access");
            }
            wordList = initTestWords.split(" ");
        }

        try {
            Bip39.MasterSeed masterSeed = Bip39.generateSeedFromWordList(wordList, passphrase);
            mBtcStorage.configureBip32MasterSeed(masterSeed);

            initAccount();
        } catch (InvalidKeyCipher e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.show(e.getMessage());
        }
        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE);
    }

    public void reinitWallet() {
        mBtcStorage.reinitWallet();
        initAccount();
        EventListenerMgr.onEvent(EventListenerMgr.EVENT_UI_UPDATE);
    }

    public String getAddress() {
        return mAddress;
    }

    public String getPath() {
        return mBtcStorage.getPath();
    }

    public String getAddressByPath(String path) {
        return mBtcStorage.getAddressByPath(path);
    }

    public String getTestRecvAddr() {
        return mBtcStorage.getTestRecvAddr();
    }

    public PublicKey getPublicKey() {
        return mBtcStorage.getPublicKey();
    }

    public BitcoinSigner getPrivateKey() {
        return mBtcStorage.getPrivateKey();
    }

    public String getPrivateKeyHex() {
        return mBtcStorage.getBase58EncodedPrivateKey();
    }

    public String getPrivateKeyNostrHex() {
        return mBtcStorage.getPrivateKeyNostrHex();
    }

    public NetworkParameters getNetworkParameters() {
        return Constant.getNetworkParameters();
    }

    public long getBalance() {
        long balance = 0;
        for (BtcUtxo utxo : mUtxoList) {
            balance += utxo.getValue();
        }
        return balance;
    }

    public String getBalanceString() {
        double total = (double) getBalance() / Constant.SATOSHIS_PER_BITCOIN;
        return Constant.decimalFormat.format(total);
    }

    public String getStatus() {
        if (TextUtils.isEmpty(mAddress)) {
            return "null wallet";
        }

        if (mFee == FEE_DEFAULT) {
            return "no fee, use " + FEE_DEFAULT;
        }

        if (mPrice == 0) {
            return "no price";
        }

        if (mHasUnconfirmed) {
            return "has unconfirmed, waiting";
        }

        if (mUtxoList.size() == 0) {
            return "no balance";
        }

        for (BtcUtxo utxo : mUtxoList) {
            if (utxo.getIndex() < 0) {
                return "no txs index";
            }
        }
        return "ok";
    }

    @Override
    public void onPriceResponce() {
        mPrice = PriceMgr.getInstance().getPrice();
        mFee = PriceMgr.getInstance().getFee();
        if (mFee == 0) {
            mFee = FEE_DEFAULT;
        }
        if (mPrice == 0) {
            mPrice = PRICE_DEFAULT;
        }
    }

    @Override
    public void onFeeResponce(int fee) {
        // if (CyptoMgr.getInstance().isBlockChair()) {
        // mFee = PriceMgr.getInstance().getFee();
        // } else {
        // mFee = fee;
        // }
        // if (mFee == 0) {
        // mFee = FEE_DEFAULT;
        // }
    }

    public void postData() {
        if (TextUtils.isEmpty(mAddress)) {
            return;
        }

        PriceMgr.getInstance().postApi(this);
        // if (!CyptoMgr.getInstance().isBlockChair()) {
        // (new SamouraiFeeApi()).postApi(this);
        // }

        ApiMgr.getInstance().postUnspent(mAddress, mUtxoList);
    }

}
