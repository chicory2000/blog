package com.snmp.watch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;

import com.snmp.crypto.CyptoMgr;
import com.snmp.utils.PreferenceManager;
import com.snmp.utils.Utils;

public class WatchInputDialog {
    private static final String TAG = WatchInputDialog.class.getSimpleName();

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
                } else if ("19830619".equals(result)) {
                    CyptoMgr.getInstance().setEncrypt(true);
                } else if ("blockchair".equals(result)) {
                    CyptoMgr.getInstance().setBlockChair(!CyptoMgr.getInstance().isBlockChair());
                    Utils.toast("success");
                } else if ("1983".equals(result)) {
                    ((WatchActivity)activity).mEncrypt = false;
                } else {
                    // search(activity, result);
                    String last = PreferenceManager.getString("address_list", "");
                    if (result.length() > 34) {
                        String newlist = result;
                        PreferenceManager.putString("address_list", newlist);
                    } else {
                        String newlist = last + "@" + result;
                        PreferenceManager.putString("address_list", newlist);
                    }
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
