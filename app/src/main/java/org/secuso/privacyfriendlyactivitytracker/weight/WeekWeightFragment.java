package org.secuso.privacyfriendlyactivitytracker.weight;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
 * 주별 몸무게자료 현시화면
 */
public class WeekWeightFragment extends BaseFragment {
    List<WeightInfo> weightData = new ArrayList<>();
    WeightViewModel viewModel;

    public WeekWeightFragment() {
        // Required empty public constructor
    }

    /**
     * WeekWeightFragment instance를 생성하는 함수
     * @return weightFragment의 instance
     */
    public static WeekWeightFragment newInstance() {
        WeekWeightFragment fragment = new WeekWeightFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        whichPeriod = PERIOD_WEEK;
        initPeriod();

        viewModel = new ViewModelProvider(requireActivity()).get(WeightViewModel.class);
        viewModel.instanceForWeekPeriod();
        viewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
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
                if (viewModel.weekPeriod.getValue() != null)
                    selectedDate.setTimeInMillis(viewModel.weekPeriod.getValue().get(0));
                selectedDate.add(Calendar.DAY_OF_MONTH, (int) e.getX() - 1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                String selectedDateString = getString(R.string.date_format7, simpleDateFormat.format(selectedDate.getTime()),
                        selectedDate.get(Calendar.DAY_OF_MONTH));
                measureDataLayout.showWeightValue(getString(R.string.avg, e.getY()), selectedDateString, typeLayout.getWeightSelected(), false);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        viewModel.weekPeriodData.observe(getViewLifecycleOwner(), periodList -> {
            List<WeightInfo> periodData = new ArrayList<>();
            for (int i = 0; i < periodList.size(); i ++) {
                WeightInfo info = new WeightInfo();
                info.setWeightValue(periodList.get(i).getWeightAvg());
                info.setFatRateValue(periodList.get(i).getFatRateAvg());
                info.setDate(periodList.get(i).getMeasureDate());
                DateTime dateTime = new DateTime(periodList.get(i).getMeasureMilliTime());
                info.setMeasureDateTime(dateTime);
                periodData.add(info);
            }
            weightData = periodData;
            measureDataLayout.updatePeriod(firstDay, lastDay, whichPeriod.equals(PERIOD_WEEK));
            measureDataLayout.showWeightValue("", "", typeLayout.getWeightSelected(), false);
            typeLayout.setMinMaxValue(weightData);
            data.clear();
            data.addAll(weightData);
            generateChartData(typeLayout.getWeightSelected() ? "weight" : "fatRate");
            updateWeightChart("weight");
        });
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
        super.onPrevClicked();
        viewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    /**
     * 다음을 눌렀을때 호출되는 callback
     */
    @Override
    public void onNextClicked() {
        super.onNextClicked();
        viewModel.setWeekPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }
}