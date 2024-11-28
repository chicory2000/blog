package com.snmp.wallet.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;

import com.snmp.utils.PreferenceManager;
import com.snmp.utils.Utils;

public class JsonInputDialog {
    private static final String TAG = JsonInputDialog.class.getSimpleName();

    public static void showInput(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("please input");
        final EditText editText = new EditText(activity);
        builder.setView(editText);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String result = editText.getText().toString();
                if (TextUtils.isEmpty(result)) {
                    Utils.toast("null input");
                    return;
                }

                PreferenceManager.putString("json_input2", result);
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

}
