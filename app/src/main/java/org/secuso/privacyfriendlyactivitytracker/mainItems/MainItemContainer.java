package org.secuso.privacyfriendlyactivitytracker.mainItems;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer.HistoryItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 건강화면들에 배치된 건강항목들의 기초클라스
 */
public class MainItemContainer extends LinearLayout {
    TextView mDesc;
    TextView mDate;
    TextView mValue;
    TextView mUnit;
    LinearLayout mValueArea;
    LinearLayout mVisualArea;

    List<HistoryItemInfo> mData = new ArrayList<>();

    public MainItemContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mDesc = findViewById(R.id.desc);
        mDate = findViewById(R.id.date);
        mValue = findViewById(R.id.value);
        mUnit = findViewById(R.id.unit);
        mValueArea = findViewById(R.id.value_area);
        mVisualArea = findViewById(R.id.visual_area);

        if (mData.size() > 0) {
            mDesc.setVisibility(View.GONE);
            mDate.setVisibility(View.VISIBLE);
            mValueArea.setVisibility(View.VISIBLE);
            mVisualArea.setVisibility(View.VISIBLE);
        } else {
            mDesc.setVisibility(View.VISIBLE);
            mDate.setVisibility(View.GONE);
            mValueArea.setVisibility(View.GONE);
            mVisualArea.setVisibility(View.GONE);
        }
    }

    /**
     * 현시할 자료를 갱신하는 함수
     * @param data 갱신된 자료
     */
    public void setData(List<HistoryItemInfo> data) {
        this.mData = data;
        onFinishInflate();
    }
}
