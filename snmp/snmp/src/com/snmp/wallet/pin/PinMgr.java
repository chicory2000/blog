package com.snmp.wallet.pin;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import static java.util.concurrent.TimeUnit.SECONDS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Vibrator;

import com.snmp.utils.PreferenceManager;
import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ToastUtils;
import com.snmp.book.BookConstant;
import com.snmp.crypto.R;

public class PinMgr {
    private static PinMgr sPinMgr;
    private Pin mPin = new Pin("");
    private int mRemainingCount = 7;
    private AtomicBoolean lastPinAgeOkay = new AtomicBoolean(false);
    private int failedPinCount = 0;
    private ScheduledFuture<?> pinOkTimeoutHandle;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private boolean randomizePinPad;
    private boolean startUpPinUnlocked = false;
    private boolean _pinRequiredOnStartup;

    public static PinMgr getInstance() {
        if (sPinMgr == null) {
            synchronized (PinMgr.class) {
                if (sPinMgr == null) {
                    sPinMgr = new PinMgr();
                }
            }
        }
        return sPinMgr;
    }

    private PinMgr() {
        String password = "";
        boolean startupLock = false;
//        if (BookConstant.SNMP_PROGUARD) {
//            password = "123789";
//            startupLock = true;
//        }
        String pinSave = PreferenceManager.getString("pin_save", password);
        mPin = new Pin(pinSave);
        randomizePinPad = getPreferences().getBoolean(Constants.RANDOMIZE_PIN, false);
        _pinRequiredOnStartup = getPreferences().getBoolean(Constants.PIN_SETTING_REQUIRED_ON_STARTUP, startupLock);
    }

    public Pin getPin() {
        return mPin;
    }

    public void savePin(Pin pin) {
        mPin = pin;
        PreferenceManager.putString("pin_save", pin.getPin());
    }

    public int getResetPinRemainingBlocksCount() {
        return mRemainingCount;
    }

    public boolean isPinPadRandomized() {
        return randomizePinPad;
    }

    public void setPinPadRandomized(boolean randomizePinPad) {
        getEditor().putBoolean(Constants.RANDOMIZE_PIN, randomizePinPad).apply();
        this.randomizePinPad = randomizePinPad;
    }

    public void vibrate() {
        Vibrator v = (Vibrator) SnmpApplication.getInstance().getSystemService(Context.VIBRATOR_SERVICE);

        if (v != null) {
            v.vibrate(500);
        }
    }

    private SharedPreferences getPreferences() {
        return PreferenceManager.get();
    }

    private SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    public boolean getPinRequiredOnStartup() {
        return this._pinRequiredOnStartup;
    }

    public boolean isUnlockPinRequired() {
        return getPinRequiredOnStartup() && !startUpPinUnlocked;
    }

    public void setStartUpPinUnlocked(boolean unlocked) {
        this.startUpPinUnlocked = unlocked;
    }

    public void setPinRequiredOnStartup(boolean pinRequiredOnStartup) {
        getEditor().putBoolean(Constants.PIN_SETTING_REQUIRED_ON_STARTUP, pinRequiredOnStartup).apply();

        this._pinRequiredOnStartup = pinRequiredOnStartup;
    }

    public boolean isPinProtected() {
        return getPin().isSet();
    }

    private void pinOkForOneS() {
        if (pinOkTimeoutHandle != null) {
            pinOkTimeoutHandle.cancel(true);
        }
        lastPinAgeOkay.set(true);
        pinOkTimeoutHandle = scheduler.schedule(new Runnable() {
            public void run() {
                lastPinAgeOkay.set(false);
            }
        }, 1, SECONDS);
    }

    // returns the PinDialog or null, if no pin was needed
    public PinDialog runPinProtectedFunction(final Activity activity, final Runnable fun, boolean cancelable) {
        return runPinProtectedFunctionInternal(activity, fun, cancelable);
    }

    // returns the PinDialog or null, if no pin was needed
    public PinDialog runPinProtectedFunction(final Activity activity, final Runnable fun) {
        return runPinProtectedFunctionInternal(activity, fun, true);
    }

    // returns the PinDialog or null, if no pin was needed
    private PinDialog runPinProtectedFunctionInternal(Activity activity, Runnable fun, boolean cancelable) {
        if (isPinProtected() && !lastPinAgeOkay.get()) {
            PinDialog d = new PinDialog(activity, true, cancelable);
            runPinProtectedFunction(activity, d, fun);
            return d;
        } else {
            fun.run();
            return null;
        }
    }

