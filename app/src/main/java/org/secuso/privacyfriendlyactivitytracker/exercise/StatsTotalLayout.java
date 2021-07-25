package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao.DayTotal;

import java.util.List;

/**
 * 기간별 총자료를 현시하는 layout
 */
public class StatsTotalLayout extends LinearLayout {
    TextView mTotalDistance;
    TextView mTotalDistanceUnit;
    TextView mTotalTimes;
    TextView mTotalDuration;
    TextView mTotalCalories;
    TextView mAveragePace;
    TextView mPaceUnit;

    public StatsTotalLayout(Context context) {
        super(context);
    }

    public StatsTotalLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StatsTotalLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTotalDistance = findViewById(R.id.total_distance);
        mTotalDistanceUnit = findViewById(R.id.total_distance_unit);
        mTotalTimes = findViewById(R.id.total_times);
        mTotalDuration = findViewById(R.id.total_duration);
        mTotalCalories = findViewById(R.id.total_calories);
        mAveragePace = findViewById(R.id.average_pace);
        mPaceUnit = findViewById(R.id.pace_unit);
    }

    /**
     * 기간별 총자료를 현시하는 함수
     * @param data 현시할 자료
     */
    @SuppressLint("DefaultLocale")
    public void setValue(List<DayTotal> data) {
        if (data.size() > 0) {
            setVisibility(View.VISIBLE);

            float totalDistance = 0;
            long totalDuration = 0;
            int times = 0;
            int totalCalories = 0;
            float pace;
            int paceMinute, paceSecond;
            int activity = data.get(0).getActivity();

            for (int i = 0; i < data.size(); i ++) {
                totalDistance += data.get(i).getTotalDistance();
                totalDuration += data.get(i).getTotalDuration();
                times += data.get(i).getCount();
            }

            switch (activity) {
                case 1:
                case 4:
                    totalCalories = (int) (totalDistance * 60);
                    break;
                case 2:
                    totalCalories = (int) (totalDistance * 30);
                    break;
                case 3:
                    totalCalories = (int) (totalDistance) * 19;
                    break;
                case 5:
                    totalCalories = (int) (totalDistance / 500 * 78);
                    break;
            }

            if (activity == 5)
                pace = totalDuration / (totalDistance * 10);
            else
                pace = totalDuration / (totalDistance * 1000);
            paceMinute = (int) (pace / 60);
            paceSecond = (int) (pace - paceMinute * 60);

            mTotalDistance.setText(activity == 5 ? String.valueOf((int) totalDistance) : String.format("%.2f", totalDistance));
            mTotalDistanceUnit.setText(activity == 5 ? getResources().getString(R.string.meter) :
                    getResources().getString(R.string.kilometer));
            mTotalDuration.setText(String.valueOf((int) (totalDuration / 60000)));
            mTotalTimes.setText(String.valueOf(times));
            mTotalCalories.setText(String.valueOf(totalCalories));
            mAveragePace.setText(getResources().getString(R.string.average_pace_format, paceMinute, paceSecond));
            mPaceUnit.setText(data.get(0).getActivity() != 5 ? getResources().getString(R.string.kilometer_with_slash) :
                    getResources().getString(R.string.swim_average_pace_unit));
        } else {
            setVisibility(View.GONE);
        }
    }
}
