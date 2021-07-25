package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Parcelable;
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
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.models.ExerciseInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.Exercise;
import org.secuso.privacyfriendlyactivitytracker.utils.StatefulRecyclerView;
import org.secuso.privacyfriendlyactivitytracker.viewModel.ExerciseViewModel;
import org.secuso.privacyfriendlyactivitytracker.weight.HistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 운동기록 adapter
 */
public class ExerciseHistoryAdapter extends ListAdapter<ExerciseInfo, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_MONTH = 1;
    private static final int VIEW_TYPE_TOTAL = 2;
    private static final int VIEW_TYPE_WORKOUT = 3;
    private static final int VIEW_TYPE_MONTH_DIVIDER = 4;
    private static final int VIEW_TYPE_WORKOUT_DIVIDER = 5;

    private static final int TYPE_ALL = 0;

    Context mContext;
    ExerciseActivity mExerciseActivity;
    int mType; //운동형태 0: 모두보기 1: 달리기 2: 걷기 3: 자전거타기 5: 수영

    private final OnHeaderClickListener listener;

    public ExerciseHistoryAdapter(Context context, ExerciseActivity activity, OnHeaderClickListener listener) {
        super(DIFF_CALLBACK);
        mContext = context;
        mExerciseActivity = activity;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_MONTH) {
            view = LayoutInflater.from(mContext).inflate(R.layout.exercise_history_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == VIEW_TYPE_TOTAL) {
            view = LayoutInflater.from(mContext).inflate(R.layout.exercise_history_total, parent, false);
            return new TotalViewHolder(view);
        } else if (viewType == VIEW_TYPE_WORKOUT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.exercise_history_item, parent, false);
            return new WorkoutViewHolder(view);
        } else if (viewType == VIEW_TYPE_MONTH_DIVIDER) {
            view = LayoutInflater.from(mContext).inflate(R.layout.history_divider, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.history_item_divider, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_MONTH_DIVIDER || viewType == VIEW_TYPE_WORKOUT_DIVIDER)
            return;

        ExerciseInfo info = getItem(position);
        if (info == null)
            return;
        if (viewType == VIEW_TYPE_MONTH) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            Date date = new Date(info.getInfo().getStartTime().getMillis());
            String month = simpleMonthFormat.format(date);
            viewHolder.month.setText(month);

            viewHolder.header.setOnClickListener(view -> listener.onHeaderClick(holder));
        } else if (viewType == VIEW_TYPE_TOTAL) {
            TotalViewHolder viewHolder = (TotalViewHolder) holder;
            viewHolder.totalLayout.setVisibility(mType != 0 ? View.GONE : View.VISIBLE);
            viewHolder.totalIndividualLayout.setVisibility(mType != 0 ? View.VISIBLE : View.GONE);
            if (mType == TYPE_ALL) viewHolder.totalLayout.updateData(info);
            else viewHolder.totalIndividualLayout.updateData(info, mType);
        } else if (viewType == VIEW_TYPE_WORKOUT) {
            WorkoutViewHolder viewHolder = (WorkoutViewHolder) holder;
            viewHolder.exerciseHistoryItem.applyFromItemInfo(info.getInfo(), mExerciseActivity, false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ExerciseInfo info = getItem(position);
        switch (info.getViewType()) {
            case "month":
                return VIEW_TYPE_MONTH;
            case "total":
                return VIEW_TYPE_TOTAL;
            case "workout":
                return VIEW_TYPE_WORKOUT;
            case "month_divider":
                return VIEW_TYPE_MONTH_DIVIDER;
            default:
                return VIEW_TYPE_WORKOUT_DIVIDER;
        }
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    /**
     * type설정하는 함수
     * @param type
     */
    public void setType(int type) {
        mType = type;
    }

    /**
     * 월별 header viewHolder
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView month;
        LinearLayout header;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            month = itemView.findViewById(R.id.month);
            header = itemView.findViewById(R.id.history_header);
        }
    }

    /**
     * 월별 총자료에 대한 viewHolder
     */
    public static class TotalViewHolder extends RecyclerView.ViewHolder {
        TextView totalRunDistance;
        TextView totalWalkDistance;
        TextView totalCycleDistance;
        TextView totalSwimDistance;
        TotalLayout totalLayout;
        TextView totalDistance;
        TextView totalCalorie;
        TextView totalTimes;
        TotalIndividualLayout totalIndividualLayout;
        LinearLayout totalArea;

        public TotalViewHolder(@NonNull View itemView) {
            super(itemView);
            totalRunDistance = itemView.findViewById(R.id.total_run_distance);
            totalWalkDistance = itemView.findViewById(R.id.total_walk_distance);
            totalCycleDistance = itemView.findViewById(R.id.total_cycle_distance);
            totalSwimDistance = itemView.findViewById(R.id.total_swim_distance);
            totalLayout = itemView.findViewById(R.id.total_layout);
            totalDistance = itemView.findViewById(R.id.distance);
            totalCalorie = itemView.findViewById(R.id.calorie);
            totalTimes = itemView.findViewById(R.id.times);
            totalIndividualLayout = itemView.findViewById(R.id.total_individual_layout);
            totalArea = itemView.findViewById(R.id.total_area);
        }
    }

    /**
     * 개별적인 기록에 대한 viewHolder
     */
    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        HistoryItemExercise exerciseHistoryItem;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseHistoryItem = itemView.findViewById(R.id.exercise_history_item);
        }
    }

    public interface OnHeaderClickListener {
        void onHeaderClick(RecyclerView.ViewHolder holder);
    }

    /**
     * 자료가 갱신되였을때 개별적인 항목들의 이전자료와 현재자료를 비교하는 diffcallback
     */
    public static final DiffUtil.ItemCallback<ExerciseInfo> DIFF_CALLBACK =
        new DiffUtil.ItemCallback<ExerciseInfo>() {

            @Override
            public boolean areItemsTheSame(
                    @NonNull ExerciseInfo oldData, @NonNull ExerciseInfo newData) {
                // User properties may have changed if reloaded from the DB, but ID is fixed
                return oldData.getViewType().equals(newData.getViewType());
            }

            @Override
            public boolean areContentsTheSame(
                    @NonNull ExerciseInfo oldData, @NonNull ExerciseInfo newData) {
                // NOTE: if you use equals, your object must properly override Object#equals()
                // Incorrectly returning false here will result in too many animations.
                return oldData.getExerciseType() == newData.getExerciseType() &&
                        oldData.getInfo().getModifiedTime() == newData.getInfo().getModifiedTime() &&
                        oldData.getTotalRunning().getTotal() == newData.getTotalRunning().getTotal() &&
                        oldData.getTotalWalking().getTotal() == newData.getTotalWalking().getTotal() &&
                        oldData.getTotalCycling().getTotal() == newData.getTotalCycling().getTotal() &&
                        oldData.getTotalSwimming().getTotal() == newData.getTotalSwimming().getTotal();
            }
        };
}
