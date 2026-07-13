package com.limelight.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.limelight.R;

/**
 * 配对pc框
 */
public class PairPcDialog extends BaseDialog {
    public interface OnDialogClickListener {
        void onDialogClick(PairPcDialog dialog);
    }

    public static PairPcDialog showDialog(Context context, String pinStr, OnDialogClickListener confirmListener) {
        PairPcDialog dialog = new PairPcDialog(context);
        dialog.setPinStr(pinStr);
        dialog.setPositiveButton(confirmListener);
        dialog.show();
        return dialog;
    }

    private OnDialogClickListener negtiveListener, positiveListener;
    private TextView btnNegtive, btnPositive;
    private String pinStr;

    public PairPcDialog(Context context) {
        super(context, R.style.CommonDialog);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_pair_pc;
    }

    @Override
    protected void initView() {
        btnNegtive = findViewById(R.id.btnNegtive);
        btnPositive = findViewById(R.id.btnPositive);
        TextView tvMsg = findViewById(R.id.tvMsg);
        tvMsg.setText(getContext().getResources().getString(R.string.PairDeviceTip) + " " + pinStr);

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if (positiveListener != null) {
                    positiveListener.onDialogClick(PairPcDialog.this);
                }
            }
        });
    }

    public PairPcDialog setPositiveButton(OnDialogClickListener listener) {
        positiveListener = listener;
        return this;
    }

    public void setPinStr(String pinStr) {
        this.pinStr = pinStr;
    }

    @Override
    protected int getDialogWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getDialogHeight() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#99000000")));
    }
}
