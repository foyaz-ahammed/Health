package org.secuso.privacyfriendlyactivitytracker.heart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.TextureView;

import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 심박수측정 그라프를 위한 클라스
 */
class ChartDrawer
{
    private  final TextureView chartTextureView;
    private final Paint paint = new Paint();
    private final Paint fillWhite = new Paint();

    ChartDrawer(TextureView chartTextureView, Context context) {
        this.chartTextureView = chartTextureView;


        paint.setStyle(Paint.Style.STROKE);

        paint.setColor(0xffff793c);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);

        fillWhite.setStyle(Paint.Style.FILL);
        fillWhite.setColor(ContextCompat.getColor(context, R.color.main_background_color));

    }

    /**
     * 심박수측정그라프 그리기
     * @param data 그라프 그리기 관련 자료
     */
    void draw(CopyOnWriteArrayList<Measurement<Float>> data) {
        Log.w("ChartDrawer ", "draw is called current thread is " + Thread.currentThread());
        Canvas chartCanvas = chartTextureView.lockCanvas();

        if (chartCanvas == null) return;

        chartCanvas.drawPaint(fillWhite);
        Path graphPath = new Path();

        float width = (float)chartCanvas.getWidth();
        float height = (float)chartCanvas.getHeight();
        int dataAmount = data.size();

        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;

        for (Measurement<Float> dataPoint :data) {
            if (dataPoint.measurement < min) min = dataPoint.measurement;
            if (dataPoint.measurement > max) max = dataPoint.measurement;
        }

        min = min - 0.01f;
        max = max + 0.01f;

        graphPath.moveTo(
                0,
                height * (data.get(0).measurement - min) / (max - min) );

        for (int dotIndex = 1; dotIndex < dataAmount; dotIndex++) {
            graphPath.lineTo(
                    width * (dotIndex) / dataAmount,
                    height * (data.get(dotIndex).measurement - min) / (max - min) );

        }

        chartCanvas.drawPath(graphPath, paint);


        chartTextureView.unlockCanvasAndPost(chartCanvas);
    }

}
