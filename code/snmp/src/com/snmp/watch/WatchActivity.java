package com.snmp.watch;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.snmp.api.price.PriceMgr;
import com.snmp.api.query.BaseQueryApi;
import com.snmp.crypto.CyptoMgr;
import com.snmp.crypto.R;
import com.snmp.utils.PreferenceManager;
import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ThreadPoolUtil;
import com.snmp.watch.WatchListView.WatchListAdapter;

public class WatchActivity extends Activity implements PriceMgr.onCallback, WatchUtils.onParseCallback {
    private static final String TAG = "watch";
    private String mAddress = "1EzwoHtiXB4iFwedPr49iywjZn2nnekhoj";
    private ArrayList<WatchItemData> mWatchList = new ArrayList<WatchItemData>();
    private TextView mTxtApiName;
    private TextView mTxtPrice;
    private TextView mTxtTotal;
    private int mAddressResponse = 0;
    private WatchListView mWatchListView;
    public boolean mEncrypt = true;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = new Intent("com.snmp.widget.WidgetTimeService");
        intent.setPackage(getPackageName());
        startService(intent);
        if (Math.abs(PreferenceManager.getLong("last_enter_time", 0) - System.currentTimeMillis()) > 100) {
            PreferenceManager.putLong("last_enter_time", System.currentTimeMillis());
            finish();
            return;
        }
        setContentView((int) R.layout.crypto_watch);
        View root = findViewById(R.id.crypto_root);
        initView(root);
    }

    public void initView(View root) {
        mWatchListView = (WatchListView) root.findViewById(R.id.watch_listview);
        mWatchListView.init();
        mWatchListView.setBackgroundColor(getResources().getColor(R.color.title_background));

        mTxtApiName = (TextView) root.findViewById(R.id.txt_api_name);
        mTxtPrice = (TextView) root.findViewById(R.id.txt_price);
        mTxtTotal = (TextView) root.findViewById(R.id.txt_total);
        TextView title = (TextView) root.findViewById(R.id.crypto_home_title_txt);
        title.setText("watch");

        root.findViewById(R.id.req_address).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                requestList();
            }
        });

        root.findViewById(R.id.btn_input).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                WatchInputDialog.showInput(WatchActivity.this);
            }
        });

        root.findViewById(R.id.btn_clear).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!CyptoMgr.getInstance().isEncrypt()) {
                    PreferenceManager.putString("address_list", "");
                }
            }
        });

        root.findViewById(R.id.btn_copy).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (!CyptoMgr.getInstance().isEncrypt()) {
                    PreferenceManager.putString("address_list", mAddress + "@" + mAddress + "@" + mAddress);
                }
            }
        });

        root.findViewById(R.id.crypto_home_title_setting_btn).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                // intent.setClassName(SnmpApplication.getInstance().getPackageName(),
                // SettingsActivity.class.getName());
                // intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                // SnmpApplication.getInstance().startActivity(intent);
            }
        });
        PriceMgr.getInstance().postApi(this);
        updateVisibility();
    }

    public void updateVisibility() {
        View root = findViewById(R.id.crypto_root);
        if (mEncrypt) {
            root.findViewById(R.id.req_address).setVisibility(View.GONE);
            root.findViewById(R.id.txt_api_name).setVisibility(View.GONE);
            root.findViewById(R.id.txt_price).setVisibility(View.GONE);
        } else {
            root.findViewById(R.id.req_address).setVisibility(View.VISIBLE);
            root.findViewById(R.id.txt_api_name).setVisibility(View.VISIBLE);
            root.findViewById(R.id.txt_price).setVisibility(View.VISIBLE);
        }
    }

    private void requestList() {
        if (mEncrypt) {
            return;
        }
        mWatchList.clear();
        updateWatchList();

        mAddressResponse = 0;
        mTxtApiName.setText("");
        mTxtTotal.setText("");

        ThreadPoolUtil.post(new Runnable() {
            @Override
            public void run() {
                getListDetail();
            }
        });
    }

    @Override
    public void updateWatchList() {
        SnmpApplication.post(new Runnable() {
            @Override
            public void run() {
                ListAdapter adapter = mWatchListView.getAdapter();
                ((WatchListAdapter) adapter).refresh(mWatchList);
            }
        });
    }

    @Override
    public void onParse(BaseQueryApi queryApi, WatchItemData watchItem, String data) {
        mAddressResponse++;
        long balance = queryApi.getBalance(data);
        if (balance < 0) {
            watchItem.mQueryResult = "parse fail";
            updateWatchList();
            return;
        }

        watchItem.mQueryResult = "";
        watchItem.mBalance = balance;
        updateWatchList();

        setTotalText();
    }

    private void setTotalText() {
        if (mAddressResponse >= mWatchList.size()) {
            int totalBalance = 0;
            for (WatchItemData watchItemData : mWatchList) {
                totalBalance += watchItemData.getBalance();
            }

            double total = (double) totalBalance / 100000000;
            int t = (int) (PriceMgr.getInstance().getPrice() * total);
            mTxtTotal.setText(Constant.decimalFormat.format(total) + "\n" + t);
            if (CyptoMgr.getInstance().isEncrypt()) {
                mTxtTotal.setText("");
            }
        }
    }

    private void getListDetail() {
        String prelist = PreferenceManager.getString("address_list", "");
        ArrayList<String> addressList = new ArrayList<String>(Arrays.asList(prelist.split("@")));
        // LogUtils.d(TAG, "postData " + addressList);

        PriceMgr.getInstance().postApi(this);

        BaseQueryApi queryApi = WatchMgr.getInstance().getNewApi();
        if (CyptoMgr.getInstance().isEncrypt()) {
            mTxtApiName.setText(queryApi.getApiName().substring(0, 7));
        } else {
            mTxtApiName.setText(queryApi.getApiName());
        }

        for (String address : addressList) {
            if (TextUtils.isEmpty(address) || address.length() < 20) {
                continue;
            }

            WatchItemData watchItemData = new WatchItemData(address);
            watchItemData.mApiName = queryApi.getApiName();
            mWatchList.add(watchItemData);

            queryApi.mAddress = address;
            WatchUtils.postAddress(this, queryApi, watchItemData);

            WatchMgr.getInstance().sleepForApi(queryApi);
        }

        updateWatchList();
    }

    @Override
    public void onPriceResponce() {
        String des = "price:  ";
        if (CyptoMgr.getInstance().isEncrypt()) {
            des = "";
        }
        mTxtPrice.setText(des + PriceMgr.getInstance().getShow());
    }

}
