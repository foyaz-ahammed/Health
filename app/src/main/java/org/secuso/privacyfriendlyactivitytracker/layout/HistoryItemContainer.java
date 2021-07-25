package org.secuso.privacyfriendlyactivitytracker.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.adapters.HistoryItemRecyclerViewAdapter;

import java.util.List;

/**
 * 기록들을 담고있는 layout
 */
public class HistoryItemContainer extends LinearLayout {
    RecyclerView mHistoryRecyclerView;
    HistoryItemRecyclerViewAdapter mAdapter;

    public HistoryItemContainer(Context context) {
        this(context, null);
    }

    public HistoryItemContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HistoryItemContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.history_layout, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mHistoryRecyclerView = findViewById(R.id.history_item_list);
        mAdapter = new HistoryItemRecyclerViewAdapter(getContext(), getLayoutResource());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mHistoryRecyclerView.setLayoutManager(layoutManager);
        mHistoryRecyclerView.setAdapter(mAdapter);
    }

    /**
     * Adapter 에 기록자료를 갱신하는 함수
     * @param list 기록자료목록
     * @param toolbarActivity 기록을 포함하고 있는 activity
     */
    public void setData(List<HistoryItemInfo> list, ToolbarActivity toolbarActivity) {
        mAdapter.setActivity(toolbarActivity);
        mAdapter.submitList(list);
    }

    /**
     * 기록삭제처리를 진행할수 있도록 하는 함수
     * @param isDeletable true 이면 기록삭제처리를 진행할수 있는 상태이고 false 이면 기록삭제처리를 진행 못함
     */
    public void activeDeletable(boolean isDeletable) {
        mAdapter.activeDeletable(isDeletable);
    }

    /**
     * Adapter 의 모든 항목들의 자료가 변경되였음을 알려주는 함수
     */
    public void notifyAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    /**
     * 기록의 개별적인 항목에 대한 layout 얻는 함수
     * @return 얻어진 layout id
     */
    protected int getLayoutResource() {
        return 0;
    }

    /**
     * 개별적인 기록자료의 기초 object
     */
    public static class HistoryItemInfo {
        public long modifiedTime;

        public void setModifiedTime(long modifiedTime) {
            this.modifiedTime = modifiedTime;
        }

        public long getModifiedTime() {
            return modifiedTime;
        }
    }
}
