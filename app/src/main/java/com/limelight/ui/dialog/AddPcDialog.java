package com.limelight.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.limelight.R;

/**
 * 添加pc框
 */
public class AddPcDialog extends BaseDialog {
    public interface OnDialogClickListener {
        void onDialogClick(AddPcDialog dialog);
    }

    public static AddPcDialog showDialog(Context context, OnDialogClickListener confirmListener) {
        AddPcDialog dialog = new AddPcDialog(context);
        dialog.setPositiveButton(confirmListener);
        dialog.show();
        return dialog;
    }

    private String title, msg, negtiveBtnText, positiveBtnText;
    private OnDialogClickListener negtiveListener, positiveListener;
    private TextView btnNegtive, btnPositive;
    private TextView tvTitle, tvMsg;
    private EditText etDeviceIp;

    public AddPcDialog(Context context) {
        super(context, R.style.CommonDialog);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_add_pc;
    }

    @Override
    protected void initView() {
        btnNegtive = findViewById(R.id.btnNegtive);
        btnPositive = findViewById(R.id.btnPositive);
        tvTitle = findViewById(R.id.tvTitle);
        tvMsg = findViewById(R.id.tvMsg);
        etDeviceIp = findViewById(R.id.etDeviceIp);

        btnNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if (positiveListener != null) {
                    positiveListener.onDialogClick(AddPcDialog.this);
                }
            }
        });
    }

    public AddPcDialog setPositiveButton(OnDialogClickListener listener) {
        positiveListener = listener;
        return this;
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
