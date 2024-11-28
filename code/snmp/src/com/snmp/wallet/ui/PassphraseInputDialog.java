package com.snmp.wallet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;

import com.snmp.utils.PreferenceManager;
import com.snmp.utils.Utils;

public class PassphraseInputDialog {
    private static final String TAG = "PassInputDialog";

    public static void showInput(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("please input pass word");
        final EditText editText = new EditText(activity);
        builder.setView(editText);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onInput(editText);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final Dialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
            }
        });

        dialog.show();
    }

    private static void onInput(final EditText editText) {
        String input = editText.getText().toString();

        if (TextUtils.isEmpty(input)) {
            PreferenceManager.putString("wallet_passphrase_pre", input);
        } else {
            PreferenceManager.putString("wallet_passphrase_pre", input);
            Utils.toast("success input");
        }
    }

}
