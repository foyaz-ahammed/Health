package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * 개별적인 운동기록의 상세정보를 현시하는 화면
 */
public class WorkoutShowActivity extends ToolbarActivity implements View.OnClickListener{
    TextView mTime;
    TextView mDistance;
    TextView mUnit;
    TextView mDuration;
    TextView mCalorie;
    TextView mAvgPace;
    TextView mAvgSpeed;
    TextView mSwimAvgPace;
    LinearLayout mAverageArea;
    LinearLayout mAveragePaceArea;
    LinearLayout mSwimAveragePaceArea;
    LinearLayout mPerformance;

    List<Integer> activityArray = new ArrayList<>();
    int OUTDOOR_CYCLE = 3;
    int POOL_SWIM = 5;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_workout_show);
        super.onCreate(savedInstanceState);

        initArray();

        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();
        if (bundle != null) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.outdoor_walk);
        }

        mTime = findViewById(R.id.time);
        mDistance = findViewById(R.id.distance);
        mUnit = findViewById(R.id.unit);
        mDuration = findViewById(R.id.duration);
        mCalorie = findViewById(R.id.calorie);
        mAvgPace = findViewById(R.id.average_pace);
        mAvgSpeed = findViewById(R.id.average_speed);
        mSwimAvgPace = findViewById(R.id.swim_average_pace);
        mAverageArea = findViewById(R.id.average_area);
        mAveragePaceArea = findViewById(R.id.average_pace_area);
        mSwimAveragePaceArea = findViewById(R.id.swim_average_pace_area);
        mPerformance = findViewById(R.id.performance);
        mPerformance.setOnClickListener(this);

        if (bundle != null) {
            WorkoutInfo workoutInfo = (WorkoutInfo) bundle.get("workoutInfo");
            getSupportActionBar().setTitle(activityArray.get(workoutInfo.getActivity() - 1));

            Locale locale = getResources().getConfiguration().locale;
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy, hh:mm aaa", locale);
            Date dateTime = new Date(workoutInfo.getStartTime().getMillis());
            mTime.setText(dateFormat.format(dateTime));
            if (workoutInfo.getActivity() != POOL_SWIM) {
                mDistance.setText(String.format("%.2f", workoutInfo.getDistance()));
                mUnit.setText(getResources().getString(R.string.kilometer));
                mSwimAveragePaceArea.setVisibility(View.GONE);
                mAverageArea.setVisibility(View.VISIBLE);
            } else {
                mDistance.setText(String.valueOf((int) workoutInfo.getDistance()));
                mUnit.setText(getResources().getString(R.string.meter));
                mSwimAvgPace.setText(workoutInfo.getAveragePace(this));
                mSwimAveragePaceArea.setVisibility(View.VISIBLE);
                mAverageArea.setVisibility(View.GONE);
            }
            mAveragePaceArea.setVisibility(workoutInfo.getActivity() == OUTDOOR_CYCLE ? View.GONE : View.VISIBLE);
            mDuration.setText(workoutInfo.getDuration());
            mCalorie.setText(String.valueOf(workoutInfo.getCalories()));
            mAvgPace.setText(workoutInfo.getAveragePace(this));
            mAvgSpeed.setText(String.format("%.2f", workoutInfo.getAverageSpeed()));
        }
    }

    /**
     * 운동종목 초기화하는 함수
     */
    private void initArray() {
        activityArray.add(R.string.outdoor_run);
        activityArray.add(R.string.outdoor_walk);
        activityArray.add(R.string.outdoor_cycle);
        activityArray.add(R.string.indoor_run);
        activityArray.add(R.string.pool_swim);
    }

    @Override
    public void onClick(View view) {
        if (view == mPerformance) {
            Intent intent = new Intent(this, PerformanceActivity.class);
            startActivity(intent);
        }
    }
}