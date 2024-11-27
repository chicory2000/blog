package com.snmp.wallet.pin;

import android.content.Context;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.snmp.crypto.R;

public class NewPinDialog extends PinDialog {
    private final CheckBox cbResettablePin;

    public NewPinDialog(final Context context, boolean hidden) {
        super(context, hidden, true);
        this.setTitle(R.string.pin_enter_new_pin);

        cbResettablePin = (CheckBox) findViewById(R.id.cb_resettable_pin);

        cbResettablePin.setChecked(PinMgr.getInstance().getPin().isSet());

        cbResettablePin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                updateResetInfo(context);
            }
        });

        updateResetInfo(context);
    }

    private void updateResetInfo(Context context) {
        TextView txtInfo = (TextView) findViewById(R.id.tv_resettable_pin_info);
        if (cbResettablePin.isChecked()) {
            txtInfo.setText(context.getString(R.string.pin_resettable_pin_info, Constants.MIN_PIN_AGE_RESET_PIN));
        } else {
            txtInfo.setText(context.getString(R.string.pin_unresettable_pin_info));
        }
    }

    @Override
    protected void loadLayout() {
        setContentView(R.layout.enter_new_pin_dialog);
    }

    @Override
    protected Pin getPin() {
        return new Pin(enteredPin, isResettable());
    }

    public boolean isResettable() {
        return cbResettablePin.isChecked();
    }
}
