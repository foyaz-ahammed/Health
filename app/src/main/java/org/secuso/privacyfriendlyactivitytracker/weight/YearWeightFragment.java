package org.secuso.privacyfriendlyactivitytracker.weight;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.fragments.BaseFragment;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WeightViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 년별 몸무게자료 현시화면
 */
public class YearWeightFragment extends BaseFragment {
    List<WeightInfo> weightData = new ArrayList<>();
    WeightViewModel viewModel;

    int year;

    public YearWeightFragment() {
        // Required empty public constructor
    }

    /**
     * WeekWeightFragment instance를 생성하는 함수
     * @return weightFragment의 instance
     */
    public static YearWeightFragment newInstance() {
        YearWeightFragment fragment = new YearWeightFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_YEAR;
        year = Calendar.getInstance().get(Calendar.YEAR);

        viewModel = new ViewModelProvider(requireActivity()).get(WeightViewModel.class);
        viewModel.setYear(year);
        viewModel.instanceForYear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weight, container, false);

        measureDataLayout = view.findViewById(R.id.measure_show);
        measureDataLayout.setOnViewClickListener(this);
        typeLayout = view.findViewById(R.id.type_select_area);
        typeLayout.setOnViewClickListener(this);
        mLineChart = view.findViewById(R.id.chart);

        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Calendar selectedDate = Calendar.getInstance();
                if (viewModel.year.getValue() != null) {
                    selectedDate.set(Calendar.YEAR, viewModel.year.getValue());
                    selectedDate.set(Calendar.MONTH, 0);
                }
                selectedDate.add(Calendar.MONTH, (int) e.getX() - 1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                measureDataLayout.showWeightValue(getString(R.string.avg, e.getY()), simpleDateFormat.format(selectedDate.getTime()),
                        typeLayout.getWeightSelected(), true);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        viewModel.yearData.observe(getViewLifecycleOwner(), monthList -> {
            List<WeightInfo> yearData = new ArrayList<>();
            for (int i = 0; i < monthList.size(); i ++) {
                WeightInfo info = new WeightInfo();
                info.setWeightValue(monthList.get(i).getWeightAvg());
                info.setFatRateValue(monthList.get(i).getFatRateAvg());
                info.setDate(monthList.get(i).getMeasureDate());
                DateTime dateTime = new DateTime(monthList.get(i).getMeasureMilliTime());
                info.setMeasureDateTime(dateTime);
                yearData.add(info);
            }

            measureDataLayout.showWeightValue("", "", typeLayout.getWeightSelected(), true);
            typeLayout.setMinMaxValue(yearData);

            data.clear();
            data.addAll(yearData);
            generateChartData(typeLayout.getWeightSelected() ? "weight" : "fatRate");
            updateWeightChart("weight");
        });

        measureDataLayout.updateYear(year);
        
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 이전을 눌렀을때 호출되는 callback
     */
    @Override
    public void onPrevClicked() {
        year--;
        viewModel.setYear(year);
        measureDataLayout.updateYear(year);
    }

    /**
     * 다음을 눌렀을때 호출되는 callback
     */
    @Override
    public void onNextClicked() {
        year++;
        viewModel.setYear(year);
        measureDataLayout.updateYear(year);
    }
}