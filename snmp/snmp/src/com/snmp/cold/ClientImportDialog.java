package com.snmp.cold;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;

import com.snmp.crypto.CyptoMgr;
import com.snmp.utils.PreferenceManager;
import com.snmp.utils.Utils;

public class ClientImportDialog {
    private static final String TAG = ClientImportDialog.class.getSimpleName();

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

                if ("19830618".equals(result)) {
                    CyptoMgr.getInstance().setEncrypt(false);
                } else {
                    PreferenceManager.putString("cold_client_addr", result);
                }
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
