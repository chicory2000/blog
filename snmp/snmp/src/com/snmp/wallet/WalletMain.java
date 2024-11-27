package com.snmp.wallet;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.snmp.crypto.CryptoFragment;
import com.snmp.crypto.R;
import com.snmp.crypto.SettingsActivity;
import com.snmp.utils.EventListener;
import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.LogUtils;
import com.snmp.utils.PreferenceManager;
import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ToastUtils;
import com.snmp.utils.Utils;
import com.snmp.wallet.pin.PinMgr;
import com.snmp.wallet.security.BtcTransaction;
import com.snmp.wallet.ui.EnterWordListActivity;
import com.snmp.wallet.ui.JsonDetailActivity;
import com.snmp.wallet.ui.PassphraseInputDialog;
import com.snmp.wallet.ui.PathDialog;
import com.snmp.wallet.ui.ReceiveDialog;
import com.snmp.wallet.ui.SendDialog;

public class WalletMain extends CryptoFragment {
    private static final String TAG = "wallet";
    private TextView mTxtBtcAddress;
    private TextView mTxtBtcPath;
    private TextView mTxtBtcPass;
    private EditText mTxtReceiveAddress;
    private EditText mTxtReceiveValue;
    private EditText mTxtFee;
    private TextView mTxtStatus;
    private TextView mTxtBalance;
    private TextView mTxtValue;
    private TextView mTxtApiStatus;
    private View mBtcInfo;

    private String mRecipientAddress;
    private double mRecipientValue = 10000;
    private EventListener mListener;

    public WalletMain(Activity activity) {
        super(activity);
    }

    @Override
    public int onGetLayoutId() {
        return R.layout.crypto_wallet;
    }

    @Override
    public String getName() {
        return TAG;
    }

