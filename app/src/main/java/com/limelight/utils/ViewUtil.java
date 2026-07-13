package com.limelight.utils;

import android.graphics.Paint;
import android.view.View;
import android.widget.TextView;

public class ViewUtil {

    public static int getTextWidth(TextView textView) {
        Paint paint = new Paint();
        paint.setTextSize(textView.getTextSize());
        return getTextWidth(paint, textView.getText().toString());
    }

    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    public static void setAvoidRepeatClick(View view, long timeoutMillis) {
        view.setEnabled(false);
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, timeoutMillis);
    }
}
