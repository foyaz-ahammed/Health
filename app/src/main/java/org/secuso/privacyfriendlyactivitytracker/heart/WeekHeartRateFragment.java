package org.secuso.privacyfriendlyactivitytracker.heart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HeartRateViewModel;

/**
 * 주 심박수통계화면
 */
public class WeekHeartRateFragment extends BaseHeartRateFragment {

    public WeekHeartRateFragment() {
        // Required empty public constructor
    }


    public static WeekHeartRateFragment newInstance() {
        WeekHeartRateFragment fragment = new WeekHeartRateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_WEEK;
        initPeriod();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heart_rate, container, false);

        measureDataLayout = view.findViewById(R.id.measure_show);
        measureDataLayout.setOnViewClickListener(this);
        mRangeChart = view.findViewById(R.id.range_chart);

        heartRateViewModel = new ViewModelProvider(requireActivity()).get(HeartRateViewModel.class);
        heartRateViewModel.instanceForWeek();
        heartRateViewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
        heartRateViewModel.weekData.observe(getViewLifecycleOwner(), data -> {
            measureDataLayout.showPulseValue("", 0, 0, false);
            generateHeartRateData(data);
            updateHeartRateChart();
        });

        mRangeChart.setOnChartValueSelectedListener(this);

        measureDataLayout.updatePeriod(firstDay, lastDay, true);

        return view;
    }

    /**
     * 이전단추를 눌렀을때 자료갱신
     */
    @Override
    public void onPrevClicked() {
        super.onPrevClicked();
        heartRateViewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    /**
     * 다음 단추를 눌렀을때 자료갱신
     */
    @Override
    public void onNextClicked() {
        super.onNextClicked();
        heartRateViewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }
}