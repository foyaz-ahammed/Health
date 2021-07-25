package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemView;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.DeleteConfirmDialog;

/**
 * 개별적인 운동기록을 현시하는 layout
 */
public class HistoryItemExercise extends HistoryItemView implements View.OnClickListener, View.OnLongClickListener{
    ImageView mWorkoutImg;
    TextView mDistance;
    TextView mDuration;
    TextView mPace;
    TextView mDate;
    View mDivider;

    WorkoutInfo workoutInfo = new WorkoutInfo();
    ExerciseActivity mExerciseActivity;

    public HistoryItemExercise(Context context) {
        super(context);
    }

    public HistoryItemExercise(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryItemExercise(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDivider = findViewById(R.id.divider);
        mWorkoutImg = findViewById(R.id.workout_img);
        mDistance = findViewById(R.id.distance);
        mDuration = findViewById(R.id.duration);
        mPace = findViewById(R.id.pace);
        mDate = findViewById(R.id.date);
        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    /**
     * 개별적인 기록상태변경을 위한 함수
     * @param info 새로받은 기록에 대한 object
     * @param toolbarActivity 기록을 담고 있는 activity
     * @param isDeletable 삭제상태
     */
    @Override
    public void applyFromItemInfo(HistoryItemContainer.HistoryItemInfo info, ToolbarActivity toolbarActivity, boolean isDeletable) {
        if (!(info instanceof WorkoutInfo))
            return;

        mExerciseActivity = (ExerciseActivity) toolbarActivity;
        workoutInfo = (WorkoutInfo) info;
        switch (workoutInfo.getActivity()) {
            case 1:
                mWorkoutImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.history_outdoor_run));
                break;
            case 2:
                mWorkoutImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.history_outdoor_walk));
                break;
            case 3:
                mWorkoutImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.history_outdoor_cycle));
                break;
            case 4:
                mWorkoutImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.history_indoor_run));
                break;
            case 5:
                mWorkoutImg.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.history_pool_swim));
                break;
        }
        mDistance.setText(workoutInfo.getActivity() != 5 ? getResources().getString(R.string.with_kilometer,
                workoutInfo.getDistance()) : getResources().getString(R.string.with_meter_integer, (int) workoutInfo.getDistance()));
        mDuration.setText(workoutInfo.getDuration());
        mPace.setText(workoutInfo.getActivity() != 5 ?
                getResources().getString(R.string.kilometer_pace_unit, workoutInfo.getAveragePace(getContext())) :
                getResources().getString(R.string.swim_pace_unit, workoutInfo.getAveragePace(getContext())));
        mDate.setText(getResources().getString(R.string.date_format4, workoutInfo.getStartTime().getMonthOfYear(), workoutInfo.getStartTime().getDayOfMonth()));
    }

    /**
     * 개별적인 기록들의 divider상태를 변경하는 함수
     * @param position divider의 위치
     */
    @Override
    public void setDividerVisibility(int position) {
        mDivider.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        if (view == this) {
            Intent intent = new Intent(getContext(), WorkoutShowActivity.class);
            intent.putExtra("workoutInfo", workoutInfo);
            getContext().startActivity(intent);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        mExerciseActivity.setDeleteId(workoutInfo.getId());
        DeleteConfirmDialog confirmDialog = new DeleteConfirmDialog(mExerciseActivity);
        confirmDialog.setOnButtonClickListener(mExerciseActivity);
        confirmDialog.show();
        return false;
    }
}
