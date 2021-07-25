package org.secuso.privacyfriendlyactivitytracker.heart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HeartRateViewModel;

/**
 * 월 심박수통계화면
 */
public class MonthHeartRateFragment extends BaseHeartRateFragment {

    public MonthHeartRateFragment() {
        // Required empty public constructor
    }


    public static MonthHeartRateFragment newInstance() {
        MonthHeartRateFragment fragment = new MonthHeartRateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_MONTH;
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
        heartRateViewModel.instanceForMonth();
        heartRateViewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
        heartRateViewModel.monthData.observe(getViewLifecycleOwner(), data -> {
            measureDataLayout.showPulseValue("", 0, 0, false);
            generateHeartRateData(data);
            updateHeartRateChart();
        });

        mRangeChart.setOnChartValueSelectedListener(this);

        measureDataLayout.updatePeriod(firstDay, lastDay, false);

        return view;
    }

    /**
     * 이전단추를 눌렀을때 자료갱신
     */
    @Override
    public void onPrevClicked() {
        super.onPrevClicked();
        heartRateViewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    /**
     * 다음 단추를 눌렀을때 자료갱신
     */
    @Override
    public void onNextClicked() {
        super.onNextClicked();
        heartRateViewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }
}