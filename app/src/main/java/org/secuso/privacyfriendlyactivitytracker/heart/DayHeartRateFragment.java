package org.secuso.privacyfriendlyactivitytracker.heart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.HeartRateInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRate;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomLineChartRenderer;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HeartRateViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 일별 심박수통계화면
 */
public class DayHeartRateFragment extends BaseHeartRateFragment {
    RecyclerView mDayHistory;
    TextView mHistoryTitle;

    SimpleDateFormat simpleDayFormat;
    Calendar day;

    HeartRateActivity mHeartRateActivity;

    public DayHeartRateFragment(HeartRateActivity heartRateActivity) {
        // Required empty public constructor
        mHeartRateActivity = heartRateActivity;
    }

    public static DayHeartRateFragment newInstance(HeartRateActivity heartRateActivity) {
        DayHeartRateFragment fragment = new DayHeartRateFragment(heartRateActivity);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        day = Calendar.getInstance();
        day.set(Calendar.HOUR_OF_DAY, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MILLISECOND, 0);
        simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_heart_rate, container, false);

        mLineChart = view.findViewById(R.id.chart);
        measureDataLayout = view.findViewById(R.id.measure_show);
        measureDataLayout.setOnViewClickListener(this);
        mDayHistory = view.findViewById(R.id.daily_history);
        mHistoryTitle = view.findViewById(R.id.history_title);

        DayHeartRateHistoryAdapter adapter = new DayHeartRateHistoryAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDayHistory.setLayoutManager(layoutManager);
        mDayHistory.setAdapter(adapter);

        heartRateViewModel = new ViewModelProvider(requireActivity()).get(HeartRateViewModel.class);
        heartRateViewModel.day.setValue(simpleDayFormat.format(day.getTime()));
        heartRateViewModel.instanceForDay();

        // 그라프에 표시할 일별 자료얻기
        heartRateViewModel.dayData.observe(getViewLifecycleOwner(), data -> {
            measureDataLayout.showPulseValue("", 0, 0, true);
            updateDayChart(data);

            mHeartRateActivity.cancelLoading();
        });

        // 목록으로 현시할 일별 자료 얻기
        heartRateViewModel.originDayData.observe(getViewLifecycleOwner(), new Observer<List<HeartRate>>() {
            @Override
            public void onChanged(List<HeartRate> data) {
                mHistoryTitle.setVisibility(data.size() > 0 ? View.VISIBLE : View.GONE);
                List<HeartRateInfo> heartRateInfoList = new ArrayList<>();
                for (int i = 0; i < data.size(); i ++) {
                    heartRateInfoList.add(new HeartRateInfo(data.get(data.size() - i - 1)));
                    if (i != data.size() - 1)
                        heartRateInfoList.add(new HeartRateInfo(2));
                }
                adapter.submitList(heartRateInfoList);
            }
        });

        mLineChart.setOnChartValueSelectedListener(this);

        measureDataLayout.updateDay(day);

