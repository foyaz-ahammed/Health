package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 생리기록 주달력
 */
public class CustomWeekView extends WeekView {


    private int mRadius;
    private float mRangeCircleRadius;
    private float mSmallCircleDotRadius;
    Paint mPointPaint = new Paint();
    float mPointRadius;
    private final int mPadding;
    Paint mSelectPaint = new Paint();
    Paint mPeriodPaint = new Paint();
    Paint mFertilePaint = new Paint();
    Paint mPredictPaint = new Paint();
    Paint mPredictWhitePaint = new Paint();
    Paint mBoundaryPaint = new Paint();
    Paint mThicknessPaint = new Paint();
    Paint mPeriodTextPaint = new Paint();
    Paint mFertileTextPaint = new Paint();

    public CustomWeekView(Context context) {
        super(context);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(Color.RED);

        mSelectPaint.setStyle(Paint.Style.STROKE);
        mSelectPaint.setStrokeWidth(getResources().getDimension(R.dimen.width_circle_stroke));
        mSelectPaint.setColor(getResources().getColor(R.color.cycle_calendar_select_color));

        mPeriodPaint.setColor(getResources().getColor(R.color.cycle_calendar_period_background_color));
        mFertilePaint.setColor(getResources().getColor(R.color.cycle_calendar_fertile_background_color));
        mPredictWhitePaint.setColor(getResources().getColor(R.color.main_background_color));

        mPredictPaint.setColor(getResources().getColor(R.color.cycle_calendar_predict_dot_color));
        mPredictPaint.setStyle(Paint.Style.STROKE);
        mPredictPaint.setStrokeWidth(getResources().getDimension(R.dimen.width_circle_stroke));
        DashPathEffect dashPathEffect = new DashPathEffect(new float[]{5, 5}, (float)1.0);
        mPredictPaint.setPathEffect(dashPathEffect);

        mBoundaryPaint.setColor(0xffe8e0e0);
        mBoundaryPaint.setStyle(Paint.Style.STROKE);
        mBoundaryPaint.setStrokeWidth(getResources().getDimension(R.dimen.width_circle_stroke));

        mThicknessPaint.setColor(0xffffffff);
        mThicknessPaint.setStyle(Paint.Style.STROKE);
        mThicknessPaint.setStrokeWidth(getResources().getDimension(R.dimen.width_circle_stroke_thickness));

        mPeriodTextPaint.setAntiAlias(true);
        mPeriodTextPaint.setTextAlign(Paint.Align.CENTER);
        mPeriodTextPaint.setColor(0xfffb3058);
        mPeriodTextPaint.setFakeBoldText(true);
        mPeriodTextPaint.setTextSize(dipToPx(getContext(), 16));

        mFertileTextPaint.setAntiAlias(true);
        mFertileTextPaint.setTextAlign(Paint.Align.CENTER);
        mFertileTextPaint.setColor(0xff0d7efa);
        mFertileTextPaint.setFakeBoldText(true);
        mFertileTextPaint.setTextSize(dipToPx(getContext(), 16));

        mPadding = dipToPx(getContext(), 3);
        mPointRadius = dipToPx(context, 2);
    }


    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mRangeCircleRadius = mRadius + getResources().getDimension(R.dimen.small_dot_circle_diff) / 2;
        mSmallCircleDotRadius = mRadius - getResources().getDimension(R.dimen.small_dot_circle_diff) / 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectPaint);
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight - 5 * mPadding;
        canvas.drawCircle(cx, cy, mPointRadius, mPointPaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine;
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        int schemeX = x + mItemWidth / 2;
        int schemeY = mItemHeight - 5 * mPadding;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        //실지생리령역의 canvas 그리기
        for (int i = 0; i < mDelegate.periodRangeList.size(); i ++) {
            if (mDelegate.periodRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                List<String> keys = new ArrayList<String>(mDelegate.periodRangeList.get(i).keySet());
                if (keys.indexOf(dateFormat.format(new Date(calendar.getTimeInMillis()))) == 0) {
                    canvas.drawCircle(cx, cy, mRadius, mPeriodPaint);
                    canvas.drawRect(new RectF((float) cx, (float) mItemHeight / 3, (float) x + mItemWidth, (float) mItemHeight / 3 * 2), mPeriodPaint);
                } else if (keys.indexOf(dateFormat.format(new Date(calendar.getTimeInMillis()))) == mDelegate.periodRangeList.get(i).size() - 1) {
                    canvas.drawCircle(cx, cy, mRadius, mPeriodPaint);
                    canvas.drawRect(new RectF((float) x, (float) mItemHeight / 3, (float) cx, (float) mItemHeight / 3 * 2), mPeriodPaint);
                } else {
                    canvas.drawCircle(cx, cy, mRadius, mPeriodPaint);
                    canvas.drawRect(new RectF((float) x, (float) mItemHeight / 3, (float) x + mItemWidth , (float) mItemHeight / 3 * 2), mPeriodPaint);
                }
            }
        }

        //가임기령역의 canvas 그리기
        for (int i = 0; i < mDelegate.fertileRangeList.size(); i ++) {
            if (mDelegate.fertileRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                if (mDelegate.fertileRangeList.get(i).size() == 1) {
                    canvas.drawCircle(cx, cy, mRadius, mFertilePaint);
                } else {
                    List<String> keys = new ArrayList<String>(mDelegate.fertileRangeList.get(i).keySet());
                    if (keys.indexOf(dateFormat.format(new Date(calendar.getTimeInMillis()))) == 0) {
                        canvas.drawCircle(cx, cy, mRadius, mFertilePaint);
                        canvas.drawRect(new RectF((float) cx, (float) mItemHeight / 3, (float) x + mItemWidth, (float) mItemHeight / 3 * 2), mFertilePaint);
                    } else if (keys.indexOf(dateFormat.format(new Date(calendar.getTimeInMillis()))) == mDelegate.fertileRangeList.get(i).size() - 1) {
                        canvas.drawCircle(cx, cy, mRadius, mFertilePaint);
                        canvas.drawRect(new RectF((float) x, (float) mItemHeight / 3, (float) cx, (float) mItemHeight / 3 * 2), mFertilePaint);
                    } else {
                        canvas.drawCircle(cx, cy, mRadius, mFertilePaint);
                        canvas.drawRect(new RectF((float) x, (float) mItemHeight / 3, (float) x + mItemWidth, (float) mItemHeight / 3 * 2), mFertilePaint);
                    }
                }
            }
        }

        //예상기간령역의 canvas 그리기
        for (int i = 0; i < mDelegate.predictRangeList.size(); i ++) {
            if (mDelegate.predictRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                List<String> keys = new ArrayList<String>(mDelegate.predictRangeList.get(i).keySet());
                if (keys.indexOf(dateFormat.format(new Date(calendar.getTimeInMillis()))) == 0) {
                    canvas.drawCircle(cx, cy, mRadius, mPredictPaint);
                    canvas.drawRect(new RectF((float) cx, (float) mItemHeight / 3, (float) x + mItemWidth, (float) mItemHeight / 3 * 2), mPredictWhitePaint);
                    canvas.drawLine((float) (mItemWidth / 6 * 5 + x), (float) (mItemHeight / 3), (float) (mItemWidth + x), (float) (mItemHeight / 3), mPredictPaint);
                    canvas.drawLine((float) (mItemWidth / 6 * 5 + x), (float) (mItemHeight / 3 * 2), (float) (mItemWidth + x), (float) (mItemHeight / 3 * 2), mPredictPaint);
                } else if (keys.indexOf(dateFormat.format(new Date(calendar.getTimeInMillis()))) == mDelegate.predictRangeList.get(i).size() - 1) {
                    canvas.drawCircle(cx, cy, mRadius, mPredictPaint);
                    canvas.drawRect(new RectF((float) x, (float) mItemHeight / 3, (float) cx, (float) mItemHeight / 3 * 2), mPredictWhitePaint);
                    canvas.drawLine((float) x, (float) mItemHeight / 3, (float) mItemWidth / 6 + x, (float) mItemHeight / 3, mPredictPaint);
                    canvas.drawLine((float) x, (float) mItemHeight / 3 * 2, (float) mItemWidth / 6 + x, (float) mItemHeight / 3 * 2, mPredictPaint);
                } else {
                    canvas.drawCircle(cx, cy, mRadius, mPredictPaint);
                    canvas.drawRect(new RectF((float) x, (float) mItemHeight / 3, (float) x + mItemWidth , (float) mItemHeight / 3 * 2), mPredictWhitePaint);
                    canvas.drawLine((float) mItemWidth / 6 * 5 + x, (float) mItemHeight / 3, (float) mItemWidth + x, (float) mItemHeight / 3, mPredictPaint);
                    canvas.drawLine((float) mItemWidth / 6 * 5 + x, (float) mItemHeight / 3 * 2, (float) mItemWidth + x, (float) mItemHeight / 3 * 2, mPredictPaint);
                    canvas.drawLine((float) x, (float) mItemHeight / 3, (float) mItemWidth / 6 + x, (float) mItemHeight / 3, mPredictPaint);
                    canvas.drawLine((float) x, (float) mItemHeight / 3 * 2, (float) mItemWidth / 6 + x, (float) mItemHeight / 3 * 2, mPredictPaint);
                }
            }
        }

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);

            //선택한 날자가 생리기간에 있는 경우의 canvas 그리기
            for (int i = 0; i < mDelegate.periodRangeList.size(); i ++) {
                if (mDelegate.periodRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                    canvas.drawCircle(cx, cy, mRadius, mBoundaryPaint);
                    canvas.drawCircle(cx, cy, mRadius, mThicknessPaint);
                    canvas.drawCircle(cx, cy, mRangeCircleRadius, mBoundaryPaint);
                }
            }
            //선택한 날자가 가임기간에 있는 경우의 canvas 그리기
            for (int i = 0; i < mDelegate.fertileRangeList.size(); i ++) {
                if (mDelegate.fertileRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                    canvas.drawCircle(cx, cy, mRadius, mBoundaryPaint);
                    canvas.drawCircle(cx, cy, mRadius, mThicknessPaint);
                    canvas.drawCircle(cx, cy, mRangeCircleRadius, mBoundaryPaint);
                }
            }
            //선택한 날자가 예상기간에 있는 경우의 canvas 그리기
            for (int i = 0; i < mDelegate.predictRangeList.size(); i ++) {
                if (mDelegate.predictRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                    canvas.drawCircle(cx, cy, mRadius, mBoundaryPaint);
                    canvas.drawCircle(cx, cy, mRadius, mThicknessPaint);
                    canvas.drawCircle(cx, cy, mRangeCircleRadius, mBoundaryPaint);
                    canvas.drawCircle(cx, cy, mSmallCircleDotRadius, mPredictPaint);
                }
            }
        }
        if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);
            canvas.drawCircle(schemeX, schemeY, mPointRadius, mPointPaint);

        } else {
            canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }

        //실지 생리자료의 령역의 text 그리기
        for (int i = 0; i < mDelegate.periodRangeList.size(); i ++) {
            if (mDelegate.periodRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, mPeriodTextPaint);
            }
        }
        //가임기령역의 text 그리기
        for (int i = 0; i < mDelegate.fertileRangeList.size(); i ++) {
            if (mDelegate.fertileRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, mFertileTextPaint);
            }
        }
        //예상자료령역의 text 그리기
        for (int i = 0; i < mDelegate.predictRangeList.size(); i ++) {
            if (mDelegate.predictRangeList.get(i).containsKey(dateFormat.format(new Date(calendar.getTimeInMillis())))) {
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY, mPeriodTextPaint);
            }
        }
    }

    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
