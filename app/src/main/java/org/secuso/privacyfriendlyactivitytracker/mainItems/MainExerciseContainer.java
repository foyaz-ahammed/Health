package org.secuso.privacyfriendlyactivitytracker.mainItems;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;

/**
 * 건강화면에 운동자료 현시하는 클라스
 */
public class MainExerciseContainer extends MainItemContainer implements View.OnClickListener {
    TextView mActivity;
    ImageView mMainImg;

    public MainExerciseContainer(Context context) {
        this(context, null);
    }

    public MainExerciseContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainExerciseContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    /**
     * 운동자료현시
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        mActivity = findViewById(R.id.activity);
        mMainImg = findViewById(R.id.main_img);

        if (mData.size() > 0) {
            mVisualArea.setVisibility(View.VISIBLE);
            mActivity.setVisibility(View.VISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_normal_background));
            WorkoutInfo workout = (WorkoutInfo) mData.get(0);
            int month = workout.getStartTime().getMonthOfYear();
            int day = workout.getStartTime().getDayOfMonth();
            mDate.setText(getResources().getString(R.string.date_format4, month, day));
            switch (workout.getActivity()) {
                case 1:
                    mActivity.setText(getResources().getString(R.string.outdoor_run));
                    mMainImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.main_outdoor_run));
                    break;
                case 2:
                    mActivity.setText(getResources().getString(R.string.outdoor_walk));
                    mMainImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.main_outdoor_run));
                    break;
                case 3:
                    mActivity.setText(getResources().getString(R.string.outdoor_cycle));
                    mMainImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.main_outdoor_cycle));
                    break;
                case 4:
                    mActivity.setText(getResources().getString(R.string.indoor_run));
                    mMainImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.main_indoor_run));
                    break;
                case 5:
                    mActivity.setText(getResources().getString(R.string.pool_swim));
                    mMainImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.main_pool_swim));
                    break;
            }
            mValue.setText(workout.getActivity() == 5 ? String.valueOf((int) workout.getDistance()) : String.format("%.2f", workout.getDistance()));
            mUnit.setText(workout.getActivity() == 5 ? getResources().getString(R.string.meter) :
                    getResources().getString(R.string.kilometer));
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_exercise_background));
            mVisualArea.setVisibility(View.INVISIBLE);
            mActivity.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

    }
}
