package org.secuso.privacyfriendlyactivitytracker.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer.HistoryItemInfo;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemView;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;

/**
 * 기록현시를 위한 adapter
 */
public class HistoryItemRecyclerViewAdapter extends ListAdapter<HistoryItemInfo, HistoryItemRecyclerViewAdapter.ViewHolder> {
    private static final int VIEW_TYPE_HISTORY_ITEM = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;

    int mLayoutResource;
    Context mContext;
    ToolbarActivity mToolbarActivity;

    boolean isDeletable = false; // 삭제가능상태판별

    public HistoryItemRecyclerViewAdapter(Context context, int layoutResource) {
        super(DIFF_CALLBACK);
        mContext = context;
        mLayoutResource = layoutResource;
    }

    @NonNull
    @Override
    public HistoryItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HISTORY_ITEM) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutResource, parent, false);
        return new ViewHolder(view);
        }

        if (viewType == VIEW_TYPE_DIVIDER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.history_item_divider, parent, false);
            return new ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_DIVIDER)
            return;

        if (getItem(position) == null)
            return;

        HistoryItemInfo info = getItem(position);
        HistoryItemView view = (HistoryItemView) holder.itemView;
        view.applyFromItemInfo(info, mToolbarActivity, isDeletable);
        view.setDividerVisibility(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (((BloodPressureInfo) getItem(position)).getType() == 0)
            return VIEW_TYPE_HISTORY_ITEM;
        else return VIEW_TYPE_DIVIDER;
    }

    /**
     * 기록현시를 포함하고 있는 activity 를 adapter 에 설정하는 함수
     * @param toolbarActivity 기록현시를 포함하고 있는 activity
     */
    public void setActivity(ToolbarActivity toolbarActivity) {
        mToolbarActivity = toolbarActivity;
    }

    /**
     * 기록삭제처리를 진행할수 있도록 하는 함수
     * @param isDeletable true 이면 기록삭제처리를 진행할수 있는 상태이고 false 이면 기록삭제처리를 진행 못함
     */
    public void activeDeletable(boolean isDeletable) {
        this.isDeletable = isDeletable;
        this.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {

        }
    }

    /**
     * 자료가 갱신되였을때 개별적인 항목들의 이전자료와 현재자료를 비교하는 diffcallback
     */
    public static final DiffUtil.ItemCallback<HistoryItemInfo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<HistoryItemInfo>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull HistoryItemInfo oldData, @NonNull HistoryItemInfo newData) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    if (oldData instanceof WorkoutInfo) {
                        WorkoutInfo oldInfo = (WorkoutInfo) oldData;
                        WorkoutInfo newInfo = (WorkoutInfo) newData;
                        return oldInfo.getId() == newInfo.getId();
                    } else if (oldData instanceof WeightInfo) {
                        WeightInfo oldInfo = (WeightInfo) oldData;
                        WeightInfo newInfo = (WeightInfo) newData;
                        return oldInfo.getId() == newInfo.getId();
                    } else if (oldData instanceof BloodPressureInfo) {
                        BloodPressureInfo oldInfo = (BloodPressureInfo) oldData;
                        BloodPressureInfo newInfo = (BloodPressureInfo) newData;
                        return oldInfo.getType() == newInfo.getType();
                    }
                    return true;
                }

                @SuppressLint("DiffUtilEquals")
                @Override
                public boolean areContentsTheSame(
                        @NonNull HistoryItemInfo oldData, @NonNull HistoryItemInfo newData) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    if (oldData instanceof WorkoutInfo) {
                        WorkoutInfo oldInfo = (WorkoutInfo) oldData;
                        WorkoutInfo newInfo = (WorkoutInfo) newData;
                        return oldInfo.getModifiedTime() == newInfo.getModifiedTime();
                    } else if (oldData instanceof WeightInfo) {
                        WeightInfo oldInfo = (WeightInfo) oldData;
                        WeightInfo newInfo = (WeightInfo) newData;
                        return oldInfo.getModifiedTime() == newInfo.getModifiedTime();
                    } else if (oldData instanceof BloodPressureInfo) {
                        BloodPressureInfo oldInfo = (BloodPressureInfo) oldData;
                        BloodPressureInfo newInfo = (BloodPressureInfo) newData;
                        return oldInfo.getModifiedTime() == newInfo.getModifiedTime();
                    }
                    return true;
                }
            };
}
