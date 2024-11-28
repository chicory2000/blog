package com.snmp.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.snmp.crypto.R;
import com.snmp.utils.Utils;
import com.snmp.wallet.pin.PinDialog;
import com.snmp.wallet.pin.PinMgr;

public class BookDetailActivity extends Activity {
    private static final String TAG = "BookDetailActivity";
    private TextView mTextView;
    private String mContent;
    private String name2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.book_detail);
        if (PinMgr.getInstance().isUnlockPinRequired()) {
            Runnable start = new Runnable() {
                @Override
                public void run() {
                    PinMgr.getInstance().setStartUpPinUnlocked(true);
                }
            };

            PinDialog _pinDialog = PinMgr.getInstance().runPinProtectedFunction(this, start, false);
            return;
        }
        initView();
    }

    private void initView() {
        name2 = "snmp";
        String type = getIntent().getStringExtra("type");
        String name = getIntent().getStringExtra("name");

        mTextView = (TextView) findViewById(R.id.detail_txt);
        TextView titleView = (TextView) findViewById(R.id.detail_title).findViewById(R.id.detail_title_txt);
        titleView.setText(name.replace(".txt", ""));
        findViewById(R.id.detail_title_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.detail_title_txt).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.detail_title_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });

        File file = new File(BookConstant.BOOK_PATH + "/" + type + "/" + name);
        if (!file.exists()) {
            Utils.toast("file error");
            return;
        }

        if (BookConstant.SNMP_PROGUARD && name != null && name.contains(getString(R.string.app_book_self))) {
            Utils.toast("file error");
            return;
        }
        //if (BookConstant.SNMP_PROGUARD) {
        //    mTextView.setTextIsSelectable(false);
        //}

        FileInputStream fin = null;
        try {
            int length = (int) file.length();
            byte[] buff = new byte[length];
            fin = new FileInputStream(file);
            fin.read(buff);
            fin.close();
            mContent = new String(buff, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
            }
        }

        if (BookConstant.SNMP_AES) {
            String password = BookAESCipher.getPassword() + name2;

            try {
                mContent = BookAESCipher.decrypt(password, mContent);
                // LogUtils.i(TAG, "snmp file aaa111 " + mContent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mTextView.setText(mContent);
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.removeExtra(android.content.Intent.EXTRA_TEXT);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mContent);
        startActivity(Intent.createChooser(sharingIntent, null));
    }

}
