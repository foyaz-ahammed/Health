package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;

/**
 * 운동기록자료를 포함하는 layout
 */
public class HistoryExerciseContainer extends HistoryItemContainer {

    public HistoryExerciseContainer(Context context) {
        super(context);
    }

    public HistoryExerciseContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryExerciseContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * 운동기록자료를 현시하는 layout 얻는 함수
     * @return layout
     */
    @Override
    public int getLayoutResource() {
        return R.layout.exercise_history_item;
    }
}
