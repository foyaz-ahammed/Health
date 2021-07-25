package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

/**
 * 선택대화창 기초 layout
 */
public class PickerLayout extends LinearLayout {

    int mLayoutWidth = 0;
    int mLayoutHeight = 0;
    RectF mViewRect = new RectF();

    public PickerLayout(Context context) {
        this(context, null);
    }

    public PickerLayout(Context context,
                            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PickerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    /**
     * width 설정 함수
     * @param width 설정할 width
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
}

