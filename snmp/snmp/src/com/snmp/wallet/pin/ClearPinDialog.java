package com.snmp.wallet.pin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Button;
import com.google.common.base.Optional;
import com.snmp.crypto.R;

public class ClearPinDialog extends PinDialog {
   public ClearPinDialog(final Context context, boolean hidden) {
      super(context, hidden, true);
      final int BTC_BLOCK_TIME_IN_SECONDS = 600;
      final PinMgr mPinMgr = PinMgr.getInstance();

      Button btnForgotPin = (Button) findViewById(R.id.btn_forgot_pin);

      if (mPinMgr.getPin().isResettable()){
         int resetPinRemainingBlocksCount = mPinMgr.getResetPinRemainingBlocksCount();
         if (resetPinRemainingBlocksCount == 0){
            // reset procedure was started and is already old enough -> provide option to reset PIN
            btnForgotPin.setText("Reset PIN now");
            btnForgotPin.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                  mPinMgr.savePin(Pin.CLEAR_PIN);
                  if (onPinValid != null) onPinValid.pinEntered(ClearPinDialog.this, Pin.CLEAR_PIN );
               }
            });
         }/*else if (resetPinRemainingBlocksCount.isPresent()){
            // reset procedure was started, but the target blockheight isn't reached
            btnForgotPin.setText(String.format(
                  context.getString(R.string.pin_forgotten_reset_wait_button_text),
                  Utils.formatBlockcountAsApproxDuration(mPinMgr, resetPinRemainingBlocksCount.get(), BTC_BLOCK_TIME_IN_SECONDS))
            );

            btnForgotPin.setEnabled(false);
         }*/else{
            // no reset procedure was started
            btnForgotPin.setOnClickListener(startResetListener(context, mPinMgr));
         }

      }else{
         // The current PIN is not marked as resettable - sorry, you are on your own
         btnForgotPin.setVisibility(View.GONE);
      }
   }

   private View.OnClickListener startResetListener(final Context context, final PinMgr mPinMgr) {
      return new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            new AlertDialog.Builder(ClearPinDialog.this.getContext())
                  .setPositiveButton(context.getString(R.string.yes), new OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        // mPinMgr.startResetPinProcedure();
                        ClearPinDialog.this.dismiss();
                     }
                  })
                  .setNegativeButton(context.getString(R.string.no), new OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                        ClearPinDialog.this.dismiss();
                     }
                  })
                  .setMessage(context.getString(
                              R.string.pin_forgotten_reset_pin_dialog_content,
                              Constants.MIN_PIN_AGE_RESET_PIN
                        )
                  )
                  .setTitle(context.getString(R.string.pin_forgotten_reset_pin_dialog_title))
                  .show();
         }
      };
   }

   @Override
   protected void loadLayout() {
      setContentView(R.layout.enter_clear_pin_dialog);
   }
}