    public void initView(View root) {
        mBtcInfo = (View) root.findViewById(R.id.wallet_btc_info);
        mTxtBtcAddress = (TextView) root.findViewById(R.id.wallet_btc_address);
        mTxtBtcPath = (TextView) root.findViewById(R.id.wallet_btc_path);
        mTxtBtcPass = (TextView) root.findViewById(R.id.wallet_btc_pass);
        mTxtReceiveAddress = (EditText) root.findViewById(R.id.wallet_receive_address);
        mTxtReceiveValue = (EditText) root.findViewById(R.id.wallet_receive_value);
        mTxtFee = (EditText) root.findViewById(R.id.wallet_receive_fee);
        mTxtBalance = (TextView) root.findViewById(R.id.wallet_balance);
        mTxtStatus = (TextView) root.findViewById(R.id.wallet_status);
        mTxtValue = (TextView) root.findViewById(R.id.wallet_value);
        mTxtApiStatus = (TextView) root.findViewById(R.id.wallet_api_status);

        TextView title = (TextView) root.findViewById(R.id.crypto_home_title_txt);
        title.setText("wallet");

        root.findViewById(R.id.btn_input_words).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // InputWordsDialog.showInput(WalletMain.this);
                Intent intent = new Intent();
                intent.setClassName(SnmpApplication.getInstance().getPackageName(),
                        EnterWordListActivity.class.getName());
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                SnmpApplication.getInstance().startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_path).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PathDialog.showDialog(WalletMain.this.getActivity());
            }
        });

        root.findViewById(R.id.btn_nostr).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                copyNostr();
            }
        });

        root.findViewById(R.id.btn_receive).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReceiveDialog.showQr(WalletMain.this.getActivity(), BtcAccount.getInstance().getAddress());
            }
        });

        root.findViewById(R.id.btn_refresh).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                refresh();
            }
        });

        root.findViewById(R.id.btn_send).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                send();
            }
        });

        root.findViewById(R.id.btn_passphrase).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //PassphraseInputDialog.showInput(WalletMain.this.getActivity());
                Intent intent = new Intent();
                intent.setClassName(SnmpApplication.getInstance().getPackageName(), JsonDetailActivity.class.getName());
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                SnmpApplication.getInstance().startActivity(intent);
            }
        });

        root.findViewById(R.id.crypto_home_title_setting_btn).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(SnmpApplication.getInstance().getPackageName(), SettingsActivity.class.getName());
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                SnmpApplication.getInstance().startActivity(intent);
            }
        });
        initInfoPanel(root);

        start();
    }

    private void initInfoPanel(final View root) {
        final int[] colors = new int[] { getResources().getColor(R.color.bitcoin_color_start),
                getResources().getColor(R.color.bitcoin_color_end) };
        GradientDrawable mGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BR_TL, colors);
        mGradientDrawable.setCornerRadius(15);
        mBtcInfo.setBackgroundDrawable(mGradientDrawable);

        root.findViewById(R.id.wallet_menu).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                View btsMenu = root.findViewById(R.id.btns_menu);
                btsMenu.setVisibility(btsMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void start() {
        initAccount();
    }

    private void initAccount() {
        BtcAccount.getInstance().initAccount();
        mRecipientAddress = BtcAccount.getInstance().getTestRecvAddr();
        refresh();

        initResult("init");
        mListener = new EventListener() {
            @Override
            public void onEvent(final int key, final Object... params) {
                SnmpApplication.post(new Runnable() {
                    @Override
                    public void run() {
                        String result = "";
                        if (params != null && params[0] instanceof String) {
                            result = (String) params[0];
                        }
                        initResult(result);
                    }
                });
            }
        };
        EventListenerMgr.addListener(mListener, EventListenerMgr.EVENT_UI_UPDATE);
    }

    private void initResult(String result) {
        mTxtBtcAddress.setText(BtcAccount.getInstance().getAddress());
        mTxtReceiveAddress.setText(mRecipientAddress);
        mTxtReceiveValue.setText(Utils.getBalance(mRecipientValue));
        mTxtBtcPath.setText(BtcAccount.getInstance().getPath() + " : " + BtcAccount.getInstance().mPrice + " : "
                + BtcAccount.getInstance().mFee);
        mTxtApiStatus.setText("api: " + result);

        String balanceString = Utils.getBalance(BtcAccount.getInstance().getBalance());
        mTxtBalance.setText(balanceString);

        String status = BtcAccount.getInstance().getStatus();
        if (status.equals("ok")) {
            double total = (double) BtcAccount.getInstance().getBalance() / Constant.SATOSHIS_PER_BITCOIN;
            long btcPrice = (long) BtcAccount.getInstance().mPrice;
            int value = (int) (btcPrice * total);
            mTxtValue.setText("value: " + +value);
        }
        updateStatus();
        updateForPass();
    }

    private void updateStatus() {
        String status = BtcAccount.getInstance().getStatus();
        mTxtStatus.setText("status: " + status);

        if (status.equals("ok")) {
            mTxtStatus.setTextColor(Color.BLUE);
        } else {
            mTxtStatus.setTextColor(Color.BLACK);
        }
    }

    private void refresh() {
        if (PinMgr.getInstance().isUnlockPinRequired()) {
            return;
        }

        mRecipientAddress = BtcAccount.getInstance().getTestRecvAddr();
        mTxtBtcPath.setText(BtcAccount.getInstance().getPath());
        mTxtBtcAddress.setText(BtcAccount.getInstance().getAddress());
        mTxtReceiveAddress.setText(mRecipientAddress);

        mTxtStatus.setText("");
        mTxtBalance.setText("0");
        mTxtValue.setText("0");
        PreferenceManager.putString("wallet_bc_utxos_json", "");
        PreferenceManager.putString("wallet_so_utxos_json", "");
        BtcAccount.getInstance().postData();
        updateForPass();
    }

    private void updateForPass() {
        String pass = PreferenceManager.getString("wallet_passphrase", "");
        if (pass.length() < 3) {
            return;
        }
        mTxtBtcPass.setText(pass.substring(2));
        if (PinMgr.getInstance().isUnlockPinRequired()) {
            mTxtBtcPass.setText("");
        }
    }

    private void copyNostr() {
        if (PinMgr.getInstance().isUnlockPinRequired()) {
            Runnable start = new Runnable() {
                @Override
                public void run() {
                    PinMgr.getInstance().setStartUpPinUnlocked(true);
                }
            };

            PinMgr.getInstance().runPinProtectedFunction(getActivity(), start, false);
            return;
        }
        String pass = PreferenceManager.getString("wallet_passphrase", "");
        String privateKeyHex = BtcAccount.getInstance().getPrivateKeyHex();
        if (pass.contains("nostr")) {
            privateKeyHex = BtcAccount.getInstance().getPrivateKeyNostrHex();
        }

        if (!TextUtils.isEmpty(privateKeyHex)) {
            ClipboardManager service = getActivity().getSystemService(ClipboardManager.class);
            String label = "nostr";
            service.setPrimaryClip(ClipData.newPlainText(label, privateKeyHex));
            Toast.makeText(getActivity(), "suscess copy nostr", Toast.LENGTH_SHORT).show();
        }
    }

    private void send() {
        if (PinMgr.getInstance().isUnlockPinRequired()) {
            Runnable start = new Runnable() {
                @Override
                public void run() {
                    PinMgr.getInstance().setStartUpPinUnlocked(true);
                }
            };

            PinMgr.getInstance().runPinProtectedFunction(getActivity(), start, false);
            return;
        }
        if (!TextUtils.isEmpty(mTxtFee.getText().toString())) {
            BtcAccount.getInstance().mFee = Integer.parseInt(mTxtFee.getText().toString());
        }

        String recipientAddress = mTxtReceiveAddress.getText().toString();
        // recipientAddress = recipientAddress.replace("9k", "99");
        String editString = mTxtReceiveValue.getText().toString();
        long sendValue = (long) (Double.parseDouble(editString) * Constant.SATOSHIS_PER_BITCOIN);

        final BtcTransaction btcTran = new BtcTransaction();
        Object obj = btcTran.createTransaction(recipientAddress, sendValue);
        if (obj == null) {
            ToastUtils.show("invalid btc send");
            return;
        }
        String sendTx = btcTran.getSendTx();
        LogUtils.d(TAG, "clientSend=" + sendTx);

        SendDialog.showSend(getActivity(), btcTran);
    }

}
