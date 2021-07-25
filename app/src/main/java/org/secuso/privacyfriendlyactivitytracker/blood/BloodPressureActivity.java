package org.secuso.privacyfriendlyactivitytracker.blood;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.DeleteConfirmDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.TriangularIndicatorBar;
import org.secuso.privacyfriendlyactivitytracker.viewModel.BloodViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 혈압기록보기화면
 */
public class BloodPressureActivity extends ToolbarActivity implements View.OnClickListener, DeleteConfirmDialog.OnButtonClickListener {
    LinearLayout mTopArea;
    TextView mDateTime;
    TextView mValue;
    TextView mLevel;
    TriangularIndicatorBar mIndicatorBar;
    HistoryBloodPressureContainer mHistoryContainer;
    LinearLayout mAddRecord;
    LinearLayout mSelectAll;
    public TextView mSelectAllText;
    LinearLayout mEmptyContent;
    LinearLayout mDelete;
    FrameLayout mLoading;

    int LEVEL_DESC = 0;
    int LEVEL_DUE_TO = 1;

    BloodViewModel viewModel;

    List<BloodPressureInfo> bloodPressureList;
    public boolean canDeletable = false; //삭제가능상태판별
    public Map<Integer, Boolean> checkList; // 전체 자료에서 선택된 항목들을 식별하는데 리용된다.
    public List<Integer> selectedItemList = new ArrayList<>(); //선택된 항목들의 id 목록 보관
    int bloodPressureCount = 0; // 기록자료개수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_blood_pressure);
        super.onCreate(savedInstanceState);

        mLoading = findViewById(R.id.loading);
        mTopArea = findViewById(R.id.top_area);
        mDateTime = findViewById(R.id.datetime);
        mValue = findViewById(R.id.blood_pressure_value);
        mLevel = findViewById(R.id.blood_pressure_level);
        mIndicatorBar = findViewById(R.id.indicator_bar);
        mHistoryContainer = findViewById(R.id.history_list);
        mSelectAll = findViewById(R.id.select_all);
        mAddRecord = findViewById(R.id.add_record);
        mSelectAllText = findViewById(R.id.select_all_text);
        mEmptyContent = findViewById(R.id.empty_content);
        mDelete = findViewById(R.id.delete);

