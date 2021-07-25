package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.ExerciseInfo;

/**
 * 월별 전체자료를 현시하는 layout
 */
public class TotalLayout extends LinearLayout {
    TextView mTotalRun;
    TextView mTotalWalk;
    TextView mTotalCycle;
    TextView mTotalSwim;

    ExerciseInfo data = new ExerciseInfo(); // 현시할 자료

    public TotalLayout(Context context) {
        super(context);
    }

    public TotalLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TotalLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTotalRun = findViewById(R.id.total_run_distance);
        mTotalWalk = findViewById(R.id.total_walk_distance);
        mTotalCycle = findViewById(R.id.total_cycle_distance);
        mTotalSwim = findViewById(R.id.total_swim_distance);
    }

    /**
     * 월별 전체자료를 보여주는 함수
     * @param data 현시할 자료
     */
    @SuppressLint("DefaultLocale")
    public void updateData(ExerciseInfo data) {
        this.data = data;
        if (data.getTotalRunning() != null)
            mTotalRun.setText(String.format("%.2f", data.getTotalRunning().getTotal()));
        if (data.getTotalWalking() != null)
            mTotalWalk.setText(String.format("%.2f", data.getTotalWalking().getTotal()));
        if (data.getTotalCycling() != null)
            mTotalCycle.setText(String.format("%.2f", data.getTotalCycling().getTotal()));
        if (data.getTotalSwimming() != null)
            mTotalSwim.setText(String.valueOf((int) (data.getTotalSwimming().getTotal())));
    }
}
