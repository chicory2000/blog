package com.snmp.api.fee;

import org.json.JSONObject;

import com.snmp.utils.LogUtils;
import com.snmp.wallet.api.BaseQueryApi;

public class SamouraiFeeApi extends BaseQueryApi {
    private static final String TAG = SamouraiFeeApi.class.getSimpleName();
    private static final String GET_FEE_URL = "https://api.samouraiwallet.com/v2/fees";
    private onApiCallback mCallback;

    public interface onApiCallback {
        void onFeeResponce(int fee);
    }

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public String getUrl() {
        return GET_FEE_URL;
    }

    @Override
    public void parseData(String jsondata) throws Exception {
        LogUtils.d(TAG, "barcodeResult aaaaaaaaaaaaaaaa=" + jsondata);
        JSONObject jsonObject = new JSONObject(jsondata);
        int fee = jsonObject.getInt("4");;
        mCallback.onFeeResponce(fee);
    }
    
    public void postApi(final onApiCallback callback) {
        mCallback = callback;
        super.postApi();
    }
}
