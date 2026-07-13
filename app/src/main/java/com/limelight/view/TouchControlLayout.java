package com.limelight.view;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.limelight.ui.StreamView;
import com.limelight.utils.LogUtil;

public class TouchControlLayout extends FrameLayout {
    private StreamView eventHandleView;

    public TouchControlLayout(Context context) {
        this(context, null);
    }

    public TouchControlLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TouchControlLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int pointCount = ev.getPointerCount();
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
        LogUtil.i("onInterceptTouchEvent " + pointCount + ", " + action);

        if(eventHandleView != null && action == MotionEvent.ACTION_POINTER_DOWN){
            eventHandleView.setHandleTouchEvent(true);

            //eventHandleView.onTouchEvent(ev);
            long downTime = SystemClock.uptimeMillis();
            long eventTime = downTime + 100; // 事件结束时间
            float x = ev.getX(); // 相对View的X坐标
            float y = ev.getY(); // 相对View的Y坐标
            int metaState = 0; // 元状态（如按键修饰符）

            //创建单点触控事件（ACTION_DOWN）
            MotionEvent downEvent = MotionEvent.obtain(
                    downTime,
                    eventTime,
                    MotionEvent.ACTION_DOWN,
                    x,
                    y,
                    metaState
            );
            eventHandleView.onTouchEvent(downEvent);

            //创建单点触控事件（ACTION_DOWN）
            MotionEvent down2Event = MotionEvent.obtain(
                    downTime+100,
                    eventTime+100,
                    MotionEvent.ACTION_POINTER_DOWN,
                    x,
                    y,
                    metaState
            );
            eventHandleView.onTouchEvent(down2Event);

            LogUtil.i("eventHandleView " + pointCount + ", " + action);
            return true;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(eventHandleView != null){
            if(event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL){
                eventHandleView.setHandleTouchEvent(false);
            }
            return eventHandleView.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    public void setDispatchTouchView(StreamView view){
        eventHandleView = view;
    }
}
