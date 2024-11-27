package com.snmp.cold;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.snmp.utils.QRCodeEncoder;
import com.snmp.utils.QRContents;
import com.snmp.utils.SnmpDialog;
import com.snmp.utils.ToastUtils;

public class ColdQrDialog {
    private static final String TAG = ColdQrDialog.class.getSimpleName();

    public static void showQr(final Activity activity, final String title, final String qrString) {
        SnmpDialog dialog = new SnmpDialog(activity);
        dialog.setTitle(title);

        final LinearLayout linearLayout = new LinearLayout(activity);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final TextView textView = new TextView(activity);
        int len = qrString.length() < 200 ? qrString.length() : 200;
        textView.setText(qrString.substring(0, len));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextColor(Color.BLACK);

        final ImageView imgView = new ImageView(activity);
        imgView.setImageBitmap(generateQRCode(activity, qrString));

        linearLayout.addView(textView);
        linearLayout.addView(imgView);
        dialog.setContentView(linearLayout);

        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                copyAddress(activity, qrString);
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private static Bitmap generateQRCode(final Activity activity, String uri) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Bitmap bitmap = null;

        int imgWidth = (int) (display.getWidth()) - 2;

        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(uri, null, QRContents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(), imgWidth);

        try {
            bitmap = qrCodeEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static void copyAddress(final Activity activity, final String recipientAddress) {
        CharSequence buildText = recipientAddress;
        if (!TextUtils.isEmpty(buildText)) {
            ClipboardManager service = activity.getSystemService(ClipboardManager.class);
            String label = "aaa";
            service.setPrimaryClip(ClipData.newPlainText(label, buildText));
            ToastUtils.show("suscess copy");
        }
    }
}
