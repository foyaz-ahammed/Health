package org.secuso.privacyfriendlyactivitytracker.weight;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.weight.WeightGoalActivity;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;

/**
 * 일별 몸무게의 구체적인 자료를 보여주는 layout
 */
public class DailyMeasureLayout extends LinearLayout implements View.OnClickListener {
    private ImageView mPrev;
    private ImageView mNext;
    private TextView mDate;
    private TextView mTime;
    private TextView mWeightView;
    private TextView mFatRateView;
    private LinearLayout mFatRateArea;
    private TextView mBmi;
    private TextView mLevel;
    private TextView mGoalDesc;
    private TextView mSetWeightGoal;
    private RoundedHorizontalProgressBar mGoalProgress;
    private TextView mStartAt;
    private TextView mGoal;
    private LinearLayout mProgressArea;
    private LinearLayout mGoalDescArea;

    WeightInfo mWeightInfo;
    int currentIndex, maxIndex;
    String startWeight, goalWeight;

    int WEIGHT_GOAL_SET = 0;
    int WEIGHT_GOAL_ADJUST = 1;
    int WEIGHT_GOAL_NEW_SET = 2;
    int currentGoalStatus = WEIGHT_GOAL_SET;

    private OnViewClickListener mViewClickListener;

    public DailyMeasureLayout(Context context) {
        super(context);
    }

    public DailyMeasureLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DailyMeasureLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

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
        mTime = findViewById(R.id.time);
        mWeightView = findViewById(R.id.weight_value);
        mFatRateView = findViewById(R.id.fat_rate_value);
        mFatRateArea = findViewById(R.id.fat_rate_area);
        mBmi = findViewById(R.id.bmi);
        mLevel = findViewById(R.id.level);
        mGoalDesc = findViewById(R.id.goal_desc);
        mSetWeightGoal = findViewById(R.id.set_weight_goal);
        mSetWeightGoal.setOnClickListener(this);
        mGoalProgress = findViewById(R.id.weight_progress);
        mStartAt = findViewById(R.id.start_at);
        mGoal = findViewById(R.id.goal);
        mProgressArea = findViewById(R.id.progress_area);
        mGoalDescArea = findViewById(R.id.goal_desc_area);

        mPrev.setVisibility(currentIndex + 1 > maxIndex ? GONE : VISIBLE);
        mNext.setVisibility(currentIndex - 1 < 0 ? GONE : VISIBLE);

