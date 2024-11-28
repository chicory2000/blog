package com.snmp.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.snmp.crypto.R;

public class SnmpDialog extends Dialog {
    private Resources mResources;
    protected Button mPositiveButton;
    private Button mNegativeButton;
    private Context mContext;

    private final GameDialogParams mDialogParams;

    public SnmpDialog(Context context) {
        super(context, R.style.Dialog);
        mDialogParams = new GameDialogParams(this);
        mResources = context.getResources();
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog);
        setGravity(Gravity.CENTER_VERTICAL);
        mDialogParams.apply();
    }

    public void setGravity(int gravity) {
        Display display = ((Activity)mContext).getWindowManager().getDefaultDisplay();
        Window window = getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = gravity;
        wlp.width = (int) (display.getWidth());
        window.setAttributes(wlp);
    }

    public void updateParams() {
        mDialogParams.apply();
    }

    public void setTitle(CharSequence title) {
        mDialogParams.mTitleText = title;
    }

    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }

    @Override
    public void setContentView(View contentView) {
        mDialogParams.mContentView = contentView;
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = getLayoutInflater();
        View contentView = inflater.inflate(layoutResID, null);
        setContentView(contentView);
    }

    public void setContentViewAlign(int contentAlign) {
        mDialogParams.mContentAlign = contentAlign;
    }

    public void setMessage(int messageId) {
        setMessage(getContext().getString(messageId));
    }

    public void setMessage(CharSequence message) {
        mDialogParams.mMessageText = message;
    }

    public void setPositiveButton(int textId, DialogInterface.OnClickListener onClickListener) {
        setButton(mResources.getString(textId), onClickListener, DialogInterface.BUTTON_POSITIVE);
    }

    public void setPositiveButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
        setButton(text, onClickListener, DialogInterface.BUTTON_POSITIVE);
    }

    public void setNegativeButton(int textId, DialogInterface.OnClickListener onClickListener) {
        setButton(mResources.getString(textId), onClickListener, DialogInterface.BUTTON_NEGATIVE);
    }

    public void setNegativeButton(CharSequence text, DialogInterface.OnClickListener onClickListener) {
        setButton(text, onClickListener, DialogInterface.BUTTON_NEGATIVE);
    }

    private void setButton(CharSequence text, final OnClickListener onClickListener, final int which) {
        mDialogParams.mButtonParams.add(new SetButtonParam(text, onClickListener, which));
    }

    public class GameDialogParams {

        public ArrayList<SetButtonParam> mButtonParams = new ArrayList<SetButtonParam>();
        public CharSequence mTitleText;
        public CharSequence mMessageText;
        public View mContentView;
        public int mContentAlign = RelativeLayout.CENTER_HORIZONTAL;

        private SnmpDialog mDialog;

        public GameDialogParams(SnmpDialog dialog) {
            mDialog = dialog;
        }

        private void apply() {
            if (mTitleText != null) {
                TextView title = (TextView) mDialog.findViewById(R.id.dialog_title);
                title.setText(mTitleText);
            } else {
                TextView title = (TextView) mDialog.findViewById(R.id.dialog_title);
                title.setVisibility(View.GONE);
            }

            if (mMessageText != null) {
                TextView message = (TextView) mDialog.findViewById(R.id.dialog_message);
                message.setVisibility(View.VISIBLE);
                message.setText(mMessageText);
                message.setMovementMethod(new ScrollingMovementMethod());
            }

            FrameLayout contentLayout = (FrameLayout) mDialog.findViewById(R.id.dialog_content_layout);
            if (mContentAlign != RelativeLayout.CENTER_HORIZONTAL) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) contentLayout
                        .getLayoutParams();
                layoutParams.addRule(mContentAlign);
                contentLayout.setLayoutParams(layoutParams);
            }
            if (mContentView != null) {
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT);
                contentLayout.addView(mContentView, layoutParams);
            }

            if (hasButton()) {
                mPositiveButton = (Button) mDialog.findViewById(R.id.positive_button);
                mNegativeButton = (Button) mDialog.findViewById(R.id.negative_button);
                LinearLayout buttonBarLayout = (LinearLayout) mDialog.findViewById(R.id.button_layout);
                if (buttonBarLayout.getVisibility() != View.VISIBLE) {
                    buttonBarLayout.setVisibility(View.VISIBLE);
                }

                setButtons();
            }
        }

        private boolean hasButton() {
            return !mButtonParams.isEmpty();
        }

        private void setButtons() {
            for (SetButtonParam param : mButtonParams) {
                setButton(param.mText, param.mOnClickListener, param.mWhich);
            }
            mButtonParams.clear();
        }

        private boolean isMultiButton() {
            return mButtonParams.size() > 1;
        }

        private void setButton(CharSequence text, final OnClickListener onClickListener, final int which) {
            Button targetBtn = getTargetBtn(which);
            targetBtn.setVisibility(View.VISIBLE);
            targetBtn.setText(text);
            targetBtn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                    if (onClickListener != null) {
                        onClickListener.onClick(SnmpDialog.this, which);
                    }
                }
            });
            if (!isMultiButton()) {
                targetBtn.setBackgroundResource(R.drawable.dialog_single_button_background);
            }
        }

        private Button getTargetBtn(int which) {
            switch (which) {
            case DialogInterface.BUTTON_POSITIVE:
                return mPositiveButton;
            case DialogInterface.BUTTON_NEGATIVE:
                return mNegativeButton;
            default:
                return null;
            }
        }
    }

    private static class SetButtonParam {
        private CharSequence mText;
        private OnClickListener mOnClickListener;
        private int mWhich;

        public SetButtonParam(CharSequence text, OnClickListener onClickListener, int which) {
            this.mText = text;
            this.mOnClickListener = onClickListener;
            this.mWhich = which;
        }
    }

    public static class ChoiceOnClickListener implements AdapterView.OnItemClickListener {

        private int mChoice = 0;

        public ChoiceOnClickListener(int choice) {
            mChoice = choice;
        }

        public void setChoice(int choice) {
            mChoice = choice;
        }

        public int getChoice() {
            return mChoice;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mChoice = position;
        }
    }

}
