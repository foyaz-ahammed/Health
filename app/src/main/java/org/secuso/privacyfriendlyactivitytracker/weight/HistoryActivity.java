package org.secuso.privacyfriendlyactivitytracker.weight;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.DeleteConfirmDialog;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WeightViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 기록보기화면
 */
public class HistoryActivity extends ToolbarActivity implements View.OnClickListener,
        DeleteConfirmDialog.OnButtonClickListener, HistoryAdapter.OnHeaderClickListener {
    RecyclerView mHistoryList;
    HistoryAdapter historyAdapter;
    LinearLayout mSelectAll;
    public TextView mSelectAllText;
    LinearLayout mBottomArea;
    RelativeLayout emptyContent;
    FrameLayout mLoading;

    public Map<Integer, Boolean> checkList; // 항목선택여부판별을 위한 목록
    public List<Integer> selectedItemList = new ArrayList<>(); // 선택한 항목들의 id 목록
    public boolean canDeletable = false; // 선택가능상태 확인
    int weightCount = 0;
    WeightViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_history);
        super.onCreate(savedInstanceState);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (getSupportActionBar() == null) {
            setSupportActionBar(toolbar);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mHistoryList = findViewById(R.id.history_list);
        mSelectAll = findViewById(R.id.select_all);
        mSelectAllText = findViewById(R.id.select_all_text);
        mBottomArea = findViewById(R.id.bottom_area);
        emptyContent = findViewById(R.id.empty_content);
        mLoading = findViewById(R.id.loading);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mHistoryList.setLayoutManager(layoutManager);
        historyAdapter = new HistoryAdapter(getApplicationContext(), this, this);
        mHistoryList.setAdapter(historyAdapter);

        viewModel = new ViewModelProvider(this).get(WeightViewModel.class);
        viewModel.instanceForHistory();

        viewModel.historyData.observe(this, historyData -> {
            showMainContent(historyData.size() > 0);
            weightCount = viewModel.weightAllData.getValue().size();
            updateAdapter(historyData);
            if (mLoading.getVisibility() == View.VISIBLE)
                mLoading.setVisibility(View.GONE);
        });
        viewModel.expandData.observe(this, data -> historyAdapter.submitList(data));
    }

    /**
     *  선택취소단추를 눌렀을때의 처리
     * @return
     */
    @Override
    public boolean onSupportNavigateUp() {
        if (canDeletable) {
            setAllCheckable(false);
            disableDeletable();
            mSelectAllText.setText(getResources().getString(R.string.select_all));
        } else {
            onBackPressed();
        }
        return true;
    }

    /**
     * Back단추를 눌렀을때의 처리
     */
    @Override
    public void onBackPressed() {
        if (canDeletable) {
            setAllCheckable(false);
            disableDeletable();
            mSelectAllText.setText(getResources().getString(R.string.select_all));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.delete) {
            if (!canDeletable) {
                canDeletable = true;
                mSelectAll.setVisibility(View.VISIBLE);
                Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
                getSupportActionBar().setTitle(R.string.nothing_selected);
                historyAdapter.activeDelete(true);
            } else {
                selectedItemList.clear();
                for (int id : checkList.keySet()) {
                    if (checkList.get(id))
                        selectedItemList.add(id);
                }

                if (selectedItemList.size() > 0) {
                    DeleteConfirmDialog confirmDialog = new DeleteConfirmDialog(this);
                    confirmDialog.setOnButtonClickListener(this);
                    confirmDialog.show();
                }
            }
        } else if (view.getId() == R.id.select_all) {
            setAllCheckable(getCheckedCount() < weightCount);
            mSelectAllText.setText(getCheckedCount() == weightCount ?
                    getResources().getString(R.string.deselect_all) : getResources().getString(R.string.select_all));
            historyAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 자료가 비여있을 경우 비여있다는 UI를 보여주는 함수
     * @param canShow true이면 기본 UI를 현시, false이면 비여있다는 UI 현시
     */
    private void showMainContent(boolean canShow) {
        mHistoryList.setVisibility(canShow ? View.VISIBLE : View.GONE);
        mBottomArea.setVisibility(canShow ? View.VISIBLE : View.GONE);
        emptyContent.setVisibility(canShow ? View.GONE : View.VISIBLE);
    }

    /**
     * 기록선택을 진행할때 item 개수를 현시하는 title 갱신함수
     */
    public void changeTitle() {
        if (getCheckedCount() == 0)
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.nothing_selected);
        else if (getCheckedCount() == 1) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.one_item_selected_text, 1));
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.multi_item_selected_text, getCheckedCount()));
        }
    }

    /**
     * 선택된 기록들의 개수를 얻는 함수
     * @return 선택된 개수
     */
    public int getCheckedCount() {
        int count = 0;
        for (boolean check : checkList.values()) {
            if (check) count ++;
        }
        return count;
    }

    /**
     * 모두 선택을 눌렀을때 모든 기록들의 상태를 선택상태로 변환하는 함수
     * @param isChecked 모두선택 상태
     */
    private void setAllCheckable(boolean isChecked) {
        for (int i = 0; i < weightCount; i ++) {
            checkList.put(Objects.requireNonNull(viewModel.weightAllData.getValue()).get(i).getId(), isChecked);
        }

    }

    /**
     * 삭제할수 있는 상태를 비능동으로 만들어주는 함수
     */
    private void disableDeletable() {
        canDeletable = false;
        historyAdapter.activeDelete(false);

        TypedArray backBtn = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.homeAsUpIndicator});
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(backBtn.getResourceId(0, 0));
        getSupportActionBar().setTitle(R.string.history);
        mSelectAll.setVisibility(View.GONE);
    }

    /**
     * 삭제단추를 눌렀을때 호출되는 callback
     */
    @Override
    public void onDeleteClicked() {
        //선택한 개수가 500개이상이면 여러번에 나누어 삭제를 진행
        if (selectedItemList.size() > 500) {
            List<Integer> copiedSelectedItemList = new ArrayList<>(selectedItemList);
            AsyncTask.execute(() -> {
                for (int i = 0; i < selectedItemList.size() / 500 + 1; i++) {
                    if (copiedSelectedItemList.size() > 500) {
                        viewModel.deleteWeight(copiedSelectedItemList.subList(0, 500));
                        copiedSelectedItemList.subList(0, 500).clear();
                    } else {
                        viewModel.deleteWeight(copiedSelectedItemList.subList(0, copiedSelectedItemList.size()));
                        copiedSelectedItemList.subList(0, copiedSelectedItemList.size()).clear();
                    }
                }
            });
        } else {
            AsyncTask.execute(() -> viewModel.deleteWeight(selectedItemList));
        }
        disableDeletable();
    }

    /**
     * 기록이 선택되는데 따라 adapter를 갱신해주는 함수
     * @param historyData 기록자료
     */
    public void updateAdapter(List<WeightInfo> historyData) {
        checkList = new HashMap<>(viewModel.weightAllData.getValue().size());
        for (int i = 0; i < viewModel.weightAllData.getValue().size(); i ++) {
            checkList.put(viewModel.weightAllData.getValue().get(i).getId(), false);
        }
        historyAdapter.submitList(historyData);
    }

    /**
     * 날자별 기록을 보여주는 함수
     * @param holder 날자별 viewholder
     */
    @Override
    public void onHeaderClick(RecyclerView.ViewHolder holder) {
        viewModel.setExpandPosition(holder.getAdapterPosition());
    }
}