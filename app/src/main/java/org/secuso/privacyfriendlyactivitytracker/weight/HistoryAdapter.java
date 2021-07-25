package org.secuso.privacyfriendlyactivitytracker.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;

/**
 * 기록자료를 현시하는 adapter
 */
public class HistoryAdapter extends ListAdapter<WeightInfo, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_DAY = 0;
    private static final int VIEW_TYPE_WEIGHT = 1;
    private static final int VIEW_TYPE_DAY_DIVIDER = 2;
    private static final int VIEW_TYPE_WEIGHT_DIVIDER = 3;

    Context mContext;
    HistoryActivity mHistoryActivity;

    boolean isDeletable = false; // 삭제 가능 상태

    private final OnHeaderClickListener listener;

    public HistoryAdapter(Context context, HistoryActivity historyActivity, OnHeaderClickListener listener) {
        super(DIFF_CALLBACK);
        mContext = context;
        mHistoryActivity = historyActivity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_DAY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.weight_history_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_WEIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.weight_history_item, parent, false);
            return new WeightViewHolder(view);
        } else if (viewType == VIEW_TYPE_DAY_DIVIDER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.history_divider, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.history_item_divider, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        int viewType = getItemViewType(position);

        WeightInfo info = getItem(position);
        if (info == null)
            return;

        if (viewType == VIEW_TYPE_DAY_DIVIDER || viewType == VIEW_TYPE_WEIGHT_DIVIDER) return;
        if (viewType == VIEW_TYPE_DAY) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            DateTime dateTime = info.getMeasureDateTime();
            viewHolder.date.setText(mContext.getResources().getString(R.string.date_format3, dateTime.getYear(), dateTime.getMonthOfYear(), dateTime.getDayOfMonth()));
            viewHolder.weightAvg.setText(mContext.getResources().getString(R.string.avg_text, Float.parseFloat(info.getWeightValue())));
            viewHolder.header.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onHeaderClick(holder);
                }
            });
        } else if (viewType == VIEW_TYPE_WEIGHT) {
            WeightViewHolder viewHolder = (WeightViewHolder) holder;
            viewHolder.historyItemWeight.applyFromItemInfo(info, mHistoryActivity, isDeletable);
        }

    }

    @Override
    public int getItemViewType(int position) {
        switch (getItem(position).getType()) {
            case "day":
                return VIEW_TYPE_DAY;
            case "weight":
                return VIEW_TYPE_WEIGHT;
            case "day_divider":
                return VIEW_TYPE_DAY_DIVIDER;
            default:
                return VIEW_TYPE_WEIGHT_DIVIDER;
        }
    }

    /**
     * 삭제상태를 변경하는 함수
     * @param isDeletable true이면 삭제상태, false이면 삭제상태가 아님
     */
    public void activeDelete(boolean isDeletable) {
        this.isDeletable = isDeletable;
        this.notifyDataSetChanged();
    }

    /**
     * 날자별 평균 항목을 눌렀을때의 callback에 대한 interface
     */
    public interface OnHeaderClickListener {
        void onHeaderClick(RecyclerView.ViewHolder holder);
    }

    /**
     * 날자별 평균항목에 대한 viewholder
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView date;
        TextView weightAvg;
        LinearLayout header;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.history_item_title);
            date = itemView.findViewById(R.id.date);
            weightAvg = itemView.findViewById(R.id.weight_avg);
        }
    }

    /**
     * 개별적인 기록에 대한 viewHolder
     */
    public static class WeightViewHolder extends RecyclerView.ViewHolder {
        HistoryItemWeight historyItemWeight;

        public WeightViewHolder(@NonNull View itemView) {
            super(itemView);
            historyItemWeight = itemView.findViewById(R.id.weight_history_item);
        }
    }

    /**
     * 자료가 갱신되였을때 개별적인 항목들의 이전자료와 현재자료를 비교하는 diffcallback
     */
    public static final DiffUtil.ItemCallback<WeightInfo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WeightInfo>() {

                @Override
                public boolean areItemsTheSame(
                        @NonNull WeightInfo oldData, @NonNull WeightInfo newData) {
                    // User properties may have changed if reloaded from the DB, but ID is fixed
                    return oldData.getType().equals(newData.getType());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull WeightInfo oldData, @NonNull WeightInfo newData) {
                    // NOTE: if you use equals, your object must properly override Object#equals()
                    // Incorrectly returning false here will result in too many animations.
                    return oldData.getWeightValue().equals(newData.getWeightValue()) &&
                            oldData.getModifiedTime() == newData.getModifiedTime() &&
                            oldData.getMeasureDateTime().getMillis() == newData.getMeasureDateTime().getMillis();
                }
            };
}
