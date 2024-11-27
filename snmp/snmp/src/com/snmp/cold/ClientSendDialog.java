package com.snmp.cold;

import android.app.Activity;
import android.content.DialogInterface;

import com.snmp.utils.SnmpDialog;
import com.snmp.utils.Utils;
import com.snmp.wallet.api.ApiMgr;

public class ClientSendDialog {
    private static final String TAG = ClientSendDialog.class.getSimpleName();

    public static void showSend(final Activity activity, final TransactionData transactionData) {
        SnmpDialog dialog = new SnmpDialog(activity);
        dialog.setTitle("please check the send info");
        String show = getShowTxt(transactionData.mRecipientAddr, transactionData.mRecipientValue, transactionData);
        dialog.setMessage(show);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sendTx(transactionData);
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static String getShowTxt(final String recipientAddress, final long sendValue, final TransactionData transactionData) {
        String show = "sendTo: " + recipientAddress + "\n\n";

        show += "sendValue: " + Utils.getBalance(sendValue) + "\n";

        show += "sendCost: " + transactionData.mSendCost + "\n";
        show += "sendFee: " + transactionData.mSendFee + "\n";

        show += transactionData.mSendDetail + "\n";
        return show;
    }

    private static void sendTx(TransactionData transactionData) {
        ApiMgr.getInstance().sendTx(transactionData.mSendTx);
    }

}
