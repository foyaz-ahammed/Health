package org.secuso.privacyfriendlyactivitytracker.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.OrderEditActivity.HealthItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 건강항목순서관련 Adapter
 */
public class ItemOrderAdapter extends RecyclerView.Adapter<ItemOrderAdapter.ItemViewHolder>
        implements DraggableItemAdapter<ItemOrderAdapter.ItemViewHolder> {
    List<HealthItem> mItemList = new ArrayList<>();
    Context mContext;

    public ItemOrderAdapter(List<HealthItem> itemList, Context context) {
        mItemList = itemList;
        mContext = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(viewType == 0 ? R.layout.list_item_draggable : R.layout.history_divider, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (mItemList.get(position).getViewType() == 1)
            return;

        if (position == 0 || (position > 0 && mItemList.get(position - 1).getViewType() == 1)) {
            holder.mDecorator.setVisibility(View.GONE);
        } else {
            holder.mDecorator.setVisibility(View.VISIBLE);
        }
        String name = mItemList.get(position).getName();
        String[] healthItems = mContext.getResources().getStringArray(R.array.health_item_list);
        if (name.equals(healthItems[0])) {
            name = mContext.getResources().getString(R.string.main_weight_title);
        } else if (name.equals(healthItems[1])) {
            name = mContext.getResources().getString(R.string.main_exercise_title);
        } else if (name.equals(healthItems[2])) {
            name = mContext.getResources().getString(R.string.main_blood_pressure_title);
        } else if (name.equals(healthItems[3])) {
            name = mContext.getResources().getString(R.string.main_heart_rate_title);
        } else if (name.equals(healthItems[4])) {
            name = mContext.getResources().getString(R.string.main_water_title);
        } else {
            name = mContext.getResources().getString(R.string.main_cycle_title);
        }
        holder.mItemName.setText(name);

        if (mItemList.get(position).getIsShown() == 1) {
            holder.mDragHandler.setVisibility(View.VISIBLE);
            holder.mAdd.setVisibility(View.VISIBLE);
            holder.mRemove.setVisibility(View.GONE);
            holder.mDragHandler.setClickable(true);
        } else {
            holder.mDragHandler.setVisibility(View.INVISIBLE);
            holder.mAdd.setVisibility(View.GONE);
            holder.mRemove.setVisibility(View.VISIBLE);
            holder.mDragHandler.setClickable(false);
        }

        holder.mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //선택된 항목의 상태를 invisible 상태로 변경
                mItemList.get(position).setIsShown(0);
                //목록이 divider 를 가지고 있는지 확인
                boolean hasDivider = false;
                for (int i = 0; i < mItemList.size(); i ++) {
                    if (mItemList.get(i).getViewType() == 1) {
                        hasDivider = true;
                        break;
                    }
                }

                //선택된 항목 삭제
                HealthItem item = mItemList.remove(position);
                //현재 상태가 visible 상태이고 view 형태가 divider 가 아닌것을 찾고 그 다음에 새 항목 추가
                int i;
                for (i = 0; i < mItemList.size(); i ++) {
                    if (mItemList.get(i).getIsShown() == 0)
                        break;
                }
                mItemList.add(hasDivider ? i + 1 : i, item);
                //목록이 가름선을 가지고 있지 않으면 새 가름선 추가
                if (!hasDivider)
                    mItemList.add(i, new HealthItem(1, null, 0, 0));
                else {
                    if (mItemList.get(0).getViewType() == 1)
                        mItemList.remove(0);
                }
                notifyDataSetChanged();
            }
        });

        holder.mRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //선택된 항목의 상태를 visible 로 변경
                mItemList.get(position).setIsShown(1);
                //check if list have a divider or not
                boolean hasDivider = false;
                for (int i = 0; i < mItemList.size(); i ++) {
                    if (mItemList.get(i).getViewType() == 1) {
                        hasDivider = true;
                        break;
                    }
                }
                //선택된 항목 삭제
                HealthItem item = mItemList.remove(position);
                // 상태가 visible 이고 view 형태가 divider 가 아닌 항목을 찾고 그 후에 새 항목 추가
                int i;
                for (i = 0; i < mItemList.size(); i ++) {
                    if (mItemList.get(i).getViewType() == 0 && mItemList.get(i).getIsShown() == 0)
                        break;
                }

                mItemList.add(hasDivider ? i - 1 : i, item);
                //목록이 가름선을 가지고 있지 않으면 새 가름선 추가
                if (!hasDivider)
                    mItemList.add(i + 1, new HealthItem(1, null, 0, 0));
                else {
                    if (mItemList.get(mItemList.size() - 1).getViewType() == 1)
                        mItemList.remove(mItemList.size() - 1);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return Math.max(mItemList.size(), 0);
    }

    @Override
    public long getItemId(int position) {
        return mItemList.get(position).getOrder();
    }

    @Override
    public int getItemViewType(int position) {
        return mItemList.get(position).getViewType();
    }

    /**
     * 항목을 끌기하여 옮길수 있는지 확인하는 함수
     * @param holder 끌기하려는 viewHolder
     * @param position Adapter 자료의 위치
     * @param x 다치기 한 x좌표
     * @param y 다치기 한 y좌표
     * @return 항목을 끌기할수 있는 여부
     */
    @Override
    public boolean onCheckCanStartDrag(@NonNull ItemViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandler;

        if (!dragHandleView.isClickable()) {
            return false;
        }
        final int offsetX = containerView.getLeft() + (int) (containerView.getTranslationX() + 0.5f);
        final int offsetY = containerView.getTop() + (int) (containerView.getTranslationY() + 0.5f);

        return Utils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    /**
     * onCheckCanStartDrag 함수의 돌림값이 true 인 경우 호출되는 함수
     * @param holder 끌기하는 viewHolder
     * @param position Adapter 자료에서의 위치
     * @return 항목을 끌기하여 옮길수 있는 령역
     */
    @Nullable
    @Override
    public ItemDraggableRange onGetItemDraggableRange(@NonNull ItemViewHolder holder, int position) {
        int i;
        for (i = 0; i < mItemList.size(); i ++) {
            if (mItemList.get(i).getIsShown() == 0)
                break;
        }
        if (i == 0)
            return new ItemDraggableRange(-1, -1);
        else return new ItemDraggableRange(0, i - 1);
    }

    /**
     * 항목이 이동될때 호출되는 callback. 이동 작업결과를 자료목록에 반영해야 한다.
     * @param fromPosition 항목의 이전위치
     * @param toPosition 항목의 새 위치
     */
    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        HealthItem removed = mItemList.remove(fromPosition);
        mItemList.add(toPosition, removed);
    }

    /**
     * 끌기하는동안 끌기하는 항목을 지정된 위치에 놓을수 있는지 확인하는 함수
     * @param draggingPosition 현재 끌고 있는 항목의 위치
     * @param dropPosition 끌기하는 항목의 놓기가능여부를 확인할수 있는 위치
     * @return 지정된 위치에 놓을수 있는 확인여부
     */
    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }

    /**
     * 항목의 끌기를 시작하였을때 호출되는 callback
     * @param position 항목의 위치
     */
    @Override
    public void onItemDragStarted(int position) {
        notifyDataSetChanged();
    }

    /**
     * 항목의 끌기를 끝냈을때 호출되는 callback
     * @param fromPosition 항목의 이전위치
     * @param toPosition 항목의 새 위치
     * @param result 끌기 작업이 성공했는지를 나타내는 확인여부
     */
    @Override
    public void onItemDragFinished(int fromPosition, int toPosition, boolean result) {
        notifyDataSetChanged();
    }

    /**
     * 자료목록갱신함수
     * @param itemList 새로 갱신될 자료목록
     */
    public void updateList(List<HealthItem> itemList) {
        this.mItemList = new ArrayList<>(itemList);
        notifyDataSetChanged();
    }

    /**
     * 자료목록을 얻는 함수
     * @return 자료목록
     */
    public List<HealthItem> getItemList() {
        return mItemList;
    }

    /**
     * 개별적인 항목의 viewHolder
     */
    public static class ItemViewHolder extends AbstractDraggableItemViewHolder {
        public LinearLayout mContainer;
        public ImageView mDragHandler;
        public TextView mItemName;
        public ImageView mAdd;
        public ImageView mRemove;
        public View mDecorator;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mDecorator = itemView.findViewById(R.id.decorator);
            mContainer = itemView.findViewById(R.id.container);
            mDragHandler = itemView.findViewById(R.id.drag_handler);
            mItemName = itemView.findViewById(R.id.item_name);
            mAdd = itemView.findViewById(R.id.add);
            mRemove = itemView.findViewById(R.id.remove);
        }
    }
}