        viewModel = new ViewModelProvider(this).get(BloodViewModel.class);
        viewModel.instanceForDetail();
        viewModel.bloodAllData.observe(this, infoList -> {
            bloodPressureList = infoList;
            bloodPressureCount = infoList.size();
            checkList = new HashMap<>(bloodPressureList.size());
            for (int i = 0; i < bloodPressureList.size(); i ++) {
                checkList.put(bloodPressureList.get(i).getId(), false);
            }
            showMainContent(bloodPressureCount > 0);
            updateTopArea();
            updateHistory();

            if (mLoading.getVisibility() == View.VISIBLE)
                mLoading.setVisibility(View.GONE);
        });
    }

    /**
     * Menu생성 함수
     * @param menu 생성될 menu
     * @return true이면 menu 보여주기, false이면 보여주지 않기
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu_help);
        return true;
    }

    /**
     * Menu 항목을 눌렀을때 처리를 진행하는 함수
     * @param item 눌러진 항목
     * @return 처리가 진행되였으면 true, 아니면 false
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_help) {
            Intent intent = new Intent(this, BloodPressureHelpActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (canDeletable) {
            setAllCheckable(false);
            disableDeletable(true);
            mSelectAllText.setText(getResources().getString(R.string.select_all));
        } else {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (canDeletable) {
            setAllCheckable(false);
            disableDeletable(true);
            mSelectAllText.setText(getResources().getString(R.string.select_all));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_record) {
            Intent intent = new Intent(this, AddBloodPressureActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.delete) {
            if (!canDeletable) {
                canDeletable = true;
                mSelectAll.setVisibility(View.VISIBLE);
                mAddRecord.setVisibility(View.GONE);
                Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
                getSupportActionBar().setTitle(R.string.nothing_selected);
                mHistoryContainer.activeDeletable(true);
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
            setAllCheckable(getCheckedCount() < bloodPressureCount);
            mSelectAllText.setText(getCheckedCount() == bloodPressureCount ?
                    getResources().getString(R.string.deselect_all) : getResources().getString(R.string.select_all));
            mHistoryContainer.notifyAdapter();
        }
    }

    public void changeTitle() {
        if (getCheckedCount() == 0)
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.nothing_selected);
        else if (getCheckedCount() == 1) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.one_item_selected_text, 1));
        } else {
            Objects.requireNonNull(getSupportActionBar()).setTitle(getResources().getString(R.string.multi_item_selected_text, getCheckedCount()));
        }
    }

    public int getCheckedCount() {
        int count = 0;
        for (boolean check : checkList.values()) {
            if (check) count ++;
        }
        return count;
    }

    private void setAllCheckable(boolean isChecked) {
        for (int i = 0; i < bloodPressureList.size(); i ++) {
            checkList.put(bloodPressureList.get(i).getId(), isChecked);
        }
    }

    private void disableDeletable(boolean isCanceled) {
        canDeletable = false;

        if (!isCanceled) {
            updateHistory();
        }
        mHistoryContainer.activeDeletable(false);

        TypedArray backBtn = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.homeAsUpIndicator});
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(backBtn.getResourceId(0, 0));
        getSupportActionBar().setTitle(R.string.main_blood_pressure_title);
        mSelectAll.setVisibility(View.GONE);
        mAddRecord.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDeleteClicked() {
        //선택한 개수가 500개이상이면 여러번에 나누어 삭제를 진행
        if (selectedItemList.size() > 500) {
            List<Integer> copiedSelectedItemList = new ArrayList<>(selectedItemList);
            AsyncTask.execute(() -> {
                for (int i = 0; i < selectedItemList.size() / 500 + 1; i++) {
                    if (copiedSelectedItemList.size() > 500) {
                        viewModel.deleteBlood(copiedSelectedItemList.subList(0, 500));
                        copiedSelectedItemList.subList(0, 500).clear();
                    } else {
                        viewModel.deleteBlood(copiedSelectedItemList.subList(0, copiedSelectedItemList.size()));
                        copiedSelectedItemList.subList(0, copiedSelectedItemList.size()).clear();
                    }
                }
            });
        } else {
            AsyncTask.execute(() -> viewModel.deleteBlood(selectedItemList));
        }
        disableDeletable(false);
    }

    private void showMainContent(boolean canShow) {
        mHistoryContainer.setVisibility(canShow ? View.VISIBLE : View.GONE);
        mTopArea.setVisibility(canShow ? View.VISIBLE : View.INVISIBLE);
        mEmptyContent.setVisibility(canShow ? View.GONE : View.VISIBLE);
        mDelete.setVisibility(canShow ? View.VISIBLE : View.GONE);
    }

    private void updateTopArea() {
        if (bloodPressureCount > 0) {
            DateTime latestTime = bloodPressureList.get(0).getMeasureDateTime();
            mDateTime.setText(getString(R.string.datetime_format, latestTime.getYear(), latestTime.getMonthOfYear(),
                    latestTime.getDayOfMonth(), Utils.get12Hour(latestTime.getHourOfDay()), latestTime.getMinuteOfHour(),
                    latestTime.getHourOfDay() < 12 ? getString(R.string.lany_am_label) : getString(R.string.lany_pm_label)));
            mValue.setText(getString(R.string.blood_pressure_format, bloodPressureList.get(0).getSystolicValue(), bloodPressureList.get(0).getDiastolicValue()));
            mLevel.setText(bloodPressureList.get(0).getLevel(this).get(LEVEL_DESC));
            if (bloodPressureList.get(0).getLevel(this).get(LEVEL_DUE_TO).equals("systolic")) {
                mIndicatorBar.setScore(bloodPressureList.get(0).getSystolicValue(), 200);
            } else {
                mIndicatorBar.setScore(bloodPressureList.get(0).getDiastolicValue(), 120);
            }
        }
    }

    private void updateHistory() {
        List<HistoryItemContainer.HistoryItemInfo> data = new ArrayList<>();
        data.addAll(bloodPressureList);
        mHistoryContainer.setData(data, this);
    }
}