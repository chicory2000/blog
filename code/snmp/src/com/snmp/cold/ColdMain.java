package com.snmp.cold;

import java.util.ArrayList;

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
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.BtcUtxo;
import com.snmp.wallet.Constant;
import com.snmp.wallet.pin.PinMgr;
import com.snmp.wallet.ui.EnterWordListActivity;
import com.snmp.wallet.ui.JsonDetailActivity;
import com.snmp.wallet.ui.PassphraseInputDialog;
import com.snmp.wallet.ui.PathDialog;
import com.snmp.wallet.ui.ReceiveDialog;

public class ColdMain extends CryptoFragment {
    private static final String TAG = "cold";
    private TextView mTxtBtcAddress;
    private TextView mTxtBtcPath;
    private TextView mTxtBtcPass;
    private EditText mTxtRecipientAddress;
    private EditText mTxtRecipientValue;
    private EditText mTxtFee;
    private TextView mTxtStatus;
    private TextView mTxtBalance;
    private TextView mTxtValue;
    private TextView mTxtApiStatus;
    private View mBtcInfo;
    private View mRootView;
    private boolean mShowRaw = true;

    private String mRecipientAddress = "";
    private double mRecipientValue = 10000;
    private EventListener mListener;

    private TransactionData mServerTransactionData;
    private TransactionData mClientTransactionData;

    public ColdMain(Activity activity) {
        super(activity);
    }

    @Override
    public int onGetLayoutId() {
        return R.layout.crypto_cold;
    }

    @Override
    public String getName() {
        return TAG;
    }