        return view;
    }

    /**
     * 이전단추를 눌렀을때 자료갱신
     */
    @Override
    public void onPrevClicked() {
        day.add(Calendar.DAY_OF_MONTH, -1);
        heartRateViewModel.day.setValue(simpleDayFormat.format(day.getTime()));
        measureDataLayout.updateDay(day);
    }

    /**
     * 다음 단추를 눌렀을때 자료갱신
     */
    @Override
    public void onNextClicked() {
        day.add(Calendar.DAY_OF_MONTH, 1);
        heartRateViewModel.day.setValue(simpleDayFormat.format(day.getTime()));
        measureDataLayout.updateDay(day);
    }

    /**
     * 그라프에서 개별적인 항목을 눌렀을때 선택한 시간과 값을 얻는 함수
     * @param e 해당 항목의 자료
     * @param h Highlight
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        long selectedTime = day.getTimeInMillis();
        selectedTime += ((int) e.getX()) * HeartRateViewModel.ONE_MINUTE;

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        measureDataLayout.showPulseValue(timeFormat.format(new Date(selectedTime)), (int) e.getY(), (int) e.getY(), true);
    }

    /**
     * 그라프 갱신
     * @param data 자료기지로부터 얻은 자료
     */
    public void updateDayChart(List<List<HeartRate>> data) {
        List<String> barChartXValues = new ArrayList<>();
        // x 축 label 목록 생성
        for (int i = 0; i < 25; i ++) {
            if (i == 0 || i == 24)
                barChartXValues.add(i, "00:00");
            else if (i == 6)
                barChartXValues.add(i, "06:00");
            else if (i == 12)
                barChartXValues.add(i, "12:00");
            else if (i == 18)
                barChartXValues.add(i, "18:00");
            else
                barChartXValues.add(i, "");
        }

        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();

        Entry start = new Entry(0, 0);
        Entry end = new Entry(1440, 0);
        LineDataSet chartLineDataSet = new LineDataSet(Arrays.asList(start, end), "");
        chartLineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        chartLineDataSet.setDrawCircles(false);
        chartLineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.transparent), 0);

        chartLineDataSet.setDrawValues(false);
        chartLineDataSet.setHighlightEnabled(false);
        lineDataSets.add(chartLineDataSet);

        // 그라프에 표시할 자료 분리
        for (int i = 0; i < data.size(); i ++) {
            List<Entry> dataEntries = new ArrayList<>();
            for (int j = 0; j < data.get(i).size(); j ++) {
                dataEntries.add(new Entry((float) (data.get(i).get(j).getMeasureTime() -
                        day.getTimeInMillis()) / HeartRateViewModel.ONE_MINUTE, data.get(i).get(j).getPulseValue()));
            }
            LineDataSet lineDataSet = new LineDataSet(dataEntries, "");
            lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.heart_rate_line_chart_color));
            lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.heart_rate_line_chart_color));
            lineDataSet.setHighLightColor(ContextCompat.getColor(getContext(), R.color.heart_rate_line_chart_color));
            lineDataSet.setDrawValues(false);
            lineDataSet.setDrawCircleHole(false);
            if (dataEntries.size() != 1)
                lineDataSet.setDrawCircles(false);
            lineDataSet.setCircleRadius(3.0f);
            lineDataSet.setFillAlpha(85);
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillDrawable(ContextCompat.getDrawable(getContext(), R.drawable.heart_rate_chart_gradient));
            lineDataSets.add(lineDataSet);
        }

        LineData combinedData = new LineData(lineDataSets);
        mLineChart.setData(combinedData);
        mLineChart.getXAxis().setValueFormatter(new XAxisValueFormatter(barChartXValues));
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLineChart.getXAxis().setDrawGridLines(false);
        mLineChart.getXAxis().setLabelCount(25, true);
        mLineChart.getAxisLeft().setAxisMinimum(0);
        mLineChart.getAxisLeft().setAxisMaximum(220);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setDescription(null);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.animateY(500, Easing.EaseInCubic);
        mLineChart.highlightValue(null);
        mLineChart.setRenderer(new CustomLineChartRenderer(mLineChart, mLineChart.getAnimator(), mLineChart.getViewPortHandler()));
        mLineChart.invalidate();
    }

    /**
     * x 축에 label 을 표시하는 형식 설정을 위한 클라스
     */
    public static class XAxisValueFormatter extends IndexAxisValueFormatter {
        private final List<String> values;

        public XAxisValueFormatter(List<String> values) {
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value) {
            //날자형식이 MM-dd인 경우 chart x label을 빈 문자렬로 표시
            if (this.values.get((int) value / 60).contains("-"))
                return "";
            // 최대값이 1440 (60분 * 24시간)이므로 시간별로 분류하기 위해 60으로 나눈다.
            // label 개수를 25으로 설정하였으므로 얻어지는 value 값이 60의 배수가 된다.
            return this.values.get((int) value / 60);
        }
    }
}