package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 생리주기달력
 */
public class CircleCalendarView extends ViewGroup {
    Paint backgroundPaint = new Paint();
    Paint dayIndicatorPaint = new Paint();
    Paint periodPaint = new Paint();
    Paint fertilePaint = new Paint();
    Paint fertileOnePaint = new Paint();
    Paint periodIndicatorPaint = new Paint();
    Paint fertileIndicatorPaint = new Paint();
    Paint firstDayTextPaint = new Paint();
    Paint currentDayTextPaint = new Paint();
    Paint selectDayTextPaint = new Paint();
    Point center;
    RectF circleBounds;

    float radius = 0; //중심에서부터 원까지의 거리
    int cycleLength = 28, daysToSelect = 0; // 생리기간, 선택한 날자
    double indicatorAngle = 0; // 0 위치에서부터 선택한 위치까지의 각도
    Ovulation cycleData; // 현시할 생리자료

    private OnDateSelectedListener mOnDateSelectedListener;

    public CircleCalendarView(Context context) {
        this(context, null);
    }

    public CircleCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    /**
     * 초기화
     */
    private void init() {
        backgroundPaint.setColor(getResources().getColor(R.color.cycle_circle_calendar_background_color));
        backgroundPaint.setStyle(Paint.Style.STROKE);
        backgroundPaint.setStrokeWidth(Utils.dip2px(50));
        backgroundPaint.setAntiAlias(true);

        periodPaint.setColor(getResources().getColor(R.color.cycle_calendar_period_background_color));
        periodPaint.setStyle(Paint.Style.STROKE);
        periodPaint.setStrokeWidth(Utils.dip2px(50));
        periodPaint.setStrokeCap(Paint.Cap.ROUND);
        periodPaint.setAntiAlias(true);

        fertileOnePaint.setColor(getResources().getColor(R.color.cycle_calendar_fertile_background_color));

        fertilePaint.setColor(getResources().getColor(R.color.cycle_calendar_fertile_background_color));
        fertilePaint.setStyle(Paint.Style.STROKE);
        fertilePaint.setStrokeWidth(Utils.dip2px(50));
        fertilePaint.setStrokeCap(Paint.Cap.ROUND);
        fertilePaint.setAntiAlias(true);

        dayIndicatorPaint.setColor(Color.WHITE);
        periodIndicatorPaint.setColor(getResources().getColor(R.color.period_indicator_color));
        fertileIndicatorPaint.setColor(getResources().getColor(R.color.fertile_indicator_color));

        firstDayTextPaint.setAntiAlias(true);
        firstDayTextPaint.setTextAlign(Paint.Align.CENTER);
        firstDayTextPaint.setColor(getResources().getColor(R.color.period_indicator_color));
        firstDayTextPaint.setFakeBoldText(true);
        firstDayTextPaint.setTextSize(Utils.dip2px(10));

        currentDayTextPaint.setAntiAlias(true);
        currentDayTextPaint.setTextAlign(Paint.Align.CENTER);
        currentDayTextPaint.setColor(Color.BLACK);
        currentDayTextPaint.setFakeBoldText(true);
        currentDayTextPaint.setTextSize(Utils.dip2px(13));

        selectDayTextPaint.setAntiAlias(true);
        selectDayTextPaint.setTextAlign(Paint.Align.CENTER);
        selectDayTextPaint.setColor(Color.BLACK);
        selectDayTextPaint.setFakeBoldText(true);
        selectDayTextPaint.setTextSize(Utils.dip2px(13));

        circleBounds = new RectF();
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

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //배경원 그리기
        canvas.drawArc(circleBounds, 0, 360, false, backgroundPaint);

        float divisionAngle = (float) (360.0 / (float) cycleLength);
        //일별 점 그리기
        for (int i = 0; i < cycleLength; i ++) {
            float angle = divisionAngle * i - 90;
            double radians = Math.toRadians((double) angle);
            float cx = (float) (center.x + radius * Math.cos(radians));
            float cy = (float) (center.y + radius * Math.sin(radians));
            canvas.drawCircle(cx, cy, Utils.dip2px(2), dayIndicatorPaint);
        }

        if (cycleData != null) {
            int periodLength = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getPeriodStart()), Utils.convertIntDateToCalendar(cycleData.getPeriodEnd())) + 1;
            //생리기간상태 그리기
            canvas.drawArc(circleBounds, -90, divisionAngle * (periodLength - 1), false, periodPaint);
            for (int i = 0; i < periodLength; i++) {
                float angle = divisionAngle * i - 90;
                double radians = Math.toRadians((double) angle);
                float cx = (float) (center.x + radius * Math.cos(radians));
                float cy = (float) (center.y + radius * Math.sin(radians));
                canvas.drawCircle(cx, cy, Utils.dip2px(2), periodIndicatorPaint);
            }
            Calendar periodStart = Utils.convertIntDateToCalendar(cycleData.getPeriodStart());

