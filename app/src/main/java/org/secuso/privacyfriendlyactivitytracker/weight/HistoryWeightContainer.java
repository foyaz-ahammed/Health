package org.secuso.privacyfriendlyactivitytracker.weight;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;

/**
 * 개별적인 기록에 대한 기초클라스
 */
public class HistoryWeightContainer extends HistoryItemContainer {

    public HistoryWeightContainer(Context context) {
        super(context);
    }

    public HistoryWeightContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HistoryWeightContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

    }

    @Override
    public int getLayoutResource() {
        return R.layout.weight_history_item;
    }
}
