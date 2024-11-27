package com.snmp.wallet.ui;

import android.app.Activity;
import android.content.DialogInterface;

import com.snmp.utils.SnmpDialog;
import com.snmp.utils.Utils;
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.api.ApiMgr;
import com.snmp.wallet.security.BtcTransaction;

public class SendDialog {
    private static final String TAG = SendDialog.class.getSimpleName();

    public static void showSend(final Activity activity, final BtcTransaction btcTran) {
        SnmpDialog dialog = new SnmpDialog(activity);
        dialog.setTitle("please check the send info");
        String show = getShowTxt(btcTran.mRecipientAddress, btcTran.mRecipientValue, btcTran);
        dialog.setMessage(show);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendTx(btcTran);
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static String getShowTxt(final String recipientAddress, final long sendValue, final BtcTransaction btcTran) {
        String show = "sendTo: " + recipientAddress + "\n\n";

        show += "sendValue: " + Utils.getBalance(sendValue) + "\n";

        long btcPrice = (long) BtcAccount.getInstance().mPrice;
        show += "sendCost: " + btcTran.getFee(sendValue, btcPrice) + "\n";
        show += "sendFee: " + Utils.getBalance(btcTran.getRawFee(sendValue)) + "\n";

        show += btcTran.getBtcTransactionDetail() + "\n";
        return show;
    }

    private static void sendTx(BtcTransaction btcTran) {
        ApiMgr.getInstance().sendTx(btcTran.getSendTx());
    }

}
