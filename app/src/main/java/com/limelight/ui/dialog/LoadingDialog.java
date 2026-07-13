package com.limelight.ui.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.ImageView;

import com.limelight.R;
import com.limelight.utils.AnimationUtil;

public class LoadingDialog extends BaseDialog {
    private ImageView ivLoading;

    public LoadingDialog(Context context) {
        super(context, R.style.CommonDialog);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_loading;
    }

    @Override
    protected void initView() {
        ivLoading = findViewById(R.id.ivLoading);
        AnimationUtil.startRotateView(ivLoading, 1500);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                AnimationUtil.stopRotateView(ivLoading);
            }
        });
    }

}
