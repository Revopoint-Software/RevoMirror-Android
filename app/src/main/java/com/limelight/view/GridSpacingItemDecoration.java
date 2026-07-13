package com.limelight.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int spanCount;
    private final int spacing;
    private final boolean includeEdge;
    private Paint mPaint;

    public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.includeEdge = includeEdge;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.TRANSPARENT);
    }

    public void setDivideColor(int color) {
        mPaint.setColor(color);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int rowCount = (int) Math.ceil(parent.getAdapter().getItemCount() * 1f / spanCount);
        int column = position % spanCount;
        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
        }
        if (position < spanCount) {
            outRect.top = 0;
        }
        if (position >= (rowCount - 1) * spanCount) {
            outRect.bottom = 0;
            //LogUtil.i("================" + position + ", " + rowCount);
        } else {
            outRect.bottom = spacing;
            //LogUtil.i("================" + position + ", " + rowCount);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        c.save();
        int childCount = parent.getChildCount();
        int rowCount = (int) Math.ceil(parent.getAdapter().getItemCount() * 1f / spanCount);
        for (int i = 0; i < childCount; i++) {
            View childAt = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(childAt);
            int columnIndex = position % spanCount;
            int rowIndex = position / spanCount;

            if (columnIndex != spanCount - 1 && parent.getAdapter().getItemCount() != 1) {
                //画右侧分割线
                drawVertical(c, childAt);
            }
            if (rowIndex != rowCount - 1) {
                //画底侧分割线
                drawHorizontal(c, childAt);
            }
        }
        c.restore();
    }

    public void drawHorizontal(Canvas c, View childAt) {
        int left = childAt.getLeft();
        int right = childAt.getRight();
        int top = childAt.getBottom();
        int bottom = childAt.getBottom() + spacing;
        c.drawRect(left, top, right, bottom, mPaint);
    }

    public void drawVertical(Canvas c, View childAt) {
        int left = childAt.getRight();
        int right = left + spacing;
        int top = childAt.getTop();
        int bottom = childAt.getBottom() + spacing;
        c.drawRect(left, top, right, bottom, mPaint);
    }
}