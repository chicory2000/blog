package com.snmp.wallet.ui;

import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.EditText;

import com.snmp.utils.SnmpDialog;
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.WalletMain;

public class InputWordsDialog {
    private static final String TAG = "InputDialog";
    private static String sInputString = "";

    public static void showInput(final WalletMain main) {
        SnmpDialog dialog = new SnmpDialog(main.getActivity());
        dialog.setTitle("please input valid bip39 word");
        final EditText editText = new EditText(main.getActivity());
        editText.setText(sInputString);
        editText.setTextColor(Color.BLACK);
        dialog.setContentView(editText);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onInput(main, editText);
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static void onInput(final WalletMain main, final EditText editText) {
        sInputString = editText.getText().toString();

        if (TextUtils.isEmpty(sInputString)) {
            // Utils.toast("empty input");
        } else {
            // Utils.toast("success input");
        }
        BtcAccount.getInstance().generateNewWallet(sInputString);
    }

}
