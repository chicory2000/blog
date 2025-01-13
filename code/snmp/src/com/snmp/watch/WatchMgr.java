package com.snmp.watch;

import java.util.ArrayList;

import android.text.TextUtils;

import com.snmp.api.query.BaseQueryApi;
import com.snmp.api.query.BitapsApi;
import com.snmp.api.query.BlockChainApi;
import com.snmp.api.query.BlockChairApi;
import com.snmp.api.query.BlockCypherApi;
import com.snmp.api.query.MempoolApi;
import com.snmp.crypto.CyptoMgr;

public class WatchMgr {
    private static final String TAG = "QueryMgr";
    private static final int SECOND_1 = 1000;
    private static WatchMgr sWatchMgr;

    private int mApiNum = 0;
    private int sTypeCount = -1;
    private ArrayList<BaseQueryApi> mQueryApiList = new ArrayList<BaseQueryApi>();

    public static WatchMgr getInstance() {
        if (sWatchMgr == null) {
            synchronized (WatchMgr.class) {
                if (sWatchMgr == null) {
                    sWatchMgr = new WatchMgr();
                }
            }
        }
        return sWatchMgr;
    }

    private WatchMgr() {
        updateWatchApiList();
    }

    public void updateWatchApiList() {
        String apiNames = CyptoMgr.getInstance().getWatchType();
        apiNames = initDefaultApi(apiNames);

        mQueryApiList.clear();
        for (BaseQueryApi api : getAllWatchList()) {
            if (apiNames.contains(api.getApiName())) {
                mQueryApiList.add(api);
            }
        }
        mApiNum = mQueryApiList.size();
        sTypeCount = -1;
    }

    private String initDefaultApi(String apiNames) {
        if (TextUtils.isEmpty(apiNames)) {
            String defaultType = "";
            for (BaseQueryApi api : getAllWatchList()) {
                defaultType += api.getApiName() + ";";
            }
            apiNames = CyptoMgr.getInstance().setWatchDefaultType(defaultType);
        }
        return apiNames;
    }

    public ArrayList<BaseQueryApi> getAllWatchList() {
        ArrayList<BaseQueryApi> allQueryApiList = new ArrayList<BaseQueryApi>();

        allQueryApiList.add(new BlockCypherApi());
        allQueryApiList.add(new BlockChairApi());
        allQueryApiList.add(new BlockChainApi());
        allQueryApiList.add(new MempoolApi());
        //allQueryApiList.add(new BitapsApi());
        //allQueryApiList.add(new BlockStreamApi());
        //allQueryApiList.add(new SamouraiApi());
        return allQueryApiList;
    }

    public BaseQueryApi getNewApi() {
        sTypeCount++;

        int offset = sTypeCount % mApiNum;
        return mQueryApiList.get(offset);
    }

    public void sleepForApi(BaseQueryApi queryApi) {
        try {
            int sleepTime = SECOND_1 / queryApi.getCountLimitPerSecond();
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
