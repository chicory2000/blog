package com.snmp.book;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.TextView;

import com.snmp.crypto.R;

public class AboutDialog {
    private static final String TAG = AboutDialog.class.getSimpleName();

    public static void showAboutDialog(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("about");
        final TextView aboutText = new TextView(activity);
        aboutText.setText((activity.getString(R.string.app_book_dev)));
        aboutText.setPadding(20, 20, 20, 20);
        builder.setView(aboutText);

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
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
