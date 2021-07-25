package org.secuso.privacyfriendlyactivitytracker.heart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;

/**
 * 결과창의 심박수표시띠
 */
public class HeartRateBar extends View {
    Paint backgroundPaint = new Paint();
    Paint boundaryPaint = new Paint();
    Paint heartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint restingAreaPaint = new Paint();
    Paint minMaxTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint restingTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint afterExerciseAreaPaint = new Paint();
    Paint maxExerciseTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    RectF rect = new RectF();

    int value = 60;
    int radius;

    boolean isAfterExercise = false; // 운동후 표시여부

    public HeartRateBar(Context context) {
        this(context, null);
    }

    public HeartRateBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeartRateBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int cx = isAfterExercise ? width / 160 * (value - 60) : width / 80 * (value - 40);

        Path path = drawHeart(cx - Utils.dip2px(5), 0, radius);
        rect.set(0, Utils.dip2px(10), width, height - Utils.dip2px(20));

        canvas.drawPath(path, heartPaint);
        canvas.drawRect(rect, backgroundPaint);

        // Min/Max 값 그리기
        minMaxTextPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(isAfterExercise ? "60" : "40", Utils.dip2px(5), height - Utils.dip2px(23), minMaxTextPaint);
        minMaxTextPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("120", width - Utils.dip2px(5), height - Utils.dip2px(23), minMaxTextPaint);

        if (!isAfterExercise) {
            // 안정구간 그리기
            rect.set((int) (width / 80 * 21), Utils.dip2px(10), (int) (width / 80 * 36), height - Utils.dip2px(20));
            canvas.drawRect(rect, restingAreaPaint);
            restingTextPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText("61", (int) (width / 80 * 21) + Utils.dip2px(2), height - Utils.dip2px(23), restingTextPaint);
            restingTextPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("76", (int) (width / 80 * 36) - Utils.dip2px(2), height - Utils.dip2px(23), restingTextPaint);
            restingTextPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(getResources().getString(R.string.average_resting_range), (int) (width / 80 * 29), height - Utils.dip2px(5), restingTextPaint);
        } else {
            // 운동후인 경우 개별적인 구간 그리기
            afterExerciseAreaPaint.setColor(0xffffd800);
            rect.set((int) (width / 9 * 2), Utils.dip2px(10), (int) (width / 9 * 3), height - Utils.dip2px(20));
            canvas.drawRect(rect, afterExerciseAreaPaint);
            afterExerciseAreaPaint.setColor(0xffff9600);
            rect.set((int) (width / 9 * 3), Utils.dip2px(10), (int) (width / 9 * 4), height - Utils.dip2px(20));
            canvas.drawRect(rect, afterExerciseAreaPaint);
            afterExerciseAreaPaint.setColor(0xffff6801);
            rect.set((int) (width / 9 * 4), Utils.dip2px(10), (int) (width / 9 * 5), height - Utils.dip2px(20));
            canvas.drawRect(rect, afterExerciseAreaPaint);
            afterExerciseAreaPaint.setColor(0xfffc4300);
            rect.set((int) (width / 9 * 5), Utils.dip2px(10), (int) (width / 9 * 6), height - Utils.dip2px(20));
            canvas.drawRect(rect, afterExerciseAreaPaint);
            afterExerciseAreaPaint.setColor(0xffc91401);
            rect.set((int) (width / 9 * 6), Utils.dip2px(10), width, height - Utils.dip2px(20));
            canvas.drawRect(rect, afterExerciseAreaPaint);
            canvas.drawText("220", width - Utils.dip2px(5), height - Utils.dip2px(23), maxExerciseTextPaint);
        }

        boundaryPaint.setColor(getResources().getColor(isAfterExercise ? R.color.white : R.color.heart_rate_circle_color));
        canvas.drawLine(cx, Utils.dip2px(10), cx, height - Utils.dip2px(20), boundaryPaint);
    }

    /**
     * 초기화함수
     */
    private void init() {
        backgroundPaint.setColor(getResources().getColor(R.color.heart_rate_bar_background_color));
        restingAreaPaint.setColor(getResources().getColor(R.color.average_resting_range));

        boundaryPaint.setColor(getResources().getColor(R.color.heart_rate_circle_color));
        boundaryPaint.setStyle(Paint.Style.STROKE);
        boundaryPaint.setStrokeWidth(getResources().getDimension(R.dimen.width_circle_stroke));
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5, 5}, (float)1.0);
        boundaryPaint.setPathEffect(dashPathEffect);

        heartPaint.setColor(getResources().getColor(R.color.heart_rate_circle_color));
        heartPaint.setStyle(Paint.Style.FILL);

        maxExerciseTextPaint.setTextAlign(Paint.Align.RIGHT);
        maxExerciseTextPaint.setTextSize(Utils.dip2px(12));
        maxExerciseTextPaint.setColor(getResources().getColor(R.color.heart_rate_bar_background_color));

        minMaxTextPaint.setTextSize(Utils.dip2px(12));
        minMaxTextPaint.setColor(getResources().getColor(R.color.heart_rate_bar_text_color));

        restingTextPaint.setTextSize(Utils.dip2px(12));
        restingTextPaint.setColor(getResources().getColor(R.color.heart_rate_circle_color));

        radius = Utils.dip2px(10);
    }

    /**
     * 심박수값 설정
     * @param value 설정할 심박수값
     */
    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    /**
     * 운동후상태 변환
     * @param isAfterExercise 운동후가 선택되였으면 true 아니면 false
     */
    public void setAfterExercise(boolean isAfterExercise) {
        this.isAfterExercise = isAfterExercise;
        invalidate();
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
