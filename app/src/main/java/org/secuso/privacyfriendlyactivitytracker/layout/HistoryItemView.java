package org.secuso.privacyfriendlyactivitytracker.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.weight.HistoryActivity;

/**
 * 개별적인 기록항목의 기초 layout
 */
public abstract class HistoryItemView extends LinearLayout {

    public HistoryItemView(Context context) {
        this(context, null);
    }

    public HistoryItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void applyFromItemInfo(HistoryItemContainer.HistoryItemInfo info, ToolbarActivity toolbarActivity, boolean isDeletable);

    public abstract void setDividerVisibility(int position);
}
