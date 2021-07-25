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
 * 년별 상태표시함수
 */
public class YearStatsFragment extends BaseFragment {
    StatsTotalLayout mTotalLayout;

    ExerciseViewModel viewModel;
    StatsActivity mStatsActivity;

    int year;

    public YearStatsFragment(StatsActivity statsActivity) {
        mStatsActivity = statsActivity;
    }

    public static YearStatsFragment newInstance(StatsActivity statsActivity) {
        YearStatsFragment fragment = new YearStatsFragment(statsActivity);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_YEAR;
        year = Calendar.getInstance().get(Calendar.YEAR);

        viewModel = new ViewModelProvider(requireActivity()).get(ExerciseViewModel.class);
        viewModel.instanceForYearStats();
        viewModel.setYear(year);
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
                if (viewModel.year.getValue() != null) {
                    selectedDate.set(Calendar.YEAR, viewModel.year.getValue());
                    selectedDate.set(Calendar.MONTH, 0);
                }
                selectedDate.add(Calendar.MONTH, (int) e.getX() - 1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                measureDataLayout.showExerciseValue(String.valueOf(e.getY()), simpleDateFormat.format(selectedDate.getTime()),
                        viewModel.yearStatsType.getValue() == 5);
            }

            @Override
            public void onNothingSelected() {

            }
        });


        viewModel.yearData.observe(getViewLifecycleOwner(), this::updateData);
        viewModel.yearTypeData.observe(getViewLifecycleOwner(), this::updateData);

        setType(mStatsActivity.getType());
        mStatsActivity.setYearTypeListener(this::setType);

        measureDataLayout.updateYear(year);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mStatsActivity.setYearTypeListener(null);
    }

    /**
     * 보여줄 종목설정하는 함수
     * @param type 종목
     */
    private void setType(int type) {
        switch (type) {
            case 0:
                viewModel.setYearStatsType(1);
                break;
            case 1:
                viewModel.setYearStatsType(2);
                break;
            case 2:
                viewModel.setYearStatsType(3);
                break;
            default:
                viewModel.setYearStatsType(5);
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
     * 이전단추를 눌렀을때의 처리를 위한 함수
     */
    @Override
    public void onPrevClicked() {
        year--;
        viewModel.setYear(year);
        measureDataLayout.updateYear(year);
    }

    /**
     * 다음단추를 눌렀을때의 처리를 위한 함수
     */
    @Override
    public void onNextClicked() {
        year++;
        viewModel.setYear(year);
        measureDataLayout.updateYear(year);
    }
}