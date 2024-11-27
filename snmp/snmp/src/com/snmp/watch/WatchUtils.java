package com.snmp.watch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.snmp.api.query.BaseQueryApi;
import com.snmp.network.JsonUtils;
import com.snmp.network.TimeoutChecker;
import com.snmp.utils.LogUtils;
import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ThreadPoolUtil;
import com.snmp.utils.ToastUtils;

public class WatchUtils {
	private static final String TAG = "watch";

	public static void postAddress(final onParseCallback watch,
			final BaseQueryApi queryApi, final WatchItemData watchItem) {
		ThreadPoolUtil.post(new Runnable() {
			@Override
			public void run() {
				TimeoutChecker checker = new TimeoutChecker() {

					@Override
					public String getData() {
						LogUtils.d(TAG, "postData " + queryApi.getUrl()
								+ queryApi.getAddress());
						return JsonUtils.postData(queryApi.getUrl()
								+ queryApi.getAddress());
					}

					@Override
					public void onSuccess(final String data) {
						if (data != null && data.contains(JsonUtils.DATA_FAIL)) {
							watchItem.mQueryResult = data.replaceAll(JsonUtils.DATA_FAIL, "");
							watch.updateWatchList();
							return;
						}

						SnmpApplication.post(new Runnable() {
							@Override
							public void run() {
								watch.onParse(queryApi, watchItem, data);
							}
						});
					}

					@Override
					public void onTimeout() {
						ToastUtils.showLimited("network timeout");
						watchItem.mQueryResult = "network timeout";
						watch.updateWatchList();
					}

				};
				checker.start();
			}
		});
	}

    public interface onParseCallback {
        void onParse(BaseQueryApi queryApi, WatchItemData watchItem, String data);
        void updateWatchList();
    }

    public static void CopyFile(Context context, boolean assetzip) {
        String filename = context.getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/snmp222.zip";
        File file = new File(filename);

        FileOutputStream mOutput = null;
        InputStream in = null;
        FileInputStream inzip = null;
        try {
            mOutput = new FileOutputStream(file);
            if (assetzip) {
                AssetManager assetManager = context.getAssets();
                in = assetManager.open("snmp222.zip");
            }

            byte[] buf = new byte[1024];
            int n;

            while ((n = in.read(buf)) != -1) {
                mOutput.write(buf, 0, n);
            }

            mOutput.flush();
            LogUtils.i(TAG, "resetLast lastaaaaa0=" + filename);
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.i(TAG, "resetLast lastaaaaa1=" + e);
        } finally {
            try {
                if (mOutput != null) {
                    mOutput.close();
                }
                if (in != null) {
                    in.close();
                }
                if (inzip != null) {
                    inzip.close();
                }
            } catch (IOException e) {

            }
        }

    }

}
