package org.secuso.privacyfriendlyactivitytracker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import org.secuso.privacyfriendlyactivitytracker.R;

/**
 * 사용자정의 MarkerView
 */
@SuppressLint("ViewConstructor")
public class ChartMarkerView extends MarkerView {

    private final TextView tvContent;
    private boolean isFloat = false;
    private boolean isShown = true;
    private boolean isStep = false;

    public ChartMarkerView(Context context, int layoutResource, boolean isFloat, boolean isStep) {
        super(context, layoutResource);

        tvContent = findViewById(R.id.tvContent);
        this.isFloat = isFloat;
        this.isStep = isStep;
    }

    // MarkerView 가 재그리기될때 호출되는 함수, UI를 갱신하는데 리용
    @SuppressLint("DefaultLocale")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e.getY() == 0) {
            isShown = false;
            return;
        } else {
            isShown = true;
        }

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            if (isFloat) {
                if (isStep) {
                    tvContent.setText(String.valueOf(ce.getHigh()));
                }
                else tvContent.setText(String.format("%.1f", ce.getHigh()));
            }
            else
                tvContent.setText(String.valueOf((int) ce.getHigh()));
        } else {
            if (isFloat) {
                if (isStep) {
                    tvContent.setText(String.valueOf(e.getY()));
                }
                else tvContent.setText(String.format("%.1f", e.getY()));
            }
            else
                tvContent.setText(String.valueOf((int) e.getY()));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        if (!isShown)
            return;
        MPPointF offset = getOffsetForDrawingAtPoint(posX, posY);

        int saveId = canvas.save();
        //정확한 위치로 이동하고 그리기
        canvas.translate(posX + offset.x, posY + offset.y);
        draw(canvas);
        canvas.restoreToCount(saveId);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

