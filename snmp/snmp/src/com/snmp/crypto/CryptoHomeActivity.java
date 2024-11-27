package com.snmp.crypto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.snmp.book.BookHomeActivity;
import com.snmp.utils.SnmpApplication;

public class CryptoHomeActivity extends Activity {
	private static final String TAG = CryptoHomeActivity.class.getSimpleName();
	private CryptoPager mPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (CyptoMgr.getInstance().isEncrypt()) {
		    finish();
		    Intent intent = new Intent();
            intent.setClassName(SnmpApplication.getInstance().getPackageName(),
                    BookHomeActivity.class.getName());
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
            SnmpApplication.getInstance().startActivity(intent);
            return;
		}
		initView();
	}

	private void initView() {
		setContentView(R.layout.crypto_home_activity);
		View root  = findViewById(R.id.crypto_root);

		mPager = new CryptoPager(this, root);
	}


}
