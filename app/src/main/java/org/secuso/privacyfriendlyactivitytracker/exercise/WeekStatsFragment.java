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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 주별 상태표시화면
 */
public class WeekStatsFragment extends BaseFragment {
    StatsTotalLayout mTotalLayout;

    ExerciseViewModel viewModel;
    StatsActivity mStatsActivity;
    StatsFragment mStatsFragment;

    public WeekStatsFragment(StatsActivity statsActivity, StatsFragment statsFragment) {
        mStatsActivity = statsActivity;
        mStatsFragment = statsFragment;
    }

    public static WeekStatsFragment newInstance(StatsActivity statsActivity, StatsFragment statsFragment) {
        WeekStatsFragment fragment = new WeekStatsFragment(statsActivity, statsFragment);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_WEEK;
        initPeriod();

        viewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
        viewModel.instanceForWeekStats();
        viewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
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
                if (viewModel.weekPeriod.getValue() != null)
                    selectedDate.setTimeInMillis(viewModel.weekPeriod.getValue().get(0));
                selectedDate.add(Calendar.DAY_OF_MONTH, (int) e.getX() - 1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                String selectedDateString = getString(R.string.date_format7, simpleDateFormat.format(selectedDate.getTime()), selectedDate.get(Calendar.DAY_OF_MONTH));
                measureDataLayout.showExerciseValue(String.valueOf(e.getY()), selectedDateString, viewModel.weekStatsType.getValue() == 5);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        viewModel.weekPeriodData.observe(getViewLifecycleOwner(), this::updateData);
        viewModel.weekTypeData.observe(getViewLifecycleOwner(), this::updateData);

        setType(mStatsActivity.getType());
        mStatsActivity.setWeekTypeListener(this::setType);

        measureDataLayout.updatePeriod(firstDay, lastDay, whichPeriod.equals(PERIOD_WEEK));

        return view;
    }

    /**
     * 보여줄 종목설정하는 함수
     * @param type 종목
     */
    private void setType(int type) {
        switch (type) {
            case 0:
                viewModel.setWeekStatsType(1);
                break;
            case 1:
                viewModel.setWeekStatsType(2);
                break;
            case 2:
                viewModel.setWeekStatsType(3);
                break;
            default:
                viewModel.setWeekStatsType(5);
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStatsActivity.setWeekTypeListener(null);
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

        mStatsFragment.cancelLoading();
    }

    /**
     * 이전단추를 눌렀을때의 처리를 위한 callback
     */
    @Override
    public void onPrevClicked() {
        super.onPrevClicked();
        viewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    /**
     * 다음단추를 눌렀을때의 처리를 위한 callback
     */
    @Override
    public void onNextClicked() {
        super.onNextClicked();
        viewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }
}