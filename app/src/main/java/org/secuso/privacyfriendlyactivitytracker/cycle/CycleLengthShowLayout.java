package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;

/**
 * 생리기록현시를 위한 layout
 */
public class CycleLengthShowLayout extends LinearLayout {
    int x = 0; //평균기간 선표시할 x 좌표
    Paint boundaryPaint = new Paint();

    public CycleLengthShowLayout(Context context) {
        this(context, null);
    }

    public CycleLengthShowLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CycleLengthShowLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        boundaryPaint.setColor(getResources().getColor(R.color.cycle_calendar_predict_dot_color));
        boundaryPaint.setStyle(Paint.Style.STROKE);
        boundaryPaint.setStrokeWidth(getResources().getDimension(R.dimen.width_circle_stroke));
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{15, 15}, (float)1.0);
        boundaryPaint.setPathEffect(dashPathEffect);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawLine(x, 0, x, getHeight(), boundaryPaint);
    }

    /**
     * 평균기간 선 위치설정 함수
     * @param x 위치
     */
    public void setDotLinePosition(int x) {
        this.x = x;
        invalidate();
    }
}