        if (mWeightInfo != null) {
            mWeightView.setText(String.valueOf(mWeightInfo.getWeightValue()));
            if (mWeightInfo.getFatRateValue() != null) {
                mFatRateView.setText(String.valueOf(mWeightInfo.getFatRateValue()));
                mFatRateArea.setVisibility(View.VISIBLE);
            } else {
                mFatRateArea.setVisibility(View.GONE);
            }
            mDate.setText(getResources().getString(R.string.date_format2, mWeightInfo.getMeasureDateTime().getYear(), mWeightInfo.getMeasureDateTime().getMonthOfYear(), mWeightInfo.getMeasureDateTime().getDayOfMonth()));
            mTime.setText(Utils.getTimeString(getContext(), mWeightInfo.getMeasureDateTime().getHourOfDay(), mWeightInfo.getMeasureDateTime().getMinuteOfHour()));
            mBmi.setText("BMI " + String.format("%.1f", mWeightInfo.getBMI(getContext())));
            mLevel.setText(mWeightInfo.getLevel(getContext()));

            if (currentIndex == 0) {
                mProgressArea.setVisibility(View.VISIBLE);
                float current = Float.parseFloat(mWeightInfo.getWeightValue());
                float start, goal;
                if (startWeight != null)
                    start = Float.parseFloat(startWeight);
                else start = 0;
                if (goalWeight != null)
                    goal = Float.parseFloat(goalWeight);
                else goal = 0;

                if (goalWeight != null) {
                    mStartAt.setText(getResources().getString(R.string.start_at, startWeight));
                    mGoal.setText(getResources().getString(R.string.goal, goalWeight));

                    int closerPercent = (int) ((current - start) / (goal - start) * 100);
                    if (start < goal) {
                        if (current <= start) {
                            currentGoalStatus = WEIGHT_GOAL_ADJUST;
                            mGoalDesc.setVisibility(View.GONE);
                            mSetWeightGoal.setText(R.string.adjust_goal);
                            mGoalProgress.animateProgress(1000, 0, 0);
                        } else if (current < Float.parseFloat(goalWeight)) {
                            currentGoalStatus = WEIGHT_GOAL_ADJUST;
                            mGoalDesc.setVisibility(View.VISIBLE);
                            mGoalDesc.setText(getResources().getString(R.string.gain_goal, Math.abs(current - goal)));
                            mSetWeightGoal.setText(R.string.adjust_goal);
                            mGoalProgress.animateProgress(1000, 0, closerPercent);
                        } else {
                            currentGoalStatus = WEIGHT_GOAL_NEW_SET;
                            mGoalDesc.setVisibility(View.VISIBLE);
                            mGoalDesc.setText(R.string.success_goal);
                            mSetWeightGoal.setText(R.string.set_new_goal);
                            mGoalProgress.animateProgress(1000, 0, 100);
                        }
                    } else {
                        if (current >= start) {
                            currentGoalStatus = WEIGHT_GOAL_ADJUST;
                            mGoalDesc.setVisibility(View.GONE);
                            mSetWeightGoal.setText(R.string.adjust_goal);
                            mGoalProgress.animateProgress(1000, 0, 0);
                        } else if (current > goal) {
                            currentGoalStatus = WEIGHT_GOAL_ADJUST;
                            mGoalDesc.setVisibility(View.VISIBLE);
                            mGoalDesc.setText(getResources().getString(R.string.lose_goal, Math.abs(current - goal)));
                            mSetWeightGoal.setText(R.string.adjust_goal);
                            mGoalProgress.animateProgress(1000, 0, closerPercent);
                        } else {
                            currentGoalStatus = WEIGHT_GOAL_NEW_SET;
                            mGoalDesc.setVisibility(View.VISIBLE);
                            mGoalDesc.setText(R.string.success_goal);
                            mSetWeightGoal.setText(R.string.set_new_goal);
                            mGoalProgress.animateProgress(1000, 0, 100);
                        }
                    }
                } else {
                    currentGoalStatus = WEIGHT_GOAL_SET;
                    mGoalDesc.setVisibility(View.VISIBLE);
                    mGoalDesc.setText(R.string.make_weight_goal_tip);
                    mSetWeightGoal.setText(R.string.set_weight_goal);
                }
                mSetWeightGoal.onPreDraw();
                mGoalDescArea.setOrientation(mSetWeightGoal.getLineCount() > 1 ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
            } else {
                mProgressArea.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 일별 최신자료 갱신함수
     * @param weightInfo 일별 최신자료
     * @param currentIndex 현재 보여주는 날자의 index
     * @param maxIndex 자료기지에 보관된 날자의 총수
     */
    public void setValues(WeightInfo weightInfo, int currentIndex, int maxIndex) {
        mWeightInfo = weightInfo;
        this.currentIndex = currentIndex;
        this.maxIndex = maxIndex;
        onFinishInflate();
    }

    /**
     * 목표도달상태를 보여주는 progress 갱신함수
     * @param start 시작몸무게
     * @param goal 목표몸무게
     */
    public void setGoalProgress(String start, String goal) {
        this.startWeight = start;
        this.goalWeight = goal;
        onFinishInflate();
    }

    @Override
    public void onClick(View view) {
        if (view == mSetWeightGoal) {
            Intent intent = new Intent(getContext(), WeightGoalActivity.class);
            if (currentGoalStatus == WEIGHT_GOAL_NEW_SET) {
                intent.putExtra("status", "new_goal_set");
                intent.putExtra("lastWeightValue", mWeightInfo.getWeightValue());
            }
            getContext().startActivity(intent);
        }
    }

    public void setOnViewClickListener(final OnViewClickListener onViewClickListener) {
        this.mViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener {
        void onPrevClicked();

        void onNextClicked();
    }
}
