package org.secuso.privacyfriendlyactivitytracker.blood;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;

/**
 * 혈압기록을 포함하는 layout
 */
public class HistoryBloodPressureContainer extends HistoryItemContainer {

    public HistoryBloodPressureContainer(Context context) {
        super(context);
    }

    public HistoryBloodPressureContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryBloodPressureContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    /**
     * 혈압 layout을 얻는 함수
     * @return
     */
    @Override
    public int getLayoutResource() {
        return R.layout.blood_pressure_history_item;
    }
}
