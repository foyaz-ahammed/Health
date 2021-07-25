package org.secuso.privacyfriendlyactivitytracker.heart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HeartRateViewModel;

import java.util.Calendar;

/**
 * 주 심박수통계화면
 */
public class YearHeartRateFragment extends BaseHeartRateFragment {
    int year;

    public YearHeartRateFragment() {
        // Required empty public constructor
    }


    public static YearHeartRateFragment newInstance() {
        YearHeartRateFragment fragment = new YearHeartRateFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_YEAR;
        year = Calendar.getInstance().get(Calendar.YEAR);
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
        heartRateViewModel.setYear(year);
        heartRateViewModel.instanceForYear();
        heartRateViewModel.yearData.observe(getViewLifecycleOwner(), data -> {
            measureDataLayout.showPulseValue("", 0, 0, false);
            generateHeartRateData(data);
            updateHeartRateChart();
        });

        mRangeChart.setOnChartValueSelectedListener(this);

        measureDataLayout.updateYear(year);

        return view;
    }

    /**
     * 이전단추를 눌렀을때 자료 갱신
     */
    @Override
    public void onPrevClicked() {
        year--;
        heartRateViewModel.setYear(year);
        measureDataLayout.updateYear(year);
    }

    /**
     * 다음단추를 눌렀을때 자료 갱신
     */
    @Override
    public void onNextClicked() {
        year++;
        heartRateViewModel.setYear(year);
        measureDataLayout.updateYear(year);
    }
}