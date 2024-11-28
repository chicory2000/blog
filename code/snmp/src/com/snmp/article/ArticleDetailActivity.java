package com.snmp.article;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.snmp.crypto.R;

public class ArticleDetailActivity extends Activity {
    private static final String TAG = ArticleDetailActivity.class.getSimpleName();
    private TextView mTextView;
    private String mContent;
    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.book_detail);
        initView();
    }

    private void initView() {
        final String name = getIntent().getStringExtra("name");

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

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(name);
            }
        }, 150);
    }

    private void initData(String name) {
        StringBuffer sb = new StringBuffer();
        BufferedReader bfr = null;
        AssetManager assets = getAssets();
        try {
            bfr = new BufferedReader(new InputStreamReader(assets.open(name)));
            String s;
            while ((s = bfr.readLine()) != null) {
                sb.append(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bfr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mTextView.setText(sb.toString());
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.removeExtra(android.content.Intent.EXTRA_TEXT);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mContent);
        startActivity(Intent.createChooser(sharingIntent, null));
    }

}
