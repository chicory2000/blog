package com.snmp.api.price;

import com.snmp.crypto.CyptoMgr;

public class PriceMgr implements PriceApi.onApiCallback {
    private static final String TAG = PriceMgr.class.getSimpleName();
    private static PriceMgr sWatchMgr;

    private WorldCoinApi mWorldCoinPriceApi = new WorldCoinApi();;
    private BlockChairApi mBlockChairApi = new BlockChairApi();
    private int mPrice = 0;
    private int mFee = 0;
    private String mShow = "";
    private onCallback mCallback;

    public static PriceMgr getInstance() {
        if (sWatchMgr == null) {
            synchronized (PriceMgr.class) {
                if (sWatchMgr == null) {
                    sWatchMgr = new PriceMgr();
                }
            }
        }
        return sWatchMgr;
    }

    public interface onCallback {
        void onPriceResponce();
    }

    private PriceMgr() {
    }

    public int getPrice() {
        return mPrice;
    }

    public int getFee() {
        return mFee;
    }

    public String getShow() {
        return mShow;
    }

    @Override
    public void onPriceResponce(int price, String show, int fee) {
        mPrice = price;
        mShow = show;
        mFee = fee;
        mCallback.onPriceResponce();
    }

    public void postApi(onCallback callback) {
        mCallback = callback;
        //if (CyptoMgr.getInstance().isBlockChair()) {
            mBlockChairApi.postApi(this);
        //} else {
            //mWorldCoinPriceApi.postApi(this);
        //}

    }

}
