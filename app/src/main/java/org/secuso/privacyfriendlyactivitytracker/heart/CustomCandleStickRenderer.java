package org.secuso.privacyfriendlyactivitytracker.heart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.CandleDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineScatterCandleRadarDataSet;
import com.github.mikephil.charting.renderer.CandleStickChartRenderer;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * 사용자 지정 CandleStick renderer
 */
public class CustomCandleStickRenderer extends CandleStickChartRenderer {
    public CustomCandleStickRenderer(CandleDataProvider chart, ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(chart, animator, viewPortHandler);
    }

    /**
     * 그라프를 클릭하였을때 두드러진부분 표시
     */
    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {

        CandleData candleData = mChart.getCandleData();

        for (Highlight high : indices) {

            ICandleDataSet set = candleData.getDataSetByIndex(high.getDataSetIndex());

            if (set == null || !set.isHighlightEnabled())
                continue;

            CandleEntry e = set.getEntryForXValue(high.getX(), high.getY());

            if (!isInBoundsX(e, set))
                continue;

            float lowValue = e.getLow() * mAnimator.getPhaseY();
            float highValue = e.getHigh() * mAnimator.getPhaseY();
            float y = (lowValue + highValue) / 2f;

            MPPointD pix = mChart.getTransformer(set.getAxisDependency()).getPixelForValues(e.getX(), y);

            high.setDraw((float) pix.x, (float) pix.y);

            // draw the highlight
            drawHighlight(c, (float) pix.x, (float) pix.y, set, e);
        }
    }

    /**
     * 두드러진부분 그리기
     */
    protected void drawHighlight(Canvas c, float x, float y, ILineScatterCandleRadarDataSet set, CandleEntry e) {

        Paint paint = new Paint();
        paint.setColor(set.getHighLightColor());
        Transformer trans = mChart.getTransformer(((CandleDataSet)set).getAxisDependency());
        float[] buffers = new float[4];
        buffers[0] = e.getX() - 0.5f + ((CandleDataSet)set).getBarSpace();
        buffers[1] = e.getClose() * mAnimator.getPhaseY();
        buffers[2] = e.getX() + 0.5f - ((CandleDataSet)set).getBarSpace();
        buffers[3] = e.getOpen() * mAnimator.getPhaseY();
        trans.pointValuesToPixel(buffers);
        RectF rectF = new RectF(buffers[0], buffers[1], buffers[2], buffers[3]);
        c.drawRect(rectF, paint);
    }
}
