package org.secuso.privacyfriendlyactivitytracker.heart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.models.HeartRateInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRate;
import org.secuso.privacyfriendlyactivitytracker.weight.DailyWeightMeasureHistoryAdapter;

/**
 * 날자별 기록보기에 대한 adapter
 */
public class DayHeartRateHistoryAdapter extends ListAdapter<HeartRateInfo, DayHeartRateHistoryAdapter.ViewHolder> {
    private static final int VIEW_TYPE_HISTORY_ITEM = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;

    Context mContext;

    protected DayHeartRateHistoryAdapter(Context context) {
        super(DIFF_CALLBACK);

        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HISTORY_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.heart_rate_item, parent, false);
            return new ViewHolder(view);
        }

        if (viewType == VIEW_TYPE_DIVIDER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.history_item_divider, parent, false);
            return new ViewHolder(view);
        }

        return null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_DIVIDER) return;

        DateTime dateTime = new DateTime(getItem(position).getMeasureTime());
        holder.mTime.setText(Utils.getTimeString(mContext, dateTime.getHourOfDay(), dateTime.getMinuteOfHour()));
        holder.mPulseValue.setText(mContext.getString(R.string.with_bpm, getItem(position).getPulseValue()));
        if (getItem(position).getStatus() >= 0) {
            holder.mStatus.setVisibility(View.VISIBLE);
            String[] statusNameArray = mContext.getResources().getStringArray(R.array.heart_rate_status_drawable_array);
            int id = mContext.getResources().getIdentifier(statusNameArray[getItem(position).getStatus()],
                    "drawable", mContext.getPackageName());
            holder.mStatus.setImageDrawable(mContext.getDrawable(id));
        } else {
            holder.mStatus.setVisibility(View.GONE);
        }
        holder.mNote.setVisibility(getItem(position).getNote().isEmpty() ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, HeartRateDetailActivity.class);
            intent.putExtra(HeartRateMeasureActivity.MEASURE_TIME, getItem(position).getMeasureTime());
            intent.putExtra(HeartRateMeasureActivity.MEASURE_RESULT, getItem(position).getPulseValue());
            intent.putExtra(HeartRateMeasureActivity.MEASURE_NOTE, getItem(position).getNote());
            intent.putExtra(HeartRateMeasureActivity.MEASURE_STATUS, getItem(position).getStatus());
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getType() == 1)
            return VIEW_TYPE_HISTORY_ITEM;
        else return VIEW_TYPE_DIVIDER;
    }

    /**
     * 개별적 심박수항목의 기초 holder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTime;
        TextView mPulseValue;
        ImageView mNote;
        ImageView mStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mTime = itemView.findViewById(R.id.time);
            mPulseValue = itemView.findViewById(R.id.pulse_value);
            mNote = itemView.findViewById(R.id.note);
            mStatus = itemView.findViewById(R.id.status);
        }
    }

    public static final DiffUtil.ItemCallback<HeartRateInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<HeartRateInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull HeartRateInfo oldItem, @NonNull HeartRateInfo newItem) {
            return oldItem.getType() == newItem.getType();
        }

        @Override
        public boolean areContentsTheSame(@NonNull HeartRateInfo oldItem, @NonNull HeartRateInfo newItem) {
            return oldItem.getMeasureTime() == newItem.getMeasureTime();
        }
    };
}