    protected void runPinProtectedFunction(final Activity activity, PinDialog pinDialog, final Runnable fun) {
        if (isPinProtected()) {
            failedPinCount = getPreferences().getInt(Constants.FAILED_PIN_COUNT, 0);
            pinDialog.setOnPinValid(new PinDialog.OnPinEntered() {
                @Override
                public void pinEntered(final PinDialog pinDialog, Pin pin) {
                    if (failedPinCount > 0) {
                        long millis = (long) (Math.pow(1.2, failedPinCount) * 10);
                        try {
                            Thread.sleep(millis);
                        } catch (InterruptedException ignored) {
                            ToastUtils.show(R.string.avoid_get_to_pin_check);
                            vibrate();
                            pinDialog.dismiss();
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    if (pin.equals(getPin())) {
                        failedPinCount = 0;
                        getEditor().putInt(Constants.FAILED_PIN_COUNT, failedPinCount).apply();
                        pinDialog.dismiss();

                        // as soon as you enter the correct pin once, abort the
                        // reset-pin-procedure
                        // //todo
                        // MbwManager.this.getMetadataStorage().clearResetPinStartBlockheight();
                        // if last Pin entry was 1sec ago, don't ask for it
                        // again.
                        // to prevent if there are two pin protected functions
                        // cascaded
                        // like startup-pin request and account-choose-pin
                        // request if opened by a bitcoin url
                        pinOkForOneS();

                        fun.run();
                    } else {
                        getEditor().putInt(Constants.FAILED_PIN_COUNT, ++failedPinCount).apply();
                        if (mPin.isResettable()) {
                            // Show hint, that this pin is resettable
                            new AlertDialog.Builder(activity)
                                    .setTitle(R.string.pin_invalid_pin)
                                    .setPositiveButton(activity.getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    pinDialog.dismiss();
                                                }
                                            })
                                    .setNeutralButton(activity.getString(R.string.reset_pin_button),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    pinDialog.dismiss();
                                                    showClearPinDialog(activity, null);
                                                }
                                            })

                                    .setMessage(activity.getString(R.string.wrong_pin_message)).show();
                        } else {
                            // This pin is not resettable, you are out of luck
                            ToastUtils.show(R.string.pin_invalid_pin);
                            vibrate();
                            pinDialog.dismiss();
                        }
                    }
                }
            });
            if (!activity.isFinishing()) {
                pinDialog.show();
            }
        } else {
            fun.run();
        }
    }

    public void showClearPinDialog(final Activity activity, final Runnable afterDialogClosed) {
        this.runPinProtectedFunction(activity, new ClearPinDialog(activity, true), new Runnable() {
            @Override
            public void run() {
                savePin(Pin.CLEAR_PIN);
                ToastUtils.show(R.string.pin_cleared);
                if (afterDialogClosed != null) {
                    afterDialogClosed.run();
                }
            }
        });
    }

    public void showSetPinDialog(final Activity activity, final Runnable afterDialogClosed) {
        // Must make a backup before setting PIN
        // if (this.getMetadataStorage().getMasterSeedBackupState() !=
        // MetadataStorage.BackupState.VERIFIED) {
        // Utils.showSimpleMessageDialog(activity, R.string.pin_backup_first,
        // afterDialogClosed);
        // ToastUtils.show(R.string.pin_backup_first);
        // return;
        // }

        final NewPinDialog pinDialog = new NewPinDialog(activity, false);
        pinDialog.setOnPinValid(new PinDialog.OnPinEntered() {
            private String newPin = null;

            @Override
            public void pinEntered(PinDialog dialog, Pin pin) {
                if (newPin == null) {
                    newPin = pin.getPin();
                    dialog.setTitle(R.string.pin_confirm_pin);
                } else if (newPin.equals(pin.getPin())) {
                    savePin(pin);
                    ToastUtils.show(R.string.pin_set);
                    dialog.dismiss();
                    if (afterDialogClosed != null) {
                        afterDialogClosed.run();
                    }
                } else {
                    ToastUtils.show(R.string.pin_codes_dont_match);
                    vibrate();
                    dialog.dismiss();
                    if (afterDialogClosed != null) {
                        afterDialogClosed.run();
                    }
                }
            }
        });

        runPinProtectedFunction(activity, new Runnable() {
            @Override
            public void run() {
                pinDialog.show();
            }
        });
    }

}
