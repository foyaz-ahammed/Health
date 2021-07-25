package org.secuso.privacyfriendlyactivitytracker.heart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.secuso.privacyfriendlyactivitytracker.Utils;

/**
 * 카메리상태현시를 위한 view
 */
public class HeartView extends LinearLayout {
    Point screenSize = new Point(0, 0);
    Paint mBorderPaint = new Paint();

    public HeartView(Context context) {
        this(context, null);
    }

    public HeartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int radius = Utils.dip2px(60);
        int cx = screenSize.x;
        int cy = screenSize.y;
        Path path = drawHeart(cx, cy, radius);
        canvas.clipPath(path);

        super.dispatchDraw(canvas);
    }

    /**
     * 초기화
     */
    private void init() {
        screenSize =  new Point(getWidth(), getHeight());
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setStrokeWidth(1);
        mBorderPaint.setColor(Color.parseColor("#000000"));
    }

    /**
     * 심장모양 그리기
     */
    private Path drawHeart(int cx, int cy, int radius) {
        Path path = new Path();
        path.moveTo((float) radius / 2 + cx, (float) radius / 5 + cy);
        path.cubicTo((float) 5 * radius / 14 + cx, cy, cx, (float) radius / 15 + cy,
                (float) radius / 28 + cx, (float) 2 * radius / 5 + cy);
        path.cubicTo((float) radius / 14 + cx, (float) 2 * radius / 3 + cy, (float) 3 * radius / 7 + cx,
                (float) 5 * radius / 6 + cy, (float) radius / 2 + cx, (float) 9 * radius / 10 + cy);
        path.cubicTo((float) 4 * radius / 7 + cx, (float) 5 * radius / 6 + cy, (float) 13 * radius / 14 + cx,
                (float) 2 * radius / 3 + cy, (float) 27 * radius / 28 + cx, (float) 2 * radius / 5 + cy);
        path.cubicTo(radius + cx, (float) radius / 15 + cy, (float) 9 * radius / 14 + cx,
                cy, (float) radius / 2 + cx, (float) radius / 5 + cy);
        path.close();
        return path;
    }
}
