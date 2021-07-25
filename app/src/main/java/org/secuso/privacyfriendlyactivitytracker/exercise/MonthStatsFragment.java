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
 * 월별운동상태화면
 */
public class MonthStatsFragment extends BaseFragment {
    StatsTotalLayout mTotalLayout;

    ExerciseViewModel viewModel;
    StatsActivity mStatsActivity;

    public MonthStatsFragment(StatsActivity statsActivity) {
        mStatsActivity = statsActivity;
    }

    public static MonthStatsFragment newInstance(StatsActivity statsActivity) {
        MonthStatsFragment fragment = new MonthStatsFragment(statsActivity);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_MONTH;
        initPeriod();

        viewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
        viewModel.instanceForMonthStats();
        viewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
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
                if (viewModel.monthPeriod.getValue() != null)
                    selectedDate.setTimeInMillis(viewModel.monthPeriod.getValue().get(0));
                selectedDate.add(Calendar.DAY_OF_MONTH, (int) e.getX() - 1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                String selectedDateString = getString(R.string.date_format7, simpleDateFormat.format(selectedDate.getTime()), selectedDate.get(Calendar.DAY_OF_MONTH));
                measureDataLayout.showExerciseValue(String.valueOf(e.getY()), selectedDateString, viewModel.monthStatsType.getValue() == 5);
            }

            @Override
            public void onNothingSelected() {
            }
        });

        viewModel.monthPeriodData.observe(getViewLifecycleOwner(), this::updateData);
        viewModel.monthTypeData.observe(getViewLifecycleOwner(), this::updateData);

        setType(mStatsActivity.getType());
        mStatsActivity.setMonthTypeListener(this::setType);

        measureDataLayout.updatePeriod(firstDay, lastDay, whichPeriod.equals(PERIOD_WEEK));

        return view;
    }

    /**
     * 운동항목설정함수
     * @param type 설정하려는 운동항목
     */
    public void setType(int type) {
        switch (type) {
            case 0:
                viewModel.setMonthStatsType(1);
                break;
            case 1:
                viewModel.setMonthStatsType(2);
                break;
            case 2:
                viewModel.setMonthStatsType(3);
                break;
            default:
                viewModel.setMonthStatsType(5);
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 자료현시함수
     * @param periodData 자료기지로부터 받은 자료
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStatsActivity.setMonthTypeListener(null);
    }

    /**
     * 이전단추를 눌렀을때 호출되는 callback
     */
    @Override
    public void onPrevClicked() {
        super.onPrevClicked();
        viewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    /**
     * 다음단추를 눌렀을때 호출되는 callback
     */
    @Override
    public void onNextClicked() {
        super.onNextClicked();
        viewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }
}