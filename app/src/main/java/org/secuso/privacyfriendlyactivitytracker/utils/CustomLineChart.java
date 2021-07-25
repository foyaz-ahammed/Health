package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.github.mikephil.charting.charts.LineChart;

/**
 * 사용자정의 LineChart
 */
public class CustomLineChart extends LineChart {
    public CustomLineChart(Context context) {
        super(context);
    }

    public CustomLineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomLineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 마지막에 두드러진 부분을 한번 더 그리기
        if (valuesToHighlight())
            mRenderer.drawHighlighted(canvas, mIndicesToHighlight);
    }
}
