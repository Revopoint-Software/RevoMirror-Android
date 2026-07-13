package com.limelight.ui;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.limelight.App;
import com.limelight.utils.LogUtil;

public class StreamView extends SurfaceView {
    enum TouchMode {NONE, DRAG, ZOOM}

    private TouchMode mMode = TouchMode.NONE;
    float mDistanceOrigin = -1;
    PointF mOneFingerOrigin = new PointF(0, 0);
    PointF mTwoFingerOrigin = new PointF(0, 0);
    PointF mOneFingerCur = new PointF(0, 0);
    PointF mTwoFingerCur = new PointF(0, 0);
    PointF mPointOrigin = new PointF(0, 0);
    PointF mPointCur = new PointF(0, 0);

    private boolean showSkipEvent = false;

    private double desiredAspectRatio;
    private InputCallbacks inputCallbacks;

    private boolean enableZoomAndPan = true;  // 开关变量，控制缩放和平移功能
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleDetector;
    private float scaleFactor = 1.0f;
    private float scaleFactorStart = 1.0f;
    private float posX = 0;
    private float posY = 0;
    private float posXStart = 0;
    private float posYStart = 0;

    private float initX;
    private float initY;
    private boolean initFlag;
    private boolean isScaleMode = false;

    private int TRIGGLE_ZOOM_VALUE = 30;
    private int TRIGGLE_DRAG_VALUE = 40;

    public void setDesiredAspectRatio(double aspectRatio) {
        this.desiredAspectRatio = aspectRatio;
    }

    public void setInputCallbacks(InputCallbacks callbacks) {
        this.inputCallbacks = callbacks;
    }

    public StreamView(Context context) {
        super(context);
        init(context);
    }

    public StreamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public StreamView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public StreamView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        // 初始化手势检测器
        gestureDetector = new GestureDetector(context, new GestureListener());
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // If no fixed aspect ratio has been provided, simply use the default onMeasure() behavior
        if (desiredAspectRatio == 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }

