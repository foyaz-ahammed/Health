package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;

import java.util.Calendar;

/**
 * 건강화면 생리현시 view
 */
public class MainCycleView extends ViewGroup {
    Paint backgroundPaint = new Paint();
    Paint periodPaint = new Paint();
    Paint fertilePaint = new Paint();
    Paint fertileOnePaint = new Paint();
    Paint dayIndicatorPaint = new Paint();
    Paint todayTextPaint = new Paint();
    Point center;
    RectF circleBounds;

    float radius = 0;
    int cycleLength = 28;
    Ovulation cycleData;

    public MainCycleView(Context context) {
        this(context, null);
    }

    public MainCycleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainCycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        backgroundPaint.setColor(getResources().getColor(R.color.cycle_circle_calendar_background_color));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(Utils.dip2px(15));
        backgroundPaint.setAntiAlias(true);

        periodPaint.setColor(getResources().getColor(R.color.main_cycle_period_color));
        periodPaint.setStyle(Paint.Style.STROKE);
        periodPaint.setStrokeWidth(Utils.dip2px(15));
        periodPaint.setStrokeCap(Paint.Cap.ROUND);
        periodPaint.setAntiAlias(true);

        fertileOnePaint.setColor(getResources().getColor(R.color.main_cycle_fertile_color));

        fertilePaint.setColor(getResources().getColor(R.color.main_cycle_fertile_color));
        fertilePaint.setStyle(Paint.Style.STROKE);
        fertilePaint.setStrokeWidth(Utils.dip2px(15));
        fertilePaint.setStrokeCap(Paint.Cap.ROUND);
        fertilePaint.setAntiAlias(true);

        dayIndicatorPaint.setColor(Color.WHITE);

        todayTextPaint.setAntiAlias(true);
        todayTextPaint.setTextAlign(Paint.Align.CENTER);
        todayTextPaint.setColor(Color.BLACK);
        todayTextPaint.setFakeBoldText(true);
        todayTextPaint.setTextSize(Utils.dip2px(7));

        circleBounds = new RectF();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        canvas.drawArc(circleBounds, 0, 360, false, backgroundPaint);

        if (cycleData != null) {
            float divisionAngle = (float) (360.0 / (float) cycleLength);
            Calendar periodStart = Utils.convertIntDateToCalendar(cycleData.getPeriodStart());

            int periodLength = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getPeriodStart()), Utils.convertIntDateToCalendar(cycleData.getPeriodEnd())) + 1;
            canvas.drawArc(circleBounds, -90, divisionAngle * (periodLength - 1), false, periodPaint);

            int fertileLength = 0, daysToFertileStart = 0;
            if (cycleData.getFertileStart() > 0) {
                fertileLength = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getFertileStart()),
                        Utils.convertIntDateToCalendar(cycleData.getFertileEnd())) + 1;
                daysToFertileStart = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getPeriodStart()),
                        Utils.convertIntDateToCalendar(cycleData.getFertileStart()));
                //가임기 상태 그려주기
                if (fertileLength == 1) {
                    float angle = daysToFertileStart * divisionAngle - 90;
                    double radians = Math.toRadians((double) angle);
                    float cx = (float) (center.x + radius * Math.cos(radians));
                    float cy = (float) (center.y + radius * Math.sin(radians));
                    canvas.drawCircle(cx, cy, backgroundPaint.getStrokeWidth() / 2, fertileOnePaint);
                } else {
                    canvas.drawArc(circleBounds, daysToFertileStart * divisionAngle - 90, divisionAngle * (fertileLength - 1), false, fertilePaint);
                }
            }

            int todayDiffDays = Utils.getDiffDays(periodStart, Calendar.getInstance());
            double todayXRadius = radius * Math.cos(Math.toRadians(divisionAngle * todayDiffDays - 90));
            double todayYRadius = radius * Math.sin(Math.toRadians(divisionAngle * todayDiffDays - 90));
            //현재날자 그려주기
            canvas.drawCircle((float) (center.x + todayXRadius),
                    (float) (center.y + todayYRadius), periodPaint.getStrokeWidth() / 2, dayIndicatorPaint);
            canvas.drawText(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), (float) (center.x + todayXRadius),
                    (float) (center.y + todayYRadius + todayTextPaint.getTextSize() / 2), todayTextPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);

        int smallestSide = Math.min(measureWidth, measureHeight);
        setMeasuredDimension(smallestSide, smallestSide);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        calculateBounds(w, h);
        requestLayout();
    }

    //경계계산함수
    private void calculateBounds(int w, int h) {
        radius = (float) w / 2 - Utils.dip2px(20);
        center = new Point(w / 2, h / 2);

        circleBounds.left = center.x - radius;
        circleBounds.top = center.y - radius;
        circleBounds.right = center.x + radius;
        circleBounds.bottom = center.y + radius;
    }

    //자료갱신함수
    public void updateData(Ovulation cycleData) {
        this.cycleData = cycleData;
        invalidate();
    }

    //생리길이갱신함수
    public void updateCycleLength(int cycleLength) {
        this.cycleLength = cycleLength;
        invalidate();
    }
}
