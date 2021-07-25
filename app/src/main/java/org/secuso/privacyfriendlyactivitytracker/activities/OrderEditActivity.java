package org.secuso.privacyfriendlyactivitytracker.activities;

import android.graphics.drawable.NinePatchDrawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator;
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.adapters.ItemOrderAdapter;
import org.secuso.privacyfriendlyactivitytracker.persistence.HealthOrder;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HealthOrderViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 건강항목순서편집화면
 */
public class OrderEditActivity extends ToolbarActivity {
    RecyclerView mItemListRecyclerView;
    RecyclerViewDragDropManager mRecyclerViewDragDropManager;

    RecyclerView.Adapter wrappedAdapter;
    ItemOrderAdapter adapter;

    HealthOrderViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_order_edit);
        super.onCreate(savedInstanceState);

        mItemListRecyclerView = findViewById(R.id.item_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRecyclerViewDragDropManager = new RecyclerViewDragDropManager();
        mRecyclerViewDragDropManager.setDraggingItemShadowDrawable((NinePatchDrawable) ContextCompat.getDrawable(this, R.drawable.material_shadow_z3));

        adapter = new ItemOrderAdapter(new ArrayList<>(), this);
        wrappedAdapter = mRecyclerViewDragDropManager.createWrappedAdapter(adapter);
        mItemListRecyclerView.setLayoutManager(layoutManager);
        mItemListRecyclerView.setAdapter(wrappedAdapter);
        mItemListRecyclerView.setItemAnimator(new DraggableItemAnimator());
//        mItemListRecyclerView.addItemDecoration(new SimpleListDividerDecorator(ContextCompat.getDrawable(this, R.drawable.divider_layer), true));

        mRecyclerViewDragDropManager.attachRecyclerView(mItemListRecyclerView);

        mViewModel = new ViewModelProvider(this).get(HealthOrderViewModel.class);
        mViewModel.getHealthOrder().observe(this, healthOrders -> {
            if (healthOrders.size() > 0) {
                List<HealthItem> itemList = new ArrayList<>();
                for (int i = 0; i < healthOrders.size(); i ++) {
                    for (int j = 0; j < healthOrders.size(); j ++) {
                        if (healthOrders.get(j).order == i) {
                            itemList.add(new HealthItem(0, healthOrders.get(j).getName(),
                                    healthOrders.get(j).getOrder(), healthOrders.get(j).getIsShown()));
                            break;
                        }
                    }
                }
                if (adapter != null) {
                    for (int i = 0; i < itemList.size(); i ++) {
                        if ( i != 0 && itemList.get(i).getIsShown() == 0 && itemList.get(i - 1).getIsShown() == 1) {
                            itemList.add(i, new HealthItem(1, null, 0, 0));
                            break;
                        }
                    }
                    adapter.updateList(itemList);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Back 단추를 눌렀을때 이미 존재하는 순서는 삭제하고 새 순서를 자료기지에 보관
        List<HealthOrder> orders = mViewModel.getOrders();
        List<HealthItem> originHealthOrders = new ArrayList<>();
        for (int i = 0; i < orders.size(); i ++) {
            originHealthOrders.add(new HealthItem(0, orders.get(i).getName(), orders.get(i).getOrder(), orders.get(i).getIsShown()));
        }

        List<HealthItem> itemList = new ArrayList<>(adapter.getItemList());
        int dividerPos = 0;
        for (int i = 0; i < itemList.size(); i ++) {
            if (itemList.get(i).viewType == 1) {
                dividerPos = i;
                break;
            }
        }
        if (dividerPos != 0)
            itemList.remove(dividerPos);

        for (int i = 0; i < itemList.size(); i ++) {
            if (!originHealthOrders.get(i).getName().equals(itemList.get(i).getName()) ||
                    originHealthOrders.get(i).getIsShown() != itemList.get(i).getIsShown()) {
                mViewModel.deleteAllOrder();
                for (int j = 0; j < itemList.size(); j ++) {
                    mViewModel.insertOrUpdate(new HealthOrder(0, itemList.get(j).getName(), j,
                            itemList.get(j).getIsShown()));
                }
                break;
            }
        }
        super.onBackPressed();
    }

    /**
     * 건강항목 class
     */
    public static class HealthItem {
        int viewType;
        int order;
        String name;
        int isShown;

        public HealthItem(int viewType, String name, int order, int isShown) {
            this.viewType = viewType;
            this.order = order;
            this.name = name;
            this.isShown = isShown;
        }

        public int getViewType() {
            return viewType;
        }

        public int getIsShown() {
            return isShown;
        }

        public String getName() {
            return name;
        }

        public int getOrder() {
            return order;
        }

        public void setIsShown(int isShown) {
            this.isShown = isShown;
        }
    }
}