        // Based on code from: https://www.buzzingandroid.com/2012/11/easy-measuring-of-custom-views-with-specific-aspect-ratio/
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measuredHeight, measuredWidth;
        if (widthSize > heightSize * desiredAspectRatio) {
            measuredHeight = heightSize;
            measuredWidth = (int) (measuredHeight * desiredAspectRatio);
        } else {
            measuredWidth = widthSize;
            measuredHeight = (int) (measuredWidth / desiredAspectRatio);
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // This callbacks allows us to override dumb IME behavior like when
        // Samsung's default keyboard consumes Shift+Space.
        if (inputCallbacks != null) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (inputCallbacks.handleKeyDown(event)) {
                    return true;
                }
            } else if (event.getAction() == KeyEvent.ACTION_UP) {
                if (inputCallbacks.handleKeyUp(event)) {
                    return true;
                }
            }
        }

        return super.onKeyPreIme(keyCode, event);
    }

    public interface InputCallbacks {
        boolean handleKeyUp(KeyEvent event);

        boolean handleKeyDown(KeyEvent event);
    }

    public void setEnableZoomAndPan(boolean enableZoomAndPan) {
        this.enableZoomAndPan = enableZoomAndPan;
    }

    public boolean isEnableZoomAndPan() {
        return enableZoomAndPan;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!initFlag) {
            initFlag = true;
            App.getContext().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initX = getX();
                    initY = getY();
                    posX = getX();
                    posY = getY();
                }
            }, 300);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtil.i("onInterceptTouchEvent onTouchEvent 1111");
        if (handleEvent) {
            // 同时处理缩放和拖动手势

            int pointCount = event.getPointerCount();
            int action = event.getAction() & MotionEvent.ACTION_MASK;

            if (action == MotionEvent.ACTION_POINTER_DOWN) {
                LogUtil.i("ACTION_POINTER_DOWN ======");
                changeMode(TouchMode.NONE, event);
                showSkipEvent = true;
            } else if (pointCount == 2) {
                if (mOneFingerOrigin.x < 0) {
                    mOneFingerOrigin.set(event.getX(0), event.getY(0));
                    mTwoFingerOrigin.set(event.getX(1), event.getY(1));
                }

                switch (action) {
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        LogUtil.i("ACTION_POINTER_DOWN ======");
                        changeMode(TouchMode.NONE, event);
                    }
                    break;
                    case MotionEvent.ACTION_MOVE: {
                        float distance = sqrDistance(event);

                        float result = 0;
                        if (mDistanceOrigin != -1) {
                            result = Math.abs(distance - mDistanceOrigin);
                        } else {
                            mDistanceOrigin = sqrDistance(event);
                        }

                        LogUtil.i("TRIGGLE_ZOOM_VALUE ====== " + distance + ", " + mDistanceOrigin);
                        LogUtil.i("TRIGGLE_ZOOM_VALUE ====== " + TRIGGLE_ZOOM_VALUE);
                        LogUtil.i("mDistanceOrigin ====== " + distance + ", " + mDistanceOrigin + ", " + result + ", " + mMode.name());
                        //mDistanceOrigin = distance;
                        if (mMode == TouchMode.NONE) {
                            //判断是否缩放动作
                            if (result > TRIGGLE_ZOOM_VALUE) {
                                changeMode(TouchMode.ZOOM, event);
                            } else {
                                //判断是否拖拽动作
                                mOneFingerCur.set(event.getX(0), event.getY(0));
                                mTwoFingerCur.set(event.getX(1), event.getY(1));
                                mPointOrigin.set((mOneFingerOrigin.x + mTwoFingerOrigin.x) / 2, (mOneFingerOrigin.y + mTwoFingerOrigin.y) / 2);
                                mPointCur.set((mOneFingerCur.x + mTwoFingerCur.x) / 2, (mOneFingerCur.y + mTwoFingerCur.y) / 2);
                                float moveDistance = sqrDistance(mPointOrigin, mPointCur);
                                LogUtil.i("moveDistance ====== " + moveDistance);
                                if (moveDistance > TRIGGLE_DRAG_VALUE) {
                                    changeMode(TouchMode.DRAG, event);
                                }
                            }
                        } else if (mMode == TouchMode.DRAG) {
                            //双指滑动，移动界面
                            return gestureDetector.onTouchEvent(event);
                        } else if (mMode == TouchMode.ZOOM) {
                            return scaleDetector.onTouchEvent(event);
                        }
                    }
                    break;
                    case MotionEvent.ACTION_POINTER_UP: {
                        LogUtil.i("point2 up");
                        changeMode(TouchMode.NONE, event);
                        mDistanceOrigin = -1;
                        mOneFingerOrigin.set(-1, -1);
                        mTwoFingerOrigin.set(-1, -1);
                    }
                    break;
                    default: {
                        LogUtil.i("2 point Action not captured");
                    }
                    break;
                }
            }
            return true;

            /*gestureDetector.onTouchEvent(event);
            scaleDetector.onTouchEvent(event);
            return true;*/
        }
        return super.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            LogUtil.i("aaaaaaaaaaaaaaaaaaa " + distanceX + ", " + distanceY);
            LogUtil.i("aaaaaaaaaaaaaaaaaaa " + posX + ", " + posY);

            if (showSkipEvent) {
                showSkipEvent = false;
                return true;
            }


            // 处理拖动
            posX -= distanceX;
            posY -= distanceY;

            // 限制移动在父控件范围内
            //checkBounds();

            // 更新视图位置
            setX(posX);
            setY(posY);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            // 双击复位
            scaleFactor = 1.0f;
            posX = initX;
            posY = initY;
            setScaleX(scaleFactor);
            setScaleY(scaleFactor);
            setX(posX);
            setY(posY);
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            // 缩放过程中更新缩放比例
            scaleFactor = scaleFactorStart * detector.getScaleFactor();
            scaleFactor = Math.max(1f, Math.min(scaleFactor, 5.0f)); // 限制缩放范围
            LogUtil.i("onScale   " + detector.getScaleFactor() + ", " + scaleFactor);
            // 设置缩放
            setScaleX(scaleFactor);
            setScaleY(scaleFactor);
            checkBounds(detector.getFocusX(), detector.getFocusY());

            setX(posX);
            setY(posY);

            return false;
        }

        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
            scaleFactor = scaleFactorStart;
            posX = getX();
            posY = getY();
            posXStart = getX();
            posYStart = getY();
            LogUtil.i("onScaleBegin   " + posXStart + ", " + posYStart + ", " + detector.getFocusX() + ", " + detector.getFocusY());
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            scaleFactorStart = scaleFactor;
            posXStart = posX;
            posYStart = posY;
            LogUtil.i("onScaleEnd   " + posX + ", " + posX + ", " + detector.getFocusX() + ", " + detector.getFocusY());
            super.onScaleEnd(detector);
        }
    }

    private void checkBounds(float focusX, float focusY) {
//        if(scaleFactor>1.0f){
//            return;
//        }
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) return;

        // 获取父控件的宽度和高度
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();

        // 获取 SurfaceView 缩放后的宽度和高度
        float viewWidth = getWidth() * scaleFactor;
        float viewHeight = getHeight() * scaleFactor;

        LogUtil.i("aaaaaaaaaaaaa " + parentWidth + "," + parentHeight);
        LogUtil.i("aaaaaaaaaaaaa " + getWidth() + "," + getHeight());
        LogUtil.i("aaaaaaaaaaaaa " + viewWidth + "," + viewHeight);
        LogUtil.i("aaaaaaaaaaaaa " + focusX + "," + focusY + ", " + scaleFactor);
        LogUtil.i("aaaaaaaaaaaaa " + posXStart + "," + posYStart);

        // 限制 posX 和 posY 在边界内
        posX = initX + (viewWidth - getWidth()) / 2 - focusX * (scaleFactor - 1);
        posY = initY + (viewHeight - getHeight()) / 2 - focusY * (scaleFactor - 1);
        LogUtil.i("aaaaaaaaaaaaabbbbbb " + posX + "," + posY);
