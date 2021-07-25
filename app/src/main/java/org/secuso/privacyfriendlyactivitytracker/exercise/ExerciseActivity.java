package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.models.ExerciseInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.DeleteConfirmDialog;
import org.secuso.privacyfriendlyactivitytracker.viewModel.ExerciseViewModel;

import java.util.List;

/**
 * 운동화면
 */
public class ExerciseActivity extends ToolbarActivity implements View.OnClickListener,
        DeleteConfirmDialog.OnButtonClickListener, ExerciseHistoryAdapter.OnHeaderClickListener {

    RecyclerView mHistoryList;
    ExerciseHistoryAdapter historyAdapter;
    RelativeLayout mEmptyContent;

    FrameLayout mLoading;

    public ExerciseViewModel viewModel;
    int deleteId; //지우려는 운동항목의 id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_exercise);
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(R.layout.workout_choose_view);
        Spinner spinner = (Spinner) findViewById(R.id.workout_choose_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.exercise_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String selectedItem = adapterView.getItemAtPosition(position).toString();
                String[] array = getResources().getStringArray(R.array.exercise_type_array);
                if (selectedItem.equals(array[0])) {
                    historyAdapter.setType(0);
                    viewModel.setType(0);
                } else if (selectedItem.equals(array[1])) {
                    historyAdapter.setType(1);
                    viewModel.setType(1);
                } else if (selectedItem.equals(array[2])) {
                    historyAdapter.setType(2);
                    viewModel.setType(2);
                } else if (selectedItem.equals(array[3])) {
                    historyAdapter.setType(3);
                    viewModel.setType(3);
                } else {
                    historyAdapter.setType(5);
                    viewModel.setType(5);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mLoading = findViewById(R.id.loading);
        mHistoryList = findViewById(R.id.history_list);
        mEmptyContent = findViewById(R.id.empty_content);

        viewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        viewModel.instanceForHistory();

        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();
        if (bundle != null) {
            int exerciseType = (int) bundle.getInt("exerciseType");
            if (exerciseType == 2) {
                spinner.setSelection(2);
            } else if (exerciseType == 1 || exerciseType == 4) {
                spinner.setSelection(1);
            }
        } else {
            viewModel.setType(0);
        }

        viewModel.historyData.observe(this, data -> {
            showMainContent(data.size() > 0);
            historyAdapter.submitList(data);

            if (mLoading.getVisibility() == View.VISIBLE)
                mLoading.setVisibility(View.GONE);
        });

        viewModel.expandData.observe(this, data -> {
            showMainContent(data.size() > 0);
            historyAdapter.submitList(data);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mHistoryList.setLayoutManager(layoutManager);
        historyAdapter = new ExerciseHistoryAdapter(this, this, this);
        mHistoryList.setAdapter(historyAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_record) {
            Intent intent = new Intent(this, AddWorkoutActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.stats) {
            Intent intent = new Intent(this, StatsActivity.class);
            startActivity(intent);
        }
    }

    /**
     * delete id 설정
     * @param id 지우려는 id
     */
    public void setDeleteId(int id) {
        deleteId = id;
    }

    /**
     * 자료지우기를 눌렀을때의 처리
     */
    @Override
    public void onDeleteClicked() {
        viewModel.deleteData(deleteId);
    }

    /**
     * 월별 header를 눌렀을때의 처리
     * @param viewHolder
     */
    @Override
    public void onHeaderClick(RecyclerView.ViewHolder viewHolder) {
        viewModel.setExpandPosition(viewHolder.getAdapterPosition());
    }

    /**
     * 기본화면 보여주는 함수
     * @param canShow true이면 기본화면 보여주기, false이면 빈 UI 보여주기
     */
    private void showMainContent(boolean canShow) {
        mHistoryList.setVisibility(canShow ? View.VISIBLE : View.GONE);
        mEmptyContent.setVisibility(canShow ? View.GONE : View.VISIBLE);
    }
}