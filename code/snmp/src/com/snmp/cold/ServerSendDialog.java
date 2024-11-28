package com.snmp.cold;

import android.app.Activity;
import android.content.DialogInterface;

import com.snmp.utils.LogUtils;
import com.snmp.utils.SnmpDialog;
import com.snmp.utils.Utils;
import com.snmp.wallet.BtcAccount;

public class ServerSendDialog {
    private static final String TAG = ServerSendDialog.class.getSimpleName();

    public static void showSend(final Activity activity, final TransactionData btcTran) {
        SnmpDialog dialog = new SnmpDialog(activity);
        dialog.setTitle("please check the send info");
        String show = getShowTxt(btcTran.mRecipientAddr, btcTran.mRecipientValue, btcTran);
        dialog.setMessage(show);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static String getShowTxt(final String recipientAddress, final long sendValue, final TransactionData btcTran) {
        String show = "sendTo: " + recipientAddress + "\n\n";

        show += "sendValue: " + Utils.getBalance(sendValue) + "\n";

        show += "sendCost: " + btcTran.mSendCost + "\n";
        show += "sendFee: " + btcTran.mSendFee + "\n";

        show += btcTran.mSendDetail + "\n";
        return show;
    }

}