//        posX = Math.max(0, Math.min(posX, parentWidth - viewWidth));
//        posY = Math.max(0, Math.min(posY, parentHeight - viewHeight));
        LogUtil.i("aaaaaaaaaaaaabbbbbb1111 " + getX() + "," + getY());
    }

    private void checkBounds() {
        if (scaleFactor > 1.0f) {
            return;
        }
        ViewGroup parent = (ViewGroup) getParent();
        if (parent == null) return;

        // 获取父控件的宽度和高度
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();

        // 获取 SurfaceView 缩放后的宽度和高度
        float viewWidth = getWidth() * scaleFactor;
        float viewHeight = getHeight() * scaleFactor;
        LogUtil.i("checkBounds 000 " + parentWidth + "," + viewWidth + ", " + parentHeight + "," + viewHeight + scaleFactor);
        LogUtil.i("checkBounds 111 " + posX + "," + posY);
        // 限制 posX 和 posY 在边界内
        posX = Math.max(0, Math.min(posX, parentWidth - viewWidth));
        posY = Math.max(0, Math.min(posY, parentHeight - viewHeight));
        LogUtil.i("checkBounds 222 " + posX + "," + posY);
    }

    public Pair<Integer, Integer> getConvertEventPos(float srcPosX, float srcPosY) {
        LogUtil.i("getConvertEventPos " + srcPosX + "," + srcPosY);
        LogUtil.i("getConvertEventPos " + initX + "," + initY);
        LogUtil.i("getConvertEventPos " + posX + "," + posY);
        LogUtil.i("getConvertEventPos " + scaleFactor);

        float viewWidth = getWidth() * scaleFactor;
        float viewHeight = getHeight() * scaleFactor;

        int newPosX = (int) (((srcPosX - posX + (viewWidth - getWidth()) / 2) / scaleFactor));
        int newPosY = (int) (((srcPosY - posY + (viewHeight - getHeight()) / 2) / scaleFactor));

        LogUtil.i("getConvertEventPos " + newPosX + "," + newPosY);
        return new Pair<>(newPosX, newPosY);
    }

    public float getScaleFactor() {
        return scaleFactor;
    }

    private boolean handleEvent = false;

    public void setHandleTouchEvent(boolean handleEvent) {
        this.handleEvent = handleEvent;
    }


    private void changeMode(TouchMode mode, MotionEvent event) {
        if (mMode == mode) {
            return;
        }
        LogUtil.i("ChangeMode:" + mode.name());
        // exit pre mode
        switch (mMode) {
            case DRAG: {
            }
            break;
            case ZOOM: {
            }
            break;
            default:
                break;
        }

        mMode = mode;
        // enter new mode
        switch (mMode) {
            case DRAG: {
                mDistanceOrigin = sqrDistance(event);
//                mTwoFingerOrigin.x=event.getX(1);
//                mTwoFingerOrigin.y=event.getY(1);
//                mOneFingerOrigin.x=event.getX(0);
//                mOneFingerOrigin.y=event.getY(0);
            }
            break;
            case ZOOM: {
                mDistanceOrigin = sqrDistance(event);
//                mTwoFingerOrigin.x=event.getX(1);
//                mTwoFingerOrigin.y=event.getY(1);
//                mOneFingerOrigin.x=event.getX(0);
//                mOneFingerOrigin.y=event.getY(0);
            }
            break;
            default:
                break;
        }
    }

    private float sqrDistance(MotionEvent event) {
        try {
            float x = event.getX(0) - event.getX(1);
            float y = event.getY(0) - event.getY(1);
            return (float) (Math.sqrt(x * x + y * y));
        } catch (Exception ex) {
        }
        return 0;
    }

    private float sqrDistance(PointF pointF1, PointF pointF2) {
        try {
            float x = pointF1.x - pointF2.x;
            float y = pointF1.y - pointF2.y;
            return (float) (Math.sqrt(x * x + y * y));
        } catch (Exception ex) {
        }
        return 0;
    }
}