            int fertileLength = 0, daysToFertileStart = 0;
            if (cycleData.getFertileStart() > 0) {
                fertileLength = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getFertileStart()),
                        Utils.convertIntDateToCalendar(cycleData.getFertileEnd())) + 1;
                daysToFertileStart = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getPeriodStart()),
                        Utils.convertIntDateToCalendar(cycleData.getFertileStart()));
                //가임기 상태 그리기
                //가임기 날자수가 하루이면 원을 그려주고 그렇지 않으면 arc를 그려준다
                if (fertileLength == 1) {
                    float angle = daysToFertileStart * divisionAngle - 90;
                    double radians = Math.toRadians((double) angle);
                    float cx = (float) (center.x + radius * Math.cos(radians));
                    float cy = (float) (center.y + radius * Math.sin(radians));
                    canvas.drawCircle(cx, cy, backgroundPaint.getStrokeWidth() / 2, fertileOnePaint);
                } else {
                    canvas.drawArc(circleBounds, daysToFertileStart * divisionAngle - 90, divisionAngle * (fertileLength - 1), false, fertilePaint);
                }
                for (int i = 0; i < fertileLength; i ++) {
                    float angle = daysToFertileStart * divisionAngle + divisionAngle * i - 90;
                    double radians = Math.toRadians((double) angle);
                    float cx = (float) (center.x + radius * Math.cos(radians));
                    float cy = (float) (center.y + radius * Math.sin(radians));
                    canvas.drawCircle(cx, cy, Utils.dip2px(2), fertileIndicatorPaint);
                }
            }

            int todayDiffDays = Utils.getDiffDays(periodStart, Calendar.getInstance());
            double todayXRadius = radius * Math.cos(Math.toRadians(divisionAngle * todayDiffDays - 90));
            double todayYRadius = radius * Math.sin(Math.toRadians(divisionAngle * todayDiffDays - 90));
            //선택된 날자 그리기
            canvas.drawCircle((float) (center.x + todayXRadius),
                    (float) (center.y + todayYRadius), Utils.dip2px(13), dayIndicatorPaint);
            canvas.drawText(String.valueOf(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)), (float) (center.x + todayXRadius),
                    (float) (center.y + todayYRadius + firstDayTextPaint.getTextSize() / 2), currentDayTextPaint);

            //생리기간 시작날자 그리기
            canvas.drawText(getResources().getString(R.string.date_format4, periodStart.get(Calendar.MONTH) + 1, periodStart.get(Calendar.DAY_OF_MONTH)),
                    (float) (center.x + radius * Math.cos(Math.toRadians(-90))), (float) (center.y + radius * Math.sin(Math.toRadians(-90)) + Utils.dip2px(15)), firstDayTextPaint);

            double selectXRadius = radius * Math.cos(Math.toRadians(divisionAngle * daysToSelect - 90));
            double selectYRadius = radius * Math.sin(Math.toRadians(divisionAngle * daysToSelect - 90));
            //선택된 날자의 원 그리기
            canvas.drawCircle((float) (center.x + selectXRadius), (float) (center.y + selectYRadius),
                    backgroundPaint.getStrokeWidth() / 2, dayIndicatorPaint);

            selectDayTextPaint.setColor(Color.BLACK);
            if (daysToSelect < periodLength)
                selectDayTextPaint.setColor(periodIndicatorPaint.getColor());
            if (cycleData.getFertileStart() > 0) {
                if (daysToSelect >= daysToFertileStart && daysToSelect < daysToFertileStart + fertileLength)
                    selectDayTextPaint.setColor(fertileIndicatorPaint.getColor());
            }
            Calendar selectedCalendar = (Calendar) periodStart.clone();
            selectedCalendar.add(Calendar.DAY_OF_MONTH, daysToSelect);
            SimpleDateFormat weekDayFormat = new SimpleDateFormat("EE", Locale.getDefault());
            //선택된 날자와 요일 그리기
            canvas.drawText(weekDayFormat.format(new Date(selectedCalendar.getTimeInMillis())), (float) (center.x + selectXRadius),
                    (float) (center.y + selectYRadius), selectDayTextPaint);
            canvas.drawText(String.valueOf(selectedCalendar.get(Calendar.DAY_OF_MONTH)), (float) (center.x + selectXRadius),
                    (float) (center.y + selectYRadius + selectDayTextPaint.getTextSize()), selectDayTextPaint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (cycleData != null) {
                    //선택된 지점이 배경원안에 있는지 검사
                    double distanceFromCenter = Math.sqrt(Math.pow(Math.abs(ev.getX() - center.x), 2.0f) + Math.pow(Math.abs(ev.getY() - center.y), 2.0f));
                    boolean isInCircle = distanceFromCenter <= radius + backgroundPaint.getStrokeWidth() / 2 && distanceFromCenter >= radius - backgroundPaint.getStrokeWidth() / 2;

                    if (isInCircle) {
                        //선택된 지점의 각도 얻기
                        double touchAngleRad = Math.atan2(center.y - ev.getY(), center.x - ev.getX());
                        double angle = Math.toDegrees(touchAngleRad) - 90;
                        if (angle < 0) {
                            angle += 360;
                        }
                        indicatorAngle = angle;
                        if (mOnDateSelectedListener != null) {
                            float divisionAngle = (float) (360.0 / (float) cycleLength);
                            //선택된 지점과 시작사이의 점개수 얻기
                            daysToSelect = (int) (indicatorAngle / divisionAngle);
                            if ((indicatorAngle - daysToSelect * divisionAngle) > (divisionAngle / 2)) {
                                daysToSelect += 1;
                            }
                            if (daysToSelect == cycleLength)
                                daysToSelect = 0;
                            Calendar selectedCalendar = Utils.convertIntDateToCalendar(cycleData.getPeriodStart());
                            selectedCalendar.add(Calendar.DAY_OF_MONTH, daysToSelect);
                            mOnDateSelectedListener.onDateSelected(selectedCalendar);
                        }
                        invalidate();
                    }
                }
                return true;

        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mOnDateSelectedListener = null;
    }

    //경계계산하는 함수
    private void calculateBounds(int w, int h) {
        radius = (float) w / 2 - Utils.dip2px(50);
        center = new Point(w / 2, h / 2);

        circleBounds.left = center.x - radius;
        circleBounds.top = center.y - radius;
        circleBounds.right = center.x + radius;
        circleBounds.bottom = center.y + radius;
    }

    /**
     * 생리주기길이 설정하는 함수
     * @param length 생리주기길이
     */
    public void setCycleLength(int length) {
        this.cycleLength = length;
        invalidate();
    }

    /**
     * 생리자료설정하는 함수
     * @param cycleData 샹라젃
     * @param selectedCalendar 선택된 날자
     */
    public void setData(Ovulation cycleData, Calendar selectedCalendar) {
        this.cycleData = cycleData;
        if (cycleData != null) {
            daysToSelect = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getPeriodStart()), selectedCalendar);
        }
        invalidate();
    }

    /**
     * 현재날자로 이행하는 함수
     */
    public void selectCurrentDay() {
        daysToSelect = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.getPeriodStart()), Calendar.getInstance());
        invalidate();
    }

    public void setOnDateSelectedListener(OnDateSelectedListener listener) {
        mOnDateSelectedListener = listener;
    }

    public interface OnDateSelectedListener {
        void onDateSelected(Calendar selectedDate);
    }
}
