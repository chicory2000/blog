package com.snmp.cold;

import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;
import com.snmp.crypto.R;
import com.snmp.utils.EventListenerMgr;
import com.snmp.utils.LogUtils;
import com.snmp.utils.PreferenceManager;

public class ScanActivity extends Activity implements DecoratedBarcodeView.TorchListener {
    protected static final String TAG = ScanActivity.class.getSimpleName();
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton;
    private ViewfinderView viewfinderView;
    private Handler mHandler = new Handler();
    private String mScanResult = "";

    private BarcodeCallback mCallback = new BarcodeCallback() {

        @Override
        public void barcodeResult(final BarcodeResult result) {
            barcodeScannerView.pause();
            capture.playBeepSoundAndVibrate();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    capture.closeAndFinish();
                    mScanResult = result.getText();
                    PreferenceManager.putString("cold_scan_result", mScanResult);
                    EventListenerMgr.onEvent(EventListenerMgr.EVENT_COLD_UI_UPDATE, "scan");

                    LogUtils.d(TAG, "barcodeResult barcodeValue=" + mScanResult);
                    copyScan();
                }
            });
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
            LogUtils.d(TAG, "possibleResultPoints " + resultPoints);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zxing_activity_custom_scanner);

        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = (Button) findViewById(R.id.switch_flashlight);

        viewfinderView = (ViewfinderView) findViewById(R.id.zxing_viewfinder_view);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setShowMissingCameraPermissionDialog(false);
        capture.decode();
        //override capture.decode() set callback
        barcodeScannerView.decodeSingle(mCallback);

        changeMaskColor(null);
        changeLaserVisibility(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     * 
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        viewfinderView.setMaskColor(color);
    }

    public void changeLaserVisibility(boolean visible) {
        viewfinderView.setLaserVisibility(visible);
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setText(R.string.turn_on_flashlight);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void copyScan() {
        CharSequence buildText = mScanResult;
        if (!TextUtils.isEmpty(buildText)) {
            ClipboardManager service = getSystemService(ClipboardManager.class);
            String label = "scan";
            service.setPrimaryClip(ClipData.newPlainText(label, buildText));
            Toast.makeText(this, "suscess scan copy to clipbord", Toast.LENGTH_SHORT).show();
        }
    }
}
