package org.secuso.privacyfriendlyactivitytracker.heart;

import android.graphics.Color;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.fragments.BaseFragment;
import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRateDao;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HeartRateViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 심박수 통계현시화면들을 위한 기초 fragment
 */
public class BaseHeartRateFragment extends BaseFragment implements OnChartValueSelectedListener {
    public CandleStickChart mRangeChart;

    List<CandleEntry> generatePulseData = new ArrayList<>();
    List<String> xAxisValues = new ArrayList<>();

    HeartRateViewModel heartRateViewModel;

    public BaseHeartRateFragment() {}

    /**
     * 그라프갱신
     */
    public void updateHeartRateChart() {
        CandleDataSet candleDataSet = new CandleDataSet(generatePulseData, "");
        candleDataSet.setDrawValues(false);
        candleDataSet.setColor(ContextCompat.getColor(getContext(), R.color.heart_rate_bar_chart_color));
        candleDataSet.setHighLightColor(ContextCompat.getColor(getContext(), R.color.heart_rate_chart_highlight_color));
        candleDataSet.setShadowColor(ContextCompat.getColor(getContext(), R.color.heart_rate_bar_chart_color));
        candleDataSet.setDecreasingColor(ContextCompat.getColor(getContext(), R.color.heart_rate_bar_chart_color));
        candleDataSet.setIncreasingColor(ContextCompat.getColor(getContext(), R.color.heart_rate_bar_chart_color));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        if (whichPeriod.equals(PERIOD_WEEK))
            candleDataSet.setBarSpace(0.3f);
        candleDataSet.setNeutralColor(Color.LTGRAY);

        CandleData candleData = new CandleData(candleDataSet);
        mRangeChart.setData(candleData);
        mRangeChart.getXAxis().setValueFormatter(new ArrayListAxisValueFormatter(xAxisValues));
        //월별그라프인 경우 모든 x label 표시
        if (whichPeriod.equals(PERIOD_MONTH)) {
            mRangeChart.getXAxis().setLabelCount(xAxisValues.size());
            mRangeChart.getXAxis().setDrawGridLines(false);
        }
        if (whichPeriod.equals(PERIOD_YEAR)) {
            mRangeChart.getXAxis().setLabelCount(12);
        }
        mRangeChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mRangeChart.getAxisLeft().setAxisMinimum(0);
        mRangeChart.getAxisLeft().setAxisMaximum(220);
        mRangeChart.getAxisRight().setEnabled(false);
        mRangeChart.setTouchEnabled(true);
        mRangeChart.setDoubleTapToZoomEnabled(false);
        mRangeChart.setPinchZoom(false);
        mRangeChart.setDescription(null);
        mRangeChart.getLegend().setEnabled(false);
        mRangeChart.animateY(500, Easing.EaseInCubic);
        mRangeChart.setRenderer(new CustomCandleStickRenderer(mRangeChart, mRangeChart.getAnimator(), mRangeChart.getViewPortHandler()));
        mRangeChart.invalidate();
    }

    /**
     * 심박수자료 생성
     * @param data 자료기지에서 불러온 자료
     */
    public void generateHeartRateData(List<HeartRateDao.MaxMin> data) {
        generatePulseData.clear();
        xAxisValues.clear();
        Calendar day_iterating = Calendar.getInstance();
        if (whichPeriod.equals(PERIOD_WEEK) || whichPeriod.equals(PERIOD_MONTH))
            day_iterating = (Calendar) firstDay.clone();

        SimpleDateFormat dayFormat = new SimpleDateFormat("MM.dd", Locale.getDefault());

        int forCount;
        switch (whichPeriod) {
            case PERIOD_WEEK:
                forCount = 7;
                break;
            case PERIOD_MONTH:
                forCount = firstDay.getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
            case PERIOD_YEAR:
                forCount = 12;
                break;
            default:
                forCount = 7;
                break;
        }

        for (int i = 0; i < forCount; i ++) {
            if (whichPeriod.equals(PERIOD_WEEK) || whichPeriod.equals(PERIOD_MONTH)) {
                String day = dayFormat.format(new Date(day_iterating.getTimeInMillis()));
                boolean hasData = false;
                for (int j = 0; j < data.size(); j ++) {
                    if (day.equals(dayFormat.format(new Date(data.get(j).getMeasureTime())))) {
                        generatePulseData.add(new CandleEntry(i, data.get(j).getMax(),
                                data.get(j).getMin(), data.get(j).getMax(), data.get(j).getMin()));
                        hasData = true;
                        break;
                    }
                }
                if (!hasData) {
                    generatePulseData.add(new CandleEntry(i, 0, 0, 0, 0));
                }
                if (whichPeriod.equals(PERIOD_MONTH)) {
                    if (day_iterating.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY)
                        xAxisValues.add(day);
                    else xAxisValues.add("");
                } else xAxisValues.add(day);
                day_iterating.add(Calendar.DAY_OF_MONTH, 1);
            } else if (whichPeriod.equals(PERIOD_YEAR)) {
                boolean hasMonthData = false;
                for (int j = 0; j < data.size(); j ++) {
                    DateTime dateTime = new DateTime(data.get(j).getMeasureTime());
                    if (dateTime.getMonthOfYear() == i + 1) {
                        generatePulseData.add(new CandleEntry(i, data.get(j).getMax(), data.get(j).getMin(),
                                data.get(j).getMax(), data.get(j).getMin()));
                        hasMonthData = true;
                        break;
                    }
                }
                if (!hasMonthData) {
                    generatePulseData.add(new CandleEntry(i, 0, 0, 0, 0));
                }
                xAxisValues.add(String.valueOf(i + 1));
            }
        }
    }

    /**
     * 그라프에서 개별적인 항목을 눌렀을때 선택한 날자와 값을 얻는 함수
     * @param e 해당 항목의 자료
     * @param h Highlight
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Calendar selectedDate = Calendar.getInstance();
        if (heartRateViewModel.weekPeriod.getValue() != null) {
            if (whichPeriod.equals(PERIOD_WEEK)) {
                selectedDate.setTimeInMillis(heartRateViewModel.weekPeriod.getValue().get(0));
            } else if (whichPeriod.equals(PERIOD_MONTH)) {
                selectedDate.setTimeInMillis(heartRateViewModel.monthPeriod.getValue().get(0));
            }
        }
        selectedDate.add(Calendar.DAY_OF_MONTH, (int) e.getX());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", Locale.getDefault());
        String selectedDateString;
        if (whichPeriod.equals(PERIOD_YEAR))
            selectedDateString = simpleDateFormat.format(selectedDate.getTime());
        else
            selectedDateString = getString(R.string.date_format7, simpleDateFormat.format(
                    selectedDate.getTime()), selectedDate.get(Calendar.DAY_OF_MONTH));
        measureDataLayout.showPulseValue(selectedDateString, (int) ((CandleEntry) (e)).getHigh(),
                (int) ((CandleEntry) (e)).getLow(), false);
    }

    @Override
    public void onNothingSelected() {

    }
}
