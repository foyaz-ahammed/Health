package org.secuso.privacyfriendlyactivitytracker.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.fragments.BaseFragment;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao.DayTotal;
import org.secuso.privacyfriendlyactivitytracker.viewModel.ExerciseViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 전체상태표시화면
 */
public class TotalStatsFragment extends BaseFragment {
    StatsTotalLayout mTotalLayout;

    ExerciseViewModel viewModel;
    StatsActivity mStatsActivity;

    public TotalStatsFragment(StatsActivity statsActivity) {
        mStatsActivity = statsActivity;
    }

    public static TotalStatsFragment newInstance(StatsActivity statsActivity) {
        TotalStatsFragment fragment = new TotalStatsFragment(statsActivity);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_TOTAL;
        initPeriod();

        viewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
        viewModel.instanceForTotalStats();
        viewModel.setTotalPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);
        measureDataLayout = view.findViewById(R.id.measure_show);
        measureDataLayout.setOnViewClickListener(this);
        mChart = view.findViewById(R.id.chart);
        mTotalLayout = view.findViewById(R.id.stats_total_layout);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Calendar selectedDate = Calendar.getInstance();
                if (viewModel.totalPeriod.getValue() != null) {
                    selectedDate.setTimeInMillis(viewModel.totalPeriod.getValue().get(0));
                }
                selectedDate.add(Calendar.YEAR, (int) e.getX() - 1);

                measureDataLayout.showExerciseValue(String.valueOf(e.getY()), getString(R.string.date_format10, selectedDate.get(Calendar.YEAR)),
                        viewModel.totalStatsType.getValue() == 5);

            }

            @Override
            public void onNothingSelected() {

            }
        });

        viewModel.totalPeriodData.observe(getViewLifecycleOwner(), this::updateData);
        viewModel.totalTypeData.observe(getViewLifecycleOwner(), this::updateData);

        setType(mStatsActivity.getType());
        mStatsActivity.setTotalTypeListener(this::setType);
        measureDataLayout.updateTotal(firstDay, lastDay);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStatsActivity.setTotalTypeListener(null);
    }

    /**
     * 보여줄 종목설정하는 함수
     * @param type 종목
     */
    private void setType(int type) {
        switch (type) {
            case 0:
                viewModel.setTotalStatsType(1);
                break;
            case 1:
                viewModel.setTotalStatsType(2);
                break;
            case 2:
                viewModel.setTotalStatsType(3);
                break;
            default:
                viewModel.setTotalStatsType(5);
                break;
        }
    }

    /**
     * 자료현시함수
     * @param periodData 현시할 자료
     */
    public void updateData(List<DayTotal> periodData) {
        List<WorkoutInfo> infoList = new ArrayList<>();
        for (int i = 0; i < periodData.size(); i ++) {
            infoList.add(new WorkoutInfo(periodData.get(i)));
        }
        data.clear();
        data.addAll(infoList);

        measureDataLayout.showExerciseValue("", "", false);
        generateChartData("exercise");
        updateChart("exercise");
        mTotalLayout.setValue(periodData);
    }

    /**
     * 이전단추를 눌렀을때의 처리를 위한 callback
     */
    @Override
    public void onPrevClicked() {
        firstDay.add(Calendar.YEAR, -5);
        lastDay.add(Calendar.YEAR, -5);
        viewModel.setTotalPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
        measureDataLayout.updateTotal(firstDay, lastDay);
    }

    /**
     * 다음단추를 눌렀을때의 처리를 위한 callback
     */
    @Override
    public void onNextClicked() {
        firstDay.add(Calendar.YEAR, 5);
        lastDay.add(Calendar.YEAR, 5);
        viewModel.setTotalPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
        measureDataLayout.updateTotal(firstDay, lastDay);
    }
}