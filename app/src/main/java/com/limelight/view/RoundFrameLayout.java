package com.limelight.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.limelight.R;

public class RoundFrameLayout extends FrameLayout {
    private Path mPath;
    private Paint mPaint;
    private RectF mRectF;
    private float mRadius;
    private float mLeftTopRadius, mLeftBottomRadius, mRightTopRadius, mRightBottomRadius;
    private boolean isClipBackground;

    public RoundFrameLayout(Context context) {
        this(context, null);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundLayout);
        mRadius = ta.getDimension(R.styleable.RoundLayout_rlRadius, 0);
        isClipBackground = ta.getBoolean(R.styleable.RoundLayout_rlClipBackground, true);
        mLeftTopRadius = ta.getDimension(R.styleable.RoundLayout_leftTopRadius, 0);
        mLeftBottomRadius = ta.getDimension(R.styleable.RoundLayout_leftBottomRadius, 0);
        mRightTopRadius = ta.getDimension(R.styleable.RoundLayout_rightTopRadius, 0);
        mRightBottomRadius = ta.getDimension(R.styleable.RoundLayout_rightBottomRadius, 0);
        ta.recycle();

        mPath = new Path();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
    }

    public void setRadius(float radius) {
        mRadius = radius;
        postInvalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, h);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void draw(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= 28) {
            draw28(canvas);
        } else {
            draw27(canvas);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= 28) {
            dispatchDraw28(canvas);
        } else {
            dispatchDraw27(canvas);
        }
    }

    private void draw27(Canvas canvas) {
        if (isClipBackground) {
            canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.drawPath(genPath(), mPaint);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }

    private void draw28(Canvas canvas) {
        if (isClipBackground) {
            canvas.save();
            canvas.clipPath(genPath());
            super.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas);
        }
    }

    private void dispatchDraw27(Canvas canvas) {
        canvas.saveLayer(mRectF, null, Canvas.ALL_SAVE_FLAG);
        super.dispatchDraw(canvas);
        canvas.drawPath(genPath(), mPaint);
        canvas.restore();
    }

    private void dispatchDraw28(Canvas canvas) {
        canvas.save();
        canvas.clipPath(genPath());
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private Path genPath() {
        mPath.reset();
        if (mRadius > 0) {
            mPath.addRoundRect(mRectF, mRadius, mRadius, Path.Direction.CW);
        } else if (mLeftTopRadius > 0 || mLeftBottomRadius > 0 || mRightTopRadius > 0 || mRightBottomRadius > 0) {
            mPath.addRoundRect(mRectF,
                    new float[]{mLeftTopRadius, mLeftTopRadius, mRightTopRadius, mRightTopRadius, mRightBottomRadius, mRightBottomRadius, mLeftBottomRadius, mLeftBottomRadius,},
                    Path.Direction.CW);
        } else {
            mPath.addRect(mRectF, Path.Direction.CW);
        }
        return mPath;
    }
}
