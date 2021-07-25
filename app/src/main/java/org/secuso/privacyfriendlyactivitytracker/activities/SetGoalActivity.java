package org.secuso.privacyfriendlyactivitytracker.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaygoo.widget.OnRangeChangedListener;
import com.jaygoo.widget.RangeSeekBar;

import org.secuso.privacyfriendlyactivitytracker.R;

/**
 * 목표설정화면
 */
public class SetGoalActivity extends ToolbarActivity implements View.OnClickListener {
    RangeSeekBar mStepGoalBar;
    TextView mStepGoal;
    RangeSeekBar mWeightGoalBar;
    TextView mWeightGoal;
    RangeSeekBar mWaterGoalBar;
    TextView mWaterGoal;
    TextView mSaveBtn;
    LinearLayout mSetWaterGoalArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_set_goal);
        super.onCreate(savedInstanceState);

        mStepGoalBar = findViewById(R.id.step_goal_bar);
        mStepGoal = findViewById(R.id.step_goal);
        mWeightGoalBar = findViewById(R.id.weight_goal_bar);
        mWeightGoal = findViewById(R.id.weight_goal);
        mWaterGoalBar = findViewById(R.id.water_goal_bar);
        mWaterGoal = findViewById(R.id.water_goal);
        mSaveBtn = findViewById(R.id.save);
        mSaveBtn.setOnClickListener(this);
        mSetWaterGoalArea = findViewById(R.id.set_water_goal_area);

        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(this);
        int stepGoal = Integer.parseInt(sharePref.getString(getString(R.string.pref_daily_step_goal), "10000"));
        float weightGoal = sharePref.getFloat(getString(R.string.pref_weight_goal), 0);
        int waterGoal = sharePref.getInt(getString(R.string.pref_daily_water_goal), 0);
        mStepGoal.setText(String.valueOf(stepGoal));
        mStepGoalBar.setProgress((float) stepGoal / 1000);
        mWeightGoal.setText(String.valueOf(weightGoal == 0 ? 60 : (int) weightGoal));
        mWeightGoalBar.setProgress(weightGoal == 0 ? 60 : weightGoal);
        if (waterGoal != 0) {
            mSetWaterGoalArea.setVisibility(View.VISIBLE);
            mWaterGoal.setText(String.valueOf(waterGoal * 250));
            mWaterGoalBar.setProgress((float) waterGoal);
        } else {
            mSetWaterGoalArea.setVisibility(View.GONE);
        }

        mStepGoalBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mStepGoal.setText(String.valueOf(((int) leftValue) * 1000));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
        mWeightGoalBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mWeightGoal.setText(String.valueOf((int) leftValue));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
        mWaterGoalBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                mWaterGoal.setText(String.valueOf(((int) leftValue) * 250));
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.pref_daily_step_goal), mStepGoal.getText().toString());
            editor.putFloat(getString(R.string.pref_weight_goal), Float.parseFloat(mWeightGoal.getText().toString()));
            if (mSetWaterGoalArea.getVisibility() == View.VISIBLE) {
                editor.putInt(getString(R.string.pref_daily_water_goal), Integer.parseInt(mWaterGoal.getText().toString()) / 250);
            }
            editor.apply();

            finish();
        }
    }
}