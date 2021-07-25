package org.secuso.privacyfriendlyactivitytracker.weight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 날자별 기록보기에 대한 adapter
 */
public class DailyWeightMeasureHistoryAdapter extends RecyclerView.Adapter<DailyWeightMeasureHistoryAdapter.ViewHolder> {
    private static final int VIEW_TYPE_HISTORY_ITEM = 0;
    private static final int VIEW_TYPE_DIVIDER = 1;

    private List<WeightInfo> mDayWeightInfoList = new ArrayList<>();
    Context mContext;

    public DailyWeightMeasureHistoryAdapter(Context context, List<WeightInfo> dayWeightInfoList) {
        mContext = context;
        mDayWeightInfoList = dayWeightInfoList;
    }

    @NonNull
    @Override
    public DailyWeightMeasureHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HISTORY_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.weight_measure_item, parent, false);
            return new ViewHolder(view);
        }

        if (viewType == VIEW_TYPE_DIVIDER) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.history_item_divider, parent, false);
            return new ViewHolder(view);
        }

        return null;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onBindViewHolder(@NonNull DailyWeightMeasureHistoryAdapter.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == VIEW_TYPE_DIVIDER)
            return;

        int index = position / 2;
        final WeightInfo weightInfo = mDayWeightInfoList.get(index);
        holder.time.setText(Utils.getTimeString(mContext, weightInfo.getMeasureDateTime().getHourOfDay(), weightInfo.getMeasureDateTime().getMinuteOfHour()));
        holder.weightValue.setText(mContext.getResources().getString(R.string.with_kilogram, Float.parseFloat(weightInfo.getWeightValue())));
        if (weightInfo.getFatRateValue() != null) {
            holder.fatRateArea.setVisibility(View.VISIBLE);
            holder.fatRateValue.setText(mContext.getResources().getString(R.string.with_percent, Float.parseFloat(weightInfo.getFatRateValue())));
        } else {
            holder.fatRateArea.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AddRecordActivity.class);
                intent.putExtra("status", "update");
                intent.putExtra("_id", weightInfo.getId());
                intent.putExtra("millisec", weightInfo.getMeasureDateTime().getMillis());
                intent.putExtra("weightValue", weightInfo.getWeightValue());
                intent.putExtra("fatRateValue", weightInfo.getFatRateValue());
                mContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0)
            return VIEW_TYPE_HISTORY_ITEM;
        else return VIEW_TYPE_DIVIDER;
    }

    @Override
    public int getItemCount() {
        return mDayWeightInfoList.size() * 2 - 1;
    }

    /**
     * 기록자료 갱신
     * @param data 새로 받은 자료
     */
    public void updateData(List<WeightInfo> data) {
        mDayWeightInfoList = data;
        this.notifyDataSetChanged();
    }

    /**
     * 개별적 기록에 대한 viewholder
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView time;
        TextView weightValue;
        TextView fatRateValue;
        LinearLayout fatRateArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            weightValue = itemView.findViewById(R.id.weight_value);
            fatRateValue = itemView.findViewById(R.id.fat_rate_value);
            fatRateArea = itemView.findViewById(R.id.fat_rate_area);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
