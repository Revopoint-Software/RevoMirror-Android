package com.limelight.ui.dialog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.WindowManager;

import com.limelight.R;

/**
 * 底部选择框
 */
public class BottomMenuDialog extends BaseDialog {
    public interface OnConfirmSelectListener {
        void onConfirmSelect(int type);
    }

    public static BottomMenuDialog showDialog(Context context, OnConfirmSelectListener listener) {
        BottomMenuDialog dialog = new BottomMenuDialog(context, listener);
        dialog.show();
        return dialog;
    }

    private OnConfirmSelectListener listener;
    private View layoutRoot, layoutDialog;

    public BottomMenuDialog(Context context, OnConfirmSelectListener listener) {
        super(context, R.style.CommonDialog);
        this.listener = listener;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_bottom_menu;
    }

    @Override
    protected void initView() {
        layoutRoot = findViewById(R.id.layoutRoot);
        layoutDialog = findViewById(R.id.layoutDialog);
        View btnRemoveHost = findViewById(R.id.btnRemoveHost);
        btnRemoveHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (listener != null) {
                    listener.onConfirmSelect(1);
                }
            }
        });
        View btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        layoutDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

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
