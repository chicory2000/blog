package com.snmp.crypto;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.snmp.api.query.BaseQueryApi;
import com.snmp.crypto.R;
import com.snmp.utils.LogUtils;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.Constant;
import com.snmp.wallet.api.ApiMgr;
import com.snmp.wallet.pin.PinMgr;
import com.snmp.watch.WatchMgr;

public final class SettingsActivity extends Activity {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private PinMgr mPinMgr = PinMgr.getInstance();
    private Switch mEnvi;
    private Switch setPin;
    private Switch setPinRequiredStartup;
    private Switch randomizePin;
    private CheckBox mCheckBox01;
    private CheckBox mCheckBox02;
    private CheckBox mCheckBox03;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.crypto_settings);
        initView();
        update();
    }

    private Activity getActivity() {
        return this;
    }

    private void initView() {
        findViewById(R.id.crypto_setting_title).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                SettingsActivity.this.finish();
            }
        });

        View envi = findViewById(R.id.crypto_setting_envi);
        mEnvi = setItem(envi, "envi", "", true);
        mEnvi.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Constant.IS_PRODUCTION = !Constant.IS_PRODUCTION;
                PreferenceManager.putBoolean("snmp_crypto_production", Constant.IS_PRODUCTION);
                update();
            }
        });

        initWalletApi();
        initWatchApi();

        initPinSettings();

        View clearStorage = findViewById(R.id.crypto_setting_clear_storage);
        setItem(clearStorage, "clear storage", "", false);
        clearStorage.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // onClickRandomizePin();
            }
        });

        View about = findViewById(R.id.crypto_setting_about);
        setItem(about, "about", "", false);
        about.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // onClickRandomizePin();
            }
        });
    }

    private void initPinSettings() {
        View pinSet = findViewById(R.id.crypto_setting_pin_set);
        setPin = setItem(pinSet, getString(R.string.pref_set_pin), getString(R.string.pref_set_pin_summary), true);
        setPin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onClickSetPin();
            }
        });

        View pinStartup = findViewById(R.id.crypto_setting_pin_startup);
        setPinRequiredStartup = setItem(pinStartup, getString(R.string.pref_pin_require_on_startup),
                getString(R.string.pref_pin_require_on_startup_summary), true);
        setPinRequiredStartup.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onClickSetPinStartup();
            }
        });

        View pinRandomized = findViewById(R.id.crypto_setting_pin_randomized);
        randomizePin = setItem(pinRandomized, getString(R.string.randomize_pin),
                getString(R.string.randomize_pin_text), true);
        randomizePin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                onClickRandomizePin();
            }
        });

    }

    private void initWatchApi() {
        View watchApi = findViewById(R.id.crypto_setting_watch_api);
        final CheckBox watchCheck1 = (CheckBox) watchApi.findViewById(R.id.crypto_setting_watch_api_btn_1);
        final CheckBox watchCheck2 = (CheckBox) watchApi.findViewById(R.id.crypto_setting_watch_api_btn_2);
        final CheckBox watchCheck3 = (CheckBox) watchApi.findViewById(R.id.crypto_setting_watch_api_btn_3);
        final CheckBox watchCheck4 = (CheckBox) watchApi.findViewById(R.id.crypto_setting_watch_api_btn_4);
        final TextView watchText1 = (TextView) watchApi.findViewById(R.id.crypto_setting_watch_api_txt_1);
        final TextView watchText2 = (TextView) watchApi.findViewById(R.id.crypto_setting_watch_api_txt_2);
        final TextView watchText3 = (TextView) watchApi.findViewById(R.id.crypto_setting_watch_api_txt_3);
        final TextView watchText4 = (TextView) watchApi.findViewById(R.id.crypto_setting_watch_api_txt_4);

        ArrayList<CheckBox> checkBoxList = new ArrayList<CheckBox>();
        checkBoxList.add(watchCheck1);
        checkBoxList.add(watchCheck2);
        checkBoxList.add(watchCheck3);
        checkBoxList.add(watchCheck4);

        ArrayList<TextView> textList = new ArrayList<TextView>();
        textList.add(watchText1);
        textList.add(watchText2);
        textList.add(watchText3);
        textList.add(watchText4);

        String apiNames = CyptoMgr.getInstance().getWatchType();

        for (int i = 0; i < WatchMgr.getInstance().getAllWatchList().size(); i++) {
            BaseQueryApi api = WatchMgr.getInstance().getAllWatchList().get(i);
            final String apiName = api.getApiName();

            final CheckBox watchCheck = checkBoxList.get(i);
            watchCheck.setChecked(apiNames.contains(apiName));
            initWatchApiItem(apiName, watchCheck, textList.get(i));
        }
    }

    private void initWatchApiItem(final String apiName, final CheckBox watchCheck, final TextView watchText) {
        watchText.setText(apiName);
        watchCheck.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean isChecked = watchCheck.isChecked();
                if (isChecked) {
                    CyptoMgr.getInstance().setWatchType(apiName);
                } else {
                    CyptoMgr.getInstance().removeWatchType(apiName);
                }
                WatchMgr.getInstance().updateWatchApiList();
            }
        });
    }

    private void initWalletApi() {
        View walletApi = findViewById(R.id.crypto_setting_wallet_api);
        final TextView walletapi1 = (TextView) walletApi.findViewById(R.id.crypto_setting_wallet_api_txt_1);
        final TextView walletapi2 = (TextView) walletApi.findViewById(R.id.crypto_setting_wallet_api_txt_2);
        final TextView walletapi3 = (TextView) walletApi.findViewById(R.id.crypto_setting_wallet_api_txt_3);

        mCheckBox01 = (CheckBox) walletApi.findViewById(R.id.crypto_setting_wallet_api_btn_1);
        mCheckBox01.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean isChecked = mCheckBox01.isChecked();
                if (isChecked) {
                    CyptoMgr.getInstance().setWalletType(ApiMgr.TYPE_BLOCKCYPHER);
                    mCheckBox01.setEnabled(false);
                    mCheckBox02.setEnabled(true);
                    mCheckBox03.setEnabled(true);

                    mCheckBox02.setChecked(false);
                    mCheckBox03.setChecked(false);
                }
            }
        });

        mCheckBox02 = (CheckBox) walletApi.findViewById(R.id.crypto_setting_wallet_api_btn_2);
        mCheckBox02.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean isChecked = mCheckBox02.isChecked();
                if (isChecked) {
                    CyptoMgr.getInstance().setWalletType(ApiMgr.TYPE_BLOCKSTREAM);
                    mCheckBox01.setEnabled(true);
                    mCheckBox02.setEnabled(false);
                    mCheckBox03.setEnabled(true);

                    mCheckBox01.setChecked(false);
                    mCheckBox03.setChecked(false);
                }
            }
        });

        mCheckBox03 = (CheckBox) walletApi.findViewById(R.id.crypto_setting_wallet_api_btn_3);
        mCheckBox03.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean isChecked = mCheckBox03.isChecked();
                if (isChecked) {
                    CyptoMgr.getInstance().setWalletType(ApiMgr.TYPE_MEMPOOL);
                    mCheckBox01.setEnabled(true);
                    mCheckBox02.setEnabled(true);
                    mCheckBox03.setEnabled(false);

                    mCheckBox02.setChecked(false);
                    mCheckBox01.setChecked(false);
                }
            }
        });

        String type = CyptoMgr.getInstance().getWalletType();
        if (ApiMgr.TYPE_BLOCKCYPHER.equals(type)) {
            mCheckBox01.setChecked(true);

            mCheckBox01.setEnabled(false);
            mCheckBox02.setEnabled(true);
            mCheckBox03.setEnabled(true);
        } else if (ApiMgr.TYPE_BLOCKSTREAM.equals(type)) {
            mCheckBox02.setChecked(true);

            mCheckBox01.setEnabled(true);
            mCheckBox02.setEnabled(false);
            mCheckBox03.setEnabled(true);
        } else if (ApiMgr.TYPE_MEMPOOL.equals(type)) {
            mCheckBox03.setChecked(true);

            mCheckBox01.setEnabled(true);
            mCheckBox02.setEnabled(true);
            mCheckBox03.setEnabled(false);
        }
        walletapi1.setText(ApiMgr.TYPE_BLOCKCYPHER);
        walletapi2.setText(ApiMgr.TYPE_BLOCKSTREAM);
        walletapi3.setText(ApiMgr.TYPE_MEMPOOL);
    }

    private Switch setItem(View itemRoot, String title, String summer, boolean isSwitch) {
        TextView titleView = (TextView) itemRoot.findViewById(R.id.crypto_setting_item_title);
        TextView summerView = (TextView) itemRoot.findViewById(R.id.crypto_setting_item_summary);
        Switch switchView = (Switch) itemRoot.findViewById(R.id.crypto_setting_item_switch);
        ImageView arrow = (ImageView) itemRoot.findViewById(R.id.crypto_setting_item_arrow);

        titleView.setText(title);
        if (TextUtils.isEmpty(summer)) {
            summerView.setVisibility(View.GONE);
        } else {
            summerView.setVisibility(View.VISIBLE);
            summerView.setText(summer);
        }
        if (isSwitch) {
            switchView.setVisibility(View.VISIBLE);
            arrow.setVisibility(View.GONE);
        } else {
            switchView.setVisibility(View.GONE);
            arrow.setVisibility(View.VISIBLE);
        }
        return switchView;
    }

    void update() {
        mEnvi.setChecked(Constant.IS_PRODUCTION);
        setPin.setChecked(mPinMgr.isPinProtected());
        setPinRequiredStartup.setChecked(mPinMgr.isPinProtected() && mPinMgr.getPinRequiredOnStartup());
        randomizePin.setChecked(mPinMgr.isPinProtected() && mPinMgr.isPinPadRandomized());
    }

    private void onClickSetPin() {
        Runnable afterDialogClosed = new Runnable() {
            @Override
            public void run() {
                update();
            }
        };
        LogUtils.d(TAG, "onClick " + setPin.isChecked());

        setPin.setChecked(!setPin.isChecked());
        if (!setPin.isChecked()) {
            mPinMgr.showSetPinDialog(getActivity(), afterDialogClosed);
        } else {
            mPinMgr.showClearPinDialog(getActivity(), afterDialogClosed);
        }
    }

    private void onClickSetPinStartup() {
        setPinRequiredStartup.setChecked(!setPinRequiredStartup.isChecked());
        mPinMgr.runPinProtectedFunction(getActivity(), new Runnable() {
            @Override
            public void run() {
                boolean checked = !setPinRequiredStartup.isChecked();
                mPinMgr.setPinRequiredOnStartup(checked);
                update();
            }
        });
    }

    private void onClickRandomizePin() {
        randomizePin.setChecked(!randomizePin.isChecked());
        mPinMgr.runPinProtectedFunction(getActivity(), new Runnable() {
            @Override
            public void run() {
                if (mPinMgr.isPinProtected()) {
                    mPinMgr.setPinPadRandomized(!randomizePin.isChecked());
                } else {
                    mPinMgr.setPinPadRandomized(false);
                }
                update();
            }
        });
    }

}
