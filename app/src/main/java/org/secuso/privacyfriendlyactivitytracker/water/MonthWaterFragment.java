package org.secuso.privacyfriendlyactivitytracker.water;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;
import org.secuso.privacyfriendlyactivitytracker.fragments.BaseFragment;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WaterViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * 월별물자료현시화면
 */
public class MonthWaterFragment extends BaseFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    WaterAverageLayout mAverageLayout;

    WaterViewModel viewModel;

    int target = 0; // 하루 물목표

    public MonthWaterFragment() {
        // Required empty public constructor
    }

    public static MonthWaterFragment newInstance() {
        MonthWaterFragment fragment = new MonthWaterFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whichPeriod = PERIOD_MONTH;
        initPeriod();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weekly_water, container, false);
        measureDataLayout = view.findViewById(R.id.measure_show);
        measureDataLayout.setOnViewClickListener(this);
        mChart = view.findViewById(R.id.chart);
        mAverageLayout = view.findViewById(R.id.average_layout);

        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                Calendar selectedDate = Calendar.getInstance();
                if (viewModel.monthPeriod.getValue() != null)
                    selectedDate.setTimeInMillis(viewModel.monthPeriod.getValue().get(0));
                selectedDate.add(Calendar.DAY_OF_MONTH, (int) e.getX() - 1);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
                String selectedDateString = getString(R.string.date_format7, simpleDateFormat.format(selectedDate.getTime()), selectedDate.get(Calendar.DAY_OF_MONTH));
                measureDataLayout.showWaterValue(String.valueOf((int) e.getY()), selectedDateString);
            }

            @Override
            public void onNothingSelected() {

            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(WaterViewModel.class);
        viewModel.instanceForMonth();
        viewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());

        viewModel.monthData.observe(getViewLifecycleOwner(), monthData -> {
            List<WaterInfo> infoList = new ArrayList<>();
            for (int i = 0; i < monthData.size(); i ++) {
                WaterInfo info = new WaterInfo();
                info.setId(monthData.get(i).getId());
                info.setGlasses(monthData.get(i).getGlasses());
                DateTime dateTime = new DateTime(monthData.get(i).getDate());
                info.setMeasureDateTime(dateTime);
                infoList.add(info);
            }
            data.clear();
            data.addAll(infoList);
            measureDataLayout.showWaterValue("", "");
            generateChartData("water");
            updateChart("water");
            mAverageLayout.setAverage(infoList);
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        target = sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0);
        mAverageLayout.changeTargetVisibility(target > 0, target);

        measureDataLayout.updatePeriod(firstDay, lastDay, whichPeriod.equals(PERIOD_WEEK));

        return view;
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    /**
     * 이전단추를 눌렀을때의 처리를 위한 callback
     */
    @Override
    public void onPrevClicked() {
        super.onPrevClicked();
        viewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    /**
     * 다음단추를 눌렀을때의 처리를 위한 callback
     */
    @Override
    public void onNextClicked() {
        super.onNextClicked();
        viewModel.setMonthPeriod(firstDay.getTimeInMillis(), lastDay.getTimeInMillis());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        if (key.equals(getString(R.string.pref_daily_water_goal))) {
            target = sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0);
            mAverageLayout.changeTargetVisibility(target > 0, target);
        }
    }
}