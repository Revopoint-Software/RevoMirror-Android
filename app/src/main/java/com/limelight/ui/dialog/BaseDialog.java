package com.limelight.ui.dialog;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * Base对话框
 */
public abstract class BaseDialog extends Dialog {
    protected View rootView;

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initSysUI();
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        rootView = LayoutInflater.from(getContext()).inflate(getLayoutId(), null);
        setContentView(rootView);
        initView();

        initWindow();
    }

    protected abstract int getLayoutId();

    protected abstract void initView();

    protected void initWindow() {
        setCanceledOnTouchOutside(isCanceledOnTouchOutside());
        setCancelable(isCancelable());

        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = getDialogWidth();
        layoutParams.height = getDialogHeight();
        window.setAttributes(layoutParams);
    }

    /**
     * 是否可取消
     *
     * @return
     */
    protected boolean isCancelable() {
        return false;
    }

    /**
     * 是否可取消
     *
     * @return
     */
    protected boolean isCanceledOnTouchOutside() {
        return isCancelable();
    }

    /**
     * 对话框宽
     *
     * @return
     */
    protected int getDialogWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    /**
     * 对话框高
     *
     * @return
     */
    protected int getDialogHeight() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    public void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    protected boolean needHideSysUi() {
        return true;
    }

    private void initSysUI() {
        hideSysUI();
        View decorView = getWindow().getDecorView();
        //监听系统ui可见性变化
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        // Note that system bars will only be "visible" if none of the
                        // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            // TODO: The system bars are visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.
                            hideSysUI();
                        } else {
                            // TODO: The system bars are NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.
                        }
                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSysUI();
        }
    }

    private void hideSysUI() {
        if (!needHideSysUi()) {
            return;
        }

        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
            ActionBar actionBar = getActionBar();
            if (null != actionBar) {
                actionBar.hide();
            }
        }
    }
}
