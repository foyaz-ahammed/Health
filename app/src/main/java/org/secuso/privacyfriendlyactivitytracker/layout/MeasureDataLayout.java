package org.secuso.privacyfriendlyactivitytracker.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao.DayTotal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 날자 및 평균값, 상태를 현시하는 layout
 */
public class MeasureDataLayout extends LinearLayout {
    private ImageView mPrev;
    private ImageView mNext;
    private TextView mDate;
    private TextView mAvgValue;
    private TextView mUnit;
    LinearLayout avgArea;
    TextView emptyValue;
    TextView mSelectedDate;
    TextView mAverageText;

    private OnViewClickListener mViewClickListener;

    public MeasureDataLayout(Context context) {
        super(context);
    }

    public MeasureDataLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureDataLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        mPrev = findViewById(R.id.prev);
        mPrev.setOnClickListener(view -> {
            if (mViewClickListener != null) {
                mViewClickListener.onPrevClicked();
            }
        });
        mNext = findViewById(R.id.next);
        mNext.setOnClickListener(view -> {
            if (mViewClickListener != null) {
                mViewClickListener.onNextClicked();
            }
        });
        mDate = findViewById(R.id.date);
        mAvgValue = findViewById(R.id.avg_value);
        avgArea = findViewById(R.id.avg_area);
        emptyValue = findViewById(R.id.empty_value);
        mUnit = findViewById(R.id.unit);
        mSelectedDate = findViewById(R.id.selected_date);
        mAverageText = findViewById(R.id.average_text);
    }

    /**
     * 몸무게값 혹은 체지방률값을 현시하는 함수
     * @param weightValue 현시할 몸무게값
     * @param selectedDate 선택한 날자
     * @param isWeightSelected true 이면 몸무게값 현시, false 이면 체지방률값 현시
     * @param isYear true 이면 년보기, false 이면 년보기가 아님
     */
    public void showWeightValue(String weightValue, String selectedDate, boolean isWeightSelected, boolean isYear) {
        if (weightValue.equals("")) {
            avgArea.setVisibility(View.GONE);
            emptyValue.setVisibility(View.VISIBLE);
            mSelectedDate.setText(getResources().getString(R.string.empty_value));
        } else {
            avgArea.setVisibility(View.VISIBLE);
            emptyValue.setVisibility(View.GONE);
            mSelectedDate.setText(selectedDate);
            mAvgValue.setText(weightValue);
            mAverageText.setText(getResources().getString(isYear ? R.string.monthly_average : R.string.average));
            mUnit.setText(getResources().getString(isWeightSelected ? R.string.kilogram : R.string.percent));
        }
    }

    /**
     * 물량을 현시하는 함수
     * @param waterValue 현시할 물량
     * @param selectedDate 선택한 날자
     */
    public void showWaterValue(String waterValue, String selectedDate) {
        if (waterValue.equals("")) {
            avgArea.setVisibility(View.GONE);
            emptyValue.setVisibility(View.VISIBLE);
            mSelectedDate.setText(getResources().getString(R.string.empty_value));
        } else {
            avgArea.setVisibility(View.VISIBLE);
            mAverageText.setVisibility(View.GONE);
            emptyValue.setVisibility(View.GONE);
            mSelectedDate.setText(selectedDate);
            mAvgValue.setText(waterValue);
            mUnit.setText(getResources().getString(R.string.glasses));
        }
    }

    /**
     * 운동량을 현시하는 함수
     * @param exerciseValue 현시할 운동량
     * @param selectedDate 선택한 날자
     * @param isMeter 운동량단위가 meter 인지 아닌지 판별
     */
    public void showExerciseValue(String exerciseValue, String selectedDate, boolean isMeter) {
        if (exerciseValue.equals("")) {
            avgArea.setVisibility(View.GONE);
            emptyValue.setVisibility(View.VISIBLE);
            mSelectedDate.setText(getResources().getString(R.string.empty_value));
        } else {
            avgArea.setVisibility(View.VISIBLE);
            emptyValue.setVisibility(View.GONE);
            mAverageText.setVisibility(View.GONE);
            mSelectedDate.setText(selectedDate);
            mAvgValue.setText(exerciseValue);
            mUnit.setText(getResources().getString(isMeter ? R.string.meter : R.string.kilometer));
        }
    }

    /**
     * 심박수를 현시하는 함수
     * @param selectedDate 선택한 날자
     * @param max 현시할 심박수 최대값
     * @param min 현시할 심박수 최소값
     * @param isDay 현시할 심박수값이 일별통계화면에 표시할것인지 아닌지 판별
     */
    public void showPulseValue(String selectedDate, int max, int min, boolean isDay) {
        if (max == 0) {
            avgArea.setVisibility(View.GONE);
            emptyValue.setVisibility(View.VISIBLE);
            mSelectedDate.setText(getResources().getString(R.string.empty_value));
        } else {
            avgArea.setVisibility(View.VISIBLE);
            emptyValue.setVisibility(View.GONE);
            mAverageText.setVisibility(View.GONE);
            mSelectedDate.setText(selectedDate);
            if (!isDay) {
                SpannableString str = new SpannableString(min + " - " + max);
                mAvgValue.setText(str);
            } else {
                mAvgValue.setText(String.valueOf(max));
            }
            mUnit.setText(getResources().getString(R.string.beats_per_minute));
        }
    }

    /**
     * 현시할 날자기간을 갱신하는 함수
     * @param start 시작날자
     * @param end 마감날자
     * @param isWeek 현재 보여주는 날자기간이 주단위인지 아닌지 판별
     */
    public void updatePeriod(Calendar start, Calendar end, boolean isWeek) {
        Calendar current = Calendar.getInstance();
        if (isWeek) {
            current.set(Calendar.DAY_OF_WEEK, current.getFirstDayOfWeek());
        } else {
            current.set(Calendar.DAY_OF_MONTH, 1);
        }
        if (start.get(Calendar.YEAR) == current.get(Calendar.YEAR) &&
                start.get(Calendar.MONTH) == current.get(Calendar.MONTH) &&
                start.get(Calendar.DAY_OF_MONTH) == current.get(Calendar.DAY_OF_MONTH)) {
            mNext.setVisibility(View.GONE);
        } else {
            mNext.setVisibility(View.VISIBLE);
        }
        final Locale locale = getContext().getResources().getConfiguration().locale;
        SimpleDateFormat simpleDateMonthFormat = new SimpleDateFormat("MMMM", locale);

        String title = getResources().getString(R.string.date_format8, simpleDateMonthFormat.format(start.getTime()),
                start.get(Calendar.DAY_OF_MONTH), simpleDateMonthFormat.format(end.getTime()), end.get(Calendar.DAY_OF_MONTH));
        mDate.setText(title);
    }

    /**
     * 현시할 년도를 갱신하는 함수
     * @param year 현시할 년도
     */
    public void updateYear(int year) {
        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        Calendar firstMonth = Calendar.getInstance();
        Calendar lastMonth = Calendar.getInstance();
        Calendar current = Calendar.getInstance();
        firstMonth.set(Calendar.YEAR, year);
        firstMonth.set(Calendar.MONTH, 0);
        lastMonth.set(Calendar.YEAR, year);
        lastMonth.set(Calendar.MONTH, 11);

        String title = getResources().getString(R.string.date_format9, firstMonth.get(Calendar.YEAR),
                simpleMonthFormat.format(firstMonth.getTime()), lastMonth.get(Calendar.YEAR), simpleMonthFormat.format(lastMonth.getTime()));
        mDate.setText(title);
        mNext.setVisibility(current.get(Calendar.YEAR) <= year ? View.GONE : View.VISIBLE);
    }

    /**
     * 현시할 날자를 갱신하는 함수
     * @param day 현시할 날자
     */
    public void updateDay(Calendar day) {
        SimpleDateFormat simpleDayFormat = new SimpleDateFormat("EEE, MMMM dd yyyy", Locale.getDefault());
        Calendar current = Calendar.getInstance();
        mDate.setText(simpleDayFormat.format(day.getTime()));
        mNext.setVisibility(Utils.getIntDate(day) >= Utils.getIntDate(current) ? View.GONE : View.VISIBLE);
    }

    /**
     * 전체보기의 기간을 갱신하는 함수
     * @param start 시작년도
     * @param end 마감년도
     */
    public void updateTotal(Calendar start, Calendar end) {
        Calendar current = Calendar.getInstance();
        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        String title = getResources().getString(R.string.date_format9, start.get(Calendar.YEAR),
                simpleMonthFormat.format(start.getTime()), end.get(Calendar.YEAR), simpleMonthFormat.format(end.getTime()));
        mDate.setText(title);
        mNext.setVisibility(end.get(Calendar.YEAR) < current.get(Calendar.YEAR) ? View.VISIBLE : View.GONE);
    }

    public void setOnViewClickListener(final OnViewClickListener onViewClickListener) {
        this.mViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener {
        void onPrevClicked();

        void onNextClicked();
    }
}
