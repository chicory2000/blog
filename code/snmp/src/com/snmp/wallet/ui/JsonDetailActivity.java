package com.snmp.wallet.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.snmp.crypto.R;
import com.snmp.network.JsonUtils;
import com.snmp.network.TimeoutChecker;
import com.snmp.utils.LogUtils;
import com.snmp.utils.PreferenceManager;
import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ThreadPoolUtil;
import com.snmp.utils.ToastUtils;

public class JsonDetailActivity extends Activity {
    private static final String TAG = JsonDetailActivity.class.getSimpleName();
    private static final String DEFAULT_URI = "https://mempool.space/api/v1/fees/recommended";
    private TextView mTextView;
    private String mContent;
    private Handler mHandler = new Handler();
    private String mUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.crypto_json_detail);
        initView();
    }

    private void initView() {
        mUri = getIntent().getStringExtra("uri");
        if (TextUtils.isEmpty(mUri)) {
            mUri = PreferenceManager.getString("json_input2", DEFAULT_URI);
        }

        mTextView = (TextView) findViewById(R.id.json_detail_txt);
        TextView titleView = (TextView) findViewById(R.id.json_detail_title).findViewById(R.id.json_detail_title_txt);
        titleView.setText("json");
        findViewById(R.id.json_detail_title_back).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.json_detail_title_txt).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.btn_json_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });

        findViewById(R.id.btn_json_input).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonInputDialog.showInput(JsonDetailActivity.this);
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                postApi(mUri);
            }
        }, 150);
    }

    private void postApi(String uri) {
        ThreadPoolUtil.post(new Runnable() {
            @Override
            public void run() {
                TimeoutChecker checker = new TimeoutChecker() {

                    @Override
                    public String getData() {
                        LogUtils.d(TAG, "postData " + mUri);
                        return JsonUtils.postData(mUri);
                    }

                    @Override
                    public void onSuccess(final String data) {
                        parseData(data);
                    }

                    @Override
                    public void onTimeout() {
                        ToastUtils.showLimited("network timeout");
                    }

                };
                checker.start();
            }
        });
    }

    private void parseData(final String jsondata) {
        SnmpApplication.post(new Runnable() {
            @Override
            public void run() {
                String jsonPrettyPrint = jsondata;
                try {
                    JSONObject jsonObject = new JSONObject(jsondata);
                    LogUtils.d(TAG, "mPrice " + jsondata);
                    jsonPrettyPrint = jsonObject.toString(4);

                } catch (JSONException e) {
                    LogUtils.e(TAG, "parseData " + e);
                    e.printStackTrace();
                }
                mTextView.setText(jsonPrettyPrint);
            }
        });
    }

    private void refresh() {
        mTextView.setText("postApi");
        postApi(mUri);
    }

}
