package com.limelight.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.limelight.R;

/**
 * 确认框
 */
public class ConfirmDialog extends BaseDialog {
    public interface OnDialogClickListener {
        void onDialogClick(ConfirmDialog dialog);
    }

    public static ConfirmDialog showDialog(Context context, String msg, String btnText, OnDialogClickListener confirmListener) {
        ConfirmDialog dialog = new ConfirmDialog(context);
        dialog.setMessage(msg).setPositiveButton(btnText, confirmListener);
        dialog.show();
        return dialog;
    }

    public static ConfirmDialog showDialog(Context context, String title, String msg, String btnText, OnDialogClickListener confirmListener) {
        ConfirmDialog dialog = new ConfirmDialog(context);
        dialog.setTitle(title).setMessage(msg).setPositiveButton(btnText, confirmListener);
        dialog.show();
        return dialog;
    }

    public static ConfirmDialog showDialog(Context context, String title, String msg, String confirmBtnText, OnDialogClickListener confirmListener,
                                           String cancelBtnText, OnDialogClickListener cancelListener) {
        ConfirmDialog dialog = new ConfirmDialog(context);
        dialog.setTitle(title).setMessage(msg).setPositiveButton(confirmBtnText, confirmListener).setNegtiveButton(cancelBtnText, cancelListener);
        dialog.show();
        return dialog;
    }

    private String title, msg, negtiveBtnText, positiveBtnText;
    private OnDialogClickListener negtiveListener, positiveListener;
    private TextView btnNegtive, btnPositive;
    private TextView tvTitle, tvMsg;

    public ConfirmDialog(Context context) {
        super(context, R.style.CommonDialog);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_confirm;
    }

    @Override
    protected void initView() {
        btnNegtive = findViewById(R.id.btnNegtive);
        btnPositive = findViewById(R.id.btnPositive);
        tvTitle = findViewById(R.id.tvTitle);
        tvMsg = findViewById(R.id.tvMsg);

        btnNegtive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if (negtiveListener != null) {
                    negtiveListener.onDialogClick(ConfirmDialog.this);
                }
            }
        });
        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
                if (positiveListener != null) {
                    positiveListener.onDialogClick(ConfirmDialog.this);
                }
            }
        });

        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
            tvTitle.setVisibility(View.VISIBLE);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);
            tvMsg.setVisibility(View.VISIBLE);
        } else {
            tvMsg.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(negtiveBtnText)) {
            btnNegtive.setVisibility(View.VISIBLE);
            btnNegtive.setText(negtiveBtnText);
        } else {
            btnNegtive.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(positiveBtnText)) {
            btnPositive.setVisibility(View.VISIBLE);
            btnPositive.setText(positiveBtnText);
        } else {
            btnPositive.setVisibility(View.GONE);
        }
    }

    public ConfirmDialog setTitle(String title) {
        this.title = title;
        if (tvTitle != null) {
            tvTitle.setText(title);
            if (TextUtils.isEmpty(title)) {
                tvTitle.setVisibility(View.GONE);
            } else {
                tvTitle.setVisibility(View.VISIBLE);
            }
        }
        return this;
    }

    public ConfirmDialog setMessage(String msg) {
        this.msg = msg;
        if (tvMsg != null) {
            tvMsg.setText(msg);
            if (TextUtils.isEmpty(msg)) {
                tvMsg.setVisibility(View.GONE);
            } else {
                tvMsg.setVisibility(View.VISIBLE);
            }
        }
        return this;
    }

    public ConfirmDialog setNegtiveButton(String btnText, OnDialogClickListener listener) {
        negtiveBtnText = btnText;
        negtiveListener = listener;
        if (btnNegtive != null) {
            btnNegtive.setText(btnText);
            btnNegtive.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public ConfirmDialog setPositiveButton(String btnText, OnDialogClickListener listener) {
        positiveBtnText = btnText;
        positiveListener = listener;
        if (btnPositive != null) {
            btnPositive.setText(btnText);
            btnPositive.setVisibility(View.VISIBLE);
        }
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
