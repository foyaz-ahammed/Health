package org.secuso.privacyfriendlyactivitytracker.layout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;

/**
 * 대화창 layout
 */
public class ConfirmLayout extends LinearLayout {

    int mLayoutWidth = 0;
    int mLayoutHeight = 0;
    float mBgRadius; // 모서리의 아로진 정도
    Path mClippedPath = new Path();
    RectF mViewRect = new RectF();

    public ConfirmLayout(Context context) {
        this(context, null);
    }

    public ConfirmLayout(Context context,
                            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ConfirmLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBgRadius = context.getResources().getDimension(R.dimen.round_rect_bg_radius);
    }

    /**
     * layout 너비설정함수
     * @param width 설정할 너비
     */
    public void setWidth(int width){
        mLayoutWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Calculate widthspec size, and set it to measure
        int widthSpec = MeasureSpec.makeMeasureSpec(mLayoutWidth, MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, heightMeasureSpec);

        //Set view path, and invalidate
        mLayoutHeight = getMeasuredHeight();
        mViewRect.set(0, 0, mLayoutWidth, mLayoutHeight);
        invalidate();
    }

    @Override
    public void dispatchDraw(Canvas canvas){
        mClippedPath.addRoundRect(mViewRect, mBgRadius, mBgRadius, Path.Direction.CCW);
        canvas.clipPath(mClippedPath);
        super.dispatchDraw(canvas);
    }
}
