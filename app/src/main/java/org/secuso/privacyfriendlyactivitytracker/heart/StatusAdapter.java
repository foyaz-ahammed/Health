package org.secuso.privacyfriendlyactivitytracker.heart;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.secuso.privacyfriendlyactivitytracker.R;

import java.util.List;

/**
 * 상태목록표시를 위한 adapter
 */
public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.StatusViewHolder> {
    List<Drawable> statusImgArray;
    String[] statusTitleArray;

    int selectPosition = -1;

    Context context;

    public StatusAdapter(List<Drawable> statusImgArray, String[] statusTitleArray, Context context) {
        this.statusImgArray = statusImgArray;
        this.statusTitleArray = statusTitleArray;
        this.context = context;
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.heart_rate_status_item, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatusViewHolder holder, int position) {
        holder.statusImage.setImageDrawable(statusImgArray.get(position));
        holder.title.setText(statusTitleArray[position]);

        if (selectPosition >= 0 && position == selectPosition) {
            holder.statusImage.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_selected_background));
            holder.statusImage.setColorFilter(ContextCompat.getColor(context, R.color.white));
        } else {
            holder.statusImage.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_background));
            holder.statusImage.setColorFilter(ContextCompat.getColor(context, R.color.status_item_color));
        }

        holder.itemView.setOnClickListener(v -> {
            selectPosition = selectPosition == holder.getAdapterPosition() ? -1 : holder.getAdapterPosition();
            ((HeartRateResultActivity) context).mRestTip.setVisibility(selectPosition == 1 ? View.VISIBLE : View.GONE);
            ((HeartRateResultActivity) context).mHeartBar.setAfterExercise(selectPosition == 2);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder {
        ImageView statusImage;
        TextView title;

        public StatusViewHolder(@NonNull View itemView) {
            super(itemView);

            statusImage = itemView.findViewById(R.id.status_image);
            title = itemView.findViewById(R.id.status_title);

        }
    }
}
