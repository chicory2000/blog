package com.snmp.crypto;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

public abstract class CryptoFragment extends FrameLayout {
    public Activity mActivity;

    public CryptoFragment(Activity activity) {
        super(activity);
        mActivity = activity;
        LayoutInflater.from(getContext()).inflate(onGetLayoutId(), this);
        initView(this);
    }

    public Activity getActivity() {
        return mActivity;
    }

    public abstract int onGetLayoutId();

    public abstract void initView(View root);

    public abstract String getName();
}