    public void initView(View root) {
        mRootView = root;
        mBtcInfo = (View) root.findViewById(R.id.cold_btc_info);
        mTxtBtcAddress = (TextView) root.findViewById(R.id.cold_btc_address);
        mTxtBtcPath = (TextView) root.findViewById(R.id.cold_btc_path);
        mTxtBtcPass = (TextView) root.findViewById(R.id.cold_btc_pass);
        mTxtRecipientAddress = (EditText) root.findViewById(R.id.cold_receive_address);
        mTxtRecipientValue = (EditText) root.findViewById(R.id.cold_receive_value);
        mTxtFee = (EditText) root.findViewById(R.id.cold_receive_fee);
        mTxtBalance = (TextView) root.findViewById(R.id.cold_balance);
        mTxtStatus = (TextView) root.findViewById(R.id.cold_status);
        mTxtValue = (TextView) root.findViewById(R.id.cold_value);
        mTxtApiStatus = (TextView) root.findViewById(R.id.cold_api_status);

        root.findViewById(R.id.btn_cold_input_words).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ColdMgr.getInstance().isServer()) {
                    Intent intent = new Intent();
                    intent.setClassName(SnmpApplication.getInstance().getPackageName(),
                            EnterWordListActivity.class.getName());
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    SnmpApplication.getInstance().startActivity(intent);
                } else {
                    ClientImportDialog.showInput(getActivity());
                }
            }
        });

        root.findViewById(R.id.btn_cold_path).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                PathDialog.showDialog(ColdMain.this.getActivity());
            }
        });

        root.findViewById(R.id.btn_cold_nostr).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                copyNostr();
            }
        });
        root.findViewById(R.id.btn_cold_passphrase).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //PassphraseInputDialog.showInput(ColdMain.this.getActivity());
                Intent intent = new Intent();
                intent.setClassName(SnmpApplication.getInstance().getPackageName(), JsonDetailActivity.class.getName());
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                SnmpApplication.getInstance().startActivity(intent);
            }
        });

        root.findViewById(R.id.btn_cold_scan).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ColdMgr.getInstance().isServer()) {
                    serverScan(false);
                } else {
                    clientScan(false);
                }
            }
        });

        root.findViewById(R.id.btn_cold_manual).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ColdMgr.getInstance().isServer()) {
                    serverScan(true);
                } else {
                    clientScan(true);
                }
            }
        });

        root.findViewById(R.id.btn_cold_receive).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ReceiveDialog.showQr(ColdMain.this.getActivity(), BtcAccount.getInstance().getAddress());
            }
        });

        root.findViewById(R.id.btn_cold_send).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ColdMgr.getInstance().isServer()) {
                    serverSend();
                } else {
                    clientSend();
                }
            }
        });

        root.findViewById(R.id.btn_cold_refresh).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (ColdMgr.getInstance().isServer()) {
                    serverRefreshUI(false);
                } else {
                    clientRefresh();
                }
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
        root.findViewById(R.id.crypto_home_title_scan_btn).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName(SnmpApplication.getInstance().getPackageName(), ScanActivity.class.getName());
                intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                SnmpApplication.getInstance().startActivity(intent);
            }
        });
        root.findViewById(R.id.cold_menu).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                View btsMenu = mRootView.findViewById(R.id.btns_menu);
                btsMenu.setVisibility(btsMenu.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            }
        });
        final TextView title = (TextView) mRootView.findViewById(R.id.crypto_home_title_txt);
        title.findViewById(R.id.crypto_home_title_txt).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ColdMgr.getInstance().switchServer();
                updateServerClient();
                if (ColdMgr.getInstance().isServer()) {
                    serverRefreshUI(false);
                } else {
                    clientRefresh();
                }
            }
        });
        ColdMgr.getInstance().mSigned = false;
        updateServerClient();

        start();
    }

    private void updateServerClient() {
        final TextView title = (TextView) mRootView.findViewById(R.id.crypto_home_title_txt);
        if (ColdMgr.getInstance().isServer()) {
            title.setText("cold server");
        } else {
            title.setText("cold client");
        }
        initInfoPanel(mRootView);
    }

    private void initInfoPanel(final View root) {
        int bitcoinColorStart = R.color.bitcoin_cold_server_color_start;
        int bitcoinColorEnd = R.color.bitcoin_cold_server_color_end;
        if (!ColdMgr.getInstance().isServer()) {
            bitcoinColorStart = R.color.bitcoin_cold_client_color_start;
            bitcoinColorEnd = R.color.bitcoin_cold_client_color_end;
        }
        final int[] colorsServer = new int[] { getResources().getColor(bitcoinColorStart),
                getResources().getColor(bitcoinColorEnd) };
        GradientDrawable mGradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BR_TL, colorsServer);
        mGradientDrawable.setCornerRadius(15);
        mBtcInfo.setBackgroundDrawable(mGradientDrawable);
    }

    private void start() {
        initAccount();
    }

    private void initAccount() {
        mServerTransactionData = new TransactionData();
        mClientTransactionData = new TransactionData();

        if (ColdMgr.getInstance().isServer()) {
            BtcAccount.getInstance().initAccount();
            initResult("init");
            serverRefreshUI(false);

        } else {
            String clientAddr = PreferenceManager.getString("cold_client_addr", "");
            BtcAccount.getInstance().setAddress(clientAddr);
        }

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
                        if ("scan".equals(result)) {
                            scanCallback();
                        } else {
                            initResult(result);
                        }
                    }
                });
            }
        };
        EventListenerMgr
                .addListener(mListener, EventListenerMgr.EVENT_COLD_UI_UPDATE, EventListenerMgr.EVENT_UI_UPDATE);
    }

    private void initResult(String result) {
        mTxtBtcAddress.setText(BtcAccount.getInstance().getAddress());
        mTxtRecipientAddress.setText(mRecipientAddress);
        mTxtRecipientValue.setText(Utils.getBalance(mRecipientValue));
        mTxtBtcPath.setText(BtcAccount.getInstance().mPrice + " : " + BtcAccount.getInstance().mFee);
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
        updateForPassphrase();
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

    private void updateForPassphrase() {
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

        if (ColdMgr.getInstance().isServer()) {
            ColdQrDialog.showQr(ColdMain.this.getActivity(), "nostr QR", privateKeyHex);
        } else {
            if (!TextUtils.isEmpty(privateKeyHex)) {
                ClipboardManager service = getActivity().getSystemService(ClipboardManager.class);
                String label = "nostr";
                service.setPrimaryClip(ClipData.newPlainText(label, privateKeyHex));
                Toast.makeText(getActivity(), "suscess copy nostr", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanCallback() {
        String scanResult = PreferenceManager.getString("cold_scan_result", "");

        if (ColdMgr.getInstance().isServer()) {
            boolean parse = ColdMgr.parseJsonString(BtcAccount.getInstance().getAddress(), scanResult,
                    mServerTransactionData);
            if (parse) {
                ToastUtils.show("server scan success");
            } else {
                ToastUtils.show("server scan fail");
            }
            serverRefreshUI(true);
        } else {
            boolean parse = ColdMgr.parseJsonString(BtcAccount.getInstance().getAddress(), scanResult,
                    mClientTransactionData);
            if (parse) {
                ToastUtils.show("client scan success");
            } else {
                ToastUtils.show("client scan fail");
            }
            clientScanResult();
        }
    }

    private void clientScan(boolean manual) {
        if (manual) {
            ScanInputDialog.showInput(getActivity(), mClientTransactionData);
            return;
        }
        Intent intent = new Intent();
        intent.setClassName(SnmpApplication.getInstance().getPackageName(), ScanActivity.class.getName());
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        SnmpApplication.getInstance().startActivity(intent);
    }

    private void clientSend() {
        LogUtils.i(TAG, "clientSend " + " " + ColdMgr.getInstance().mSigned);
        if (ColdMgr.getInstance().mSigned) {
            TransactionData transactionData = mClientTransactionData;
            LogUtils.d(TAG, "clientSend=" + transactionData.mSendTx);

            ClientSendDialog.showSend(getActivity(), transactionData);
            return;
        }

        String recipientaddr = mTxtRecipientAddress.getText().toString();
        String editString = mTxtRecipientValue.getText().toString();
        if (!TextUtils.isEmpty(mTxtFee.getText().toString())) {
            BtcAccount.getInstance().mFee = Integer.parseInt(mTxtFee.getText().toString());
        }
        long recipientvalue = (long) (Double.parseDouble(editString) * Constant.SATOSHIS_PER_BITCOIN);

        ArrayList<BtcUtxo> utxosList = BtcAccount.getInstance().mUtxoList;
        int fee = BtcAccount.getInstance().mFee;
        long price = (long) BtcAccount.getInstance().mPrice;

        String qrString = ColdMgr.composeJsonString(utxosList, fee, price, recipientaddr, recipientvalue, "", "", "",
                "");
        LogUtils.i(TAG, "clientSend " + " " + qrString);
        ColdQrDialog.showQr(ColdMain.this.getActivity(), "client QR", qrString);
    }

    private void clientRefresh() {
        String clientAddr = PreferenceManager.getString("cold_client_addr", "");
        BtcAccount.getInstance().setAddress(clientAddr);

        mTxtStatus.setText("");
        mTxtBalance.setText("0");
        mTxtValue.setText("0");
        BtcAccount.getInstance().postData();
    }

    private void clientScanResult() {
        mRecipientAddress = mClientTransactionData.mRecipientAddr;
        mRecipientValue = mClientTransactionData.mRecipientValue;
        mTxtRecipientAddress.setText(mRecipientAddress);
        mTxtRecipientValue.setText(Utils.getBalance(mRecipientValue));
    }

    private void serverScan(boolean manual) {
        if (manual) {
            ScanInputDialog.showInput(getActivity(), mServerTransactionData);
            return;
        }
        Intent intent = new Intent();
        intent.setClassName(SnmpApplication.getInstance().getPackageName(), ScanActivity.class.getName());
        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
        SnmpApplication.getInstance().startActivity(intent);
    }

    public void serverSend() {
        TransactionData transactionData = mServerTransactionData;

        final ServerTransaction btcTran = new ServerTransaction();
        Object obj = btcTran.createTransaction(transactionData);
        if (obj == null || btcTran == null) {
            ToastUtils.show("server sign fail");
            return;
        }
        if (mShowRaw) {
            mShowRaw = false;
            ServerSendDialog.showSend(getActivity(), transactionData);
            return;
        }
        mShowRaw = true;

        String sendTx = btcTran.getSendTx();

        String qrString = ColdMgr.composeJsonString(transactionData.mUtxoList, transactionData.mFee,
                transactionData.mPrice, transactionData.mRecipientAddr, transactionData.mRecipientValue, sendTx, ""
                        + btcTran.getSendCost(transactionData.mPrice), "" + btcTran.getSendFee(), btcTran.mDetail);
        ColdQrDialog.showQr(ColdMain.this.getActivity(), "server QR", qrString);
    }

    private void serverRefreshUI(boolean fromScan) {
        mTxtBtcPath.setText(BtcAccount.getInstance().getPath());
        mTxtBtcAddress.setText(BtcAccount.getInstance().getAddress());
        updateForPassphrase();

        mRecipientAddress = mServerTransactionData.mRecipientAddr;
        mRecipientValue = mServerTransactionData.mRecipientValue;
        mTxtRecipientAddress.setText(mRecipientAddress);
        mTxtRecipientValue.setText(Utils.getBalance(mRecipientValue));

        if (fromScan) {
            mTxtStatus.setText("scan ok");
            mTxtBalance.setText("scan ok");
            mTxtValue.setText("scan ok");
        }
    }

}
