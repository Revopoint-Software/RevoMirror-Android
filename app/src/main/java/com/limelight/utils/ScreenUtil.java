package com.limelight.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DisplayCutout;
import android.view.View;
import android.view.WindowInsets;

import com.limelight.App;

import java.util.List;

public class ScreenUtil {

    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static float sp2px(Context context, float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

    public static Point getScreenSize(Context context) {
        final int x = context.getResources().getDisplayMetrics().widthPixels;
        final int y = context.getResources().getDisplayMetrics().heightPixels;
        return new Point(x, y);
    }

    public static Point getWindowSize(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static boolean isLandscape(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static boolean isPortrait(Context context) {
        Configuration configuration = context.getResources().getConfiguration();
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * view相对屏幕窗口的位置
     *
     * @param view
     * @return
     */
    public static Point getViewLocationDefault(View view) {
        return getViewLocationInWindow(view);
    }

    /**
     * view相对屏幕的位置，包含状态栏
     *
     * @param view
     * @return
     */
    public static Point getViewLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0], location[1]);
    }

    /**
     * view相对窗口的位置，不包含状态栏
     *
     * @param view
     * @return
     */
    public static Point getViewLocationInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        return new Point(location[0], location[1]);
    }

    /**
     * 屏幕适配处理
     */
    public static void adapteScreenAutoSize() {
        //AutoSizeConfig.getInstance().setCustomFragment(true);
//        AutoSizeConfig.getInstance().setUseDeviceSize(true);
//        try {
//            Class<?> clazz = AutoSizeCompat.class;
//            Field field = clazz.getField("mCache");
//            Object fieldObj = field.get(clazz);
//            if (fieldObj instanceof SparseArray) {
//                SparseArray<DisplayMetricsInfo> map = (SparseArray) fieldObj;
//                map.clear();
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
    }

    /**
     * 适配刘海屏
     *
     * @param rootView
     */
    public static void adapterDisplayCutout(View rootView) {
        //设置布局显示区域，不显示在刘海屏区域
        if (rootView == null) return;
        rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DisplayCutout cutout = insets.getDisplayCutout();
                    if (cutout == null) {
                        LogUtil.i("cutout==null, is not notch screen");
                    } else {
                        List<Rect> rects = cutout.getBoundingRects();
                        if (rects == null || rects.isEmpty()) {
                            LogUtil.i("rects==null || rects.size()==0, is not notch screen");
                        } else {
                            rootView.setPadding(cutout.getSafeInsetLeft(), cutout.getSafeInsetTop(),
                                    cutout.getSafeInsetRight(), cutout.getSafeInsetBottom());
                        }
                    }
                }
                return insets;
            }
        });
    }

    /**
     * 只适配右侧刘海屏
     *
     * @param rootView
     */
    public static void adapterRightDisplayCutout(View rootView) {
        //设置布局显示区域，不显示在刘海屏区域
        if (rootView == null) return;
        rootView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    DisplayCutout cutout = insets.getDisplayCutout();
                    if (cutout == null) {
                        LogUtil.i("cutout==null, is not notch screen");
                    } else {
                        List<Rect> rects = cutout.getBoundingRects();
                        if (rects == null || rects.isEmpty()) {
                            LogUtil.i("rects==null || rects.size()==0, is not notch screen");
                        } else {
                            rootView.setPadding(0, cutout.getSafeInsetTop(),
                                    cutout.getSafeInsetRight(), cutout.getSafeInsetBottom());
                        }
                    }
                }
                return insets;
            }
        });
    }

    /**
     * 当前是否在屏幕适配的适配间隙，如果是的话，可能会导致屏幕适配变形问题。
     *
     * @param context
     * @return
     */
    public static boolean isInAutoSizeMatchErrorDuration(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (displayMetrics.density > 5) {
            return true;
        }
        return false;
    }

    public static void printScreenInfo(Context context) {
        Point point = getScreenSize(context);
        LogUtil.i("printScreenInfo, screenSize : " + point.x + ", " + point.y);
        boolean isPortrait = isPortrait(context);
        LogUtil.i("printScreenInfo, isPortrait : " + isPortrait);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        LogUtil.i("printScreenInfo, density : " + displayMetrics.density + ", densityDpi : " + displayMetrics.densityDpi);
    }

    public static int getStatusBarHeight() {
        Context context = App.getContext();
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            int statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
            return statusBarHeight;
        }
        return 48;
    }
}  