package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.ExerciseInfo;

import java.util.Map;

/**
 * 월별 개별적인 종목들에 대한 전체자료를 현시하는 layout
 */
public class TotalIndividualLayout extends LinearLayout {
    TextView mDistance;
    TextView mCalorie;
    TextView mTimes;

    private static final int TYPE_ALL = 0;
    private static final int TYPE_RUNNING = 1;
    private static final int TYPE_WALKING = 2;
    private static final int TYPE_CYCLING = 3;
    private static final int TYPE_SWIMMING = 5;

    public TotalIndividualLayout(Context context) {
        super(context);
    }

    public TotalIndividualLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TotalIndividualLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mDistance = findViewById(R.id.distance);
        mCalorie = findViewById(R.id.calorie);
        mTimes = findViewById(R.id.times);
    }

    /**
     * 월별 지정한 종목에 대한 총자료 현시하는 함수
     * @param data 현시할 자료
     * @param type 현시할 종목
     */
    @SuppressLint("DefaultLocale")
    public void updateData(ExerciseInfo data, int type) {

        if (type == TYPE_RUNNING) {
            mDistance.setText(String.format("%.2f", data.getTotalRunning().getTotal()));
            mCalorie.setText(String.valueOf(getCalorie(type, data.getTotalRunning().getTotal())));
            mTimes.setText(String.valueOf(data.getTotalRunning().getCount()));
        } else if (type == TYPE_WALKING) {
            mDistance.setText(String.format("%.2f", data.getTotalWalking().getTotal()));
            mCalorie.setText(String.valueOf(getCalorie(type, data.getTotalWalking().getTotal())));
            mTimes.setText(String.valueOf(data.getTotalWalking().getCount()));
        } else if (type == TYPE_CYCLING) {
            mDistance.setText(String.format("%.2f", data.getTotalCycling().getTotal()));
            mCalorie.setText(String.valueOf(getCalorie(type, data.getTotalCycling().getTotal())));
            mTimes.setText(String.valueOf(data.getTotalCycling().getCount()));
        } else {
            mDistance.setText(String.format("%.2f", data.getTotalSwimming().getTotal()));
            mCalorie.setText(String.valueOf(getCalorie(type, data.getTotalSwimming().getTotal())));
            mTimes.setText(String.valueOf(data.getTotalSwimming().getCount()));
        }
    }

    /**
     * 카로리를 얻는 함수
     * @param activity 종목
     * @param total 총자료
     * @return
     */
    private int getCalorie(int activity, float total) {
        if (activity == TYPE_RUNNING) {
            return (int) (total * 60);
        } else if (activity == TYPE_WALKING) {
            return (int) (total * 30);
        } else if (activity == TYPE_CYCLING) {
            return (int) (total * 19);
        } else {
            return (int) (total / 500 * 78);
        }
    }
}
