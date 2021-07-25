package org.secuso.privacyfriendlyactivitytracker.fragments;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomLineChartRenderer;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.layout.MeasureDataLayout;
import org.secuso.privacyfriendlyactivitytracker.layout.TypeLayout;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomLineChart;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * 상태현시화면들을 위한 기초 fragment
 */
public class BaseFragment extends Fragment implements MeasureDataLayout.OnViewClickListener, TypeLayout.OnViewClickListener {
    public MeasureDataLayout measureDataLayout;
    public TypeLayout typeLayout;

    public List<HistoryItemContainer.HistoryItemInfo> data = new ArrayList<>();
    public Map<String, Float> generatedChartData = new LinkedHashMap<>(); //현시할 그라프자료
    public Calendar firstDay, lastDay; // 해당 기간별 보기의 시작날자와 마감날자
    public CombinedChart mChart;
    public CustomLineChart mLineChart;
    public String whichPeriod; // "week", "month", "year", "total" 중 하나

    public static final String PERIOD_WEEK = "week";
    public static final String PERIOD_MONTH = "month";
    public static final String PERIOD_YEAR = "year";
    public static final String PERIOD_TOTAL = "total";

    public BaseFragment() {
        // Required empty public constructor
    }

    /**
     * chart 갱신함수
     * @param whichChart 어느 chart 를 갱신
     */
    public void updateChart(String whichChart) {
        int barChartI = 0;
        ArrayList<String> barChartXValues = new ArrayList<>();
        Map<String, Float> barChartDataMap;
        String barChartLabel = "";
        barChartDataMap = generatedChartData;
        List<BarEntry> dataEntries = new ArrayList<>();
        List<BarEntry> dataEntriesReachedDailyGoal = new ArrayList<>();
        for (Map.Entry<String, Float> dataEntry : barChartDataMap.entrySet()) {
            barChartXValues.add(barChartI, dataEntry.getKey());
            if (dataEntry.getValue() != null) {
                float val = dataEntry.getValue();
                dataEntries.add(new BarEntry(barChartI, val));
            }
            barChartI++;
        }
        BarDataSet barDataSet = new BarDataSet(dataEntries, barChartLabel);
        BarDataSet barDataSetReachedDailyGoal = new BarDataSet(dataEntriesReachedDailyGoal, barChartLabel);
        String formatPattern = "###,###,##0.0";
        if (whichChart.equals("water"))
            formatPattern = "###,###,##0";
        barDataSet.setValueFormatter(new DoubleValueFormatter(formatPattern));
        barDataSet.setDrawValues(false);
        barDataSet.setValueTextSize(10);
        barDataSetReachedDailyGoal.setValueFormatter(new DoubleValueFormatter(formatPattern));
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        Entry start = new Entry(0, 0);
        Entry end = new Entry(barChartI - 1, 0);
        LineDataSet chartLineDataSet = new LineDataSet(Arrays.asList(start, end), "");
        chartLineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        chartLineDataSet.setDrawCircles(false);
        chartLineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.transparent), 0);
        chartLineDataSet.setDrawValues(false);
        chartLineDataSet.setHighlightEnabled(false);
        lineDataSets.add(chartLineDataSet);

        CombinedData combinedData = new CombinedData();
        BarData barData = new BarData(barDataSet, barDataSetReachedDailyGoal);
        barData.setBarWidth(0.5f);
        combinedData.setData(barData);
        combinedData.setData(new LineData(lineDataSets));
        barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_color));

        barDataSetReachedDailyGoal.setColor(ContextCompat.getColor(getContext(), R.color.chart_color));
        mChart.getAxisLeft().setAxisMinimum(0);
        mChart.setData(combinedData);
        mChart.getXAxis().setValueFormatter(new ArrayListAxisValueFormatter(barChartXValues));
        //월별그라프인 경우 모든 x label 표시
        if (whichPeriod.equals(PERIOD_MONTH)) {
            mChart.getXAxis().setLabelCount(barChartXValues.size());
            mChart.getXAxis().setDrawGridLines(false);
        }
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        if (whichPeriod.equals(PERIOD_YEAR))
            mChart.getXAxis().setLabelCount(12);
        if (whichChart.equals("water"))
            mChart.getAxisLeft().setAxisMinValue(0);
        mChart.getAxisRight().setEnabled(false);
        mChart.setTouchEnabled(true);
        mChart.setDoubleTapToZoomEnabled(false);
        mChart.setPinchZoom(false);
        mChart.setDescription(null);
        mChart.getLegend().setEnabled(false);
        mChart.animateY(500, Easing.EaseInCubic);
        mChart.highlightValue(null);
        mChart.invalidate();
    }

    /**
     * 몸무게 chart 갱신함수
     * @param whichChart 몸무게 혹은 체지방률
     */
    public void updateWeightChart(String whichChart) {
        int barChartI = 0;
        ArrayList<String> barChartXValues = new ArrayList<>();
        Map<String, Float> lineChartDataMap;
        String barChartLabel = "";
        lineChartDataMap = generatedChartData;

        float max = 0, min = 0;
        for (String key : generatedChartData.keySet()) {
            if (generatedChartData.get(key) != null) {
                min = generatedChartData.get(key);
                break;
            }
        }
        for (String key : generatedChartData.keySet()) {
            if (generatedChartData.get(key) != null) {
                float value = generatedChartData.get(key);
                if (max < value) {
                    max = value;
                }
                if (value < min) {
                    min = value;
                }
            }
        }

        List<Entry> dataEntries = new ArrayList<>();
        List<Entry> dataEntriesReachedDailyGoal = new ArrayList<>();
        for (Map.Entry<String, Float> dataEntry : lineChartDataMap.entrySet()) {
            barChartXValues.add(barChartI, dataEntry.getKey());
            if (dataEntry.getValue() != null) {
                float val = dataEntry.getValue();
                dataEntries.add(new Entry(barChartI, val));
            }
            barChartI++;
        }
        LineDataSet barDataSet = new LineDataSet(dataEntries, barChartLabel);
        LineDataSet barDataSetReachedDailyGoal = new LineDataSet(dataEntriesReachedDailyGoal, barChartLabel);
        String formatPattern = "###,###,##0.0";
        barDataSet.setValueFormatter(new DoubleValueFormatter(formatPattern));
        barDataSetReachedDailyGoal.setValueFormatter(new DoubleValueFormatter(formatPattern));
        barDataSet.setLineWidth(2.0f);
        barDataSet.setCircleColor(getResources().getColor(R.color.chart_color));
        barDataSet.setHighLightColor(getResources().getColor(R.color.chart_color));
        barDataSet.setDrawValues(false);
        barDataSet.setCircleRadius(4.0f);
        ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        Entry start = new Entry(0, 0);
        Entry end = new Entry(barChartI - 1, 0);
        LineDataSet chartLineDataSet = new LineDataSet(Arrays.asList(start, end), "");
        chartLineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        chartLineDataSet.setDrawCircles(false);
        chartLineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.transparent), 0);

        chartLineDataSet.setDrawValues(false);
        chartLineDataSet.setHighlightEnabled(false);
        lineDataSets.add(chartLineDataSet);

        lineDataSets.add(barDataSet);
        lineDataSets.add(barDataSetReachedDailyGoal);
        LineData combinedData = new LineData(lineDataSets);
        barDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_color));
        barDataSetReachedDailyGoal.setColor(ContextCompat.getColor(getContext(), R.color.chart_color));

        mLineChart.getAxisLeft().setAxisMinimum(min - 5);
        mLineChart.getAxisLeft().setAxisMaximum(max + 5);
        mLineChart.setData(combinedData);
        mLineChart.getXAxis().setValueFormatter(new ArrayListAxisValueFormatter(barChartXValues));
        //월별그라프인 경우 모든 x label 표시
        if (whichPeriod.equals(PERIOD_MONTH)) {
            mLineChart.getXAxis().setLabelCount(barChartXValues.size());
            mLineChart.getXAxis().setDrawGridLines(false);
        }
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        if (whichPeriod.equals(PERIOD_YEAR))
            mLineChart.getXAxis().setLabelCount(12);
        if (whichChart.equals("water"))
            mLineChart.getAxisLeft().setAxisMinValue(0);
        mLineChart.getAxisRight().setEnabled(false);
        mLineChart.setTouchEnabled(true);
        mLineChart.setDoubleTapToZoomEnabled(false);
        mLineChart.setPinchZoom(false);
        mLineChart.setDescription(null);
        mLineChart.getLegend().setEnabled(false);
        mLineChart.setHighlightPerTapEnabled(true);
        mLineChart.highlightValue(null);
        mLineChart.animateX(500, Easing.EaseInOutSine);
        mLineChart.setRenderer(new CustomLineChartRenderer(mLineChart, mLineChart.getAnimator(), mLineChart.getViewPortHandler()));
        mLineChart.invalidate();
    }

    /**
     * chart 자료 생성함수
     * @param whichValue 어느 chart 의 자료를 생성
     */
    public void generateChartData(String whichValue) {
        generatedChartData.clear();
        List<WeightInfo> weightData = new ArrayList<>();
        List<WaterInfo> waterData = new ArrayList<>();
        List<WorkoutInfo> exerciseData = new ArrayList<>();

        switch (whichValue) {
            case "weight":
            case "fatRate":
                for (int i = 0; i < data.size(); i++) {
                    weightData.add((WeightInfo) data.get(i));
                }
                break;
            case "water":
                for (int i = 0; i < data.size(); i++) {
                    waterData.add((WaterInfo) data.get(i));
                }
                break;
            case "exercise":
                for (int i = 0; i < data.size(); i++) {
                    exerciseData.add((WorkoutInfo) data.get(i));
                }
                break;
        }

        Calendar day_iterating = Calendar.getInstance();
        if (whichPeriod.equals(PERIOD_WEEK) || whichPeriod.equals(PERIOD_MONTH) || whichPeriod.equals(PERIOD_TOTAL))
            day_iterating = (Calendar) firstDay.clone();
        final Locale locale = Objects.requireNonNull(getContext()).getResources().getConfiguration().locale;
        SimpleDateFormat formatDate = new SimpleDateFormat("MM.dd", locale);
        SimpleDateFormat fakeFormatDate = new SimpleDateFormat("MM-dd", locale);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", locale);
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
                forCount = 5;
                break;
        }

        generatedChartData.put("", null);
        for (int i = 0; i < forCount; i ++) {
            if (whichPeriod.equals(PERIOD_WEEK) || whichPeriod.equals(PERIOD_MONTH)) {
                boolean hasDayData = false;
                label:
                for (int j = 0; j < data.size(); j++) {
                    switch (whichValue) {
                        case "weight":
                        case "fatRate":
                            if (day_iterating.get(Calendar.DAY_OF_MONTH) == weightData.get(j).getMeasureDateTime().getDayOfMonth()) {
                                if (whichValue.equals("weight")) {
                                    //기간이 월단위인 경우 월요일이면 MM.dd형식의 날자를 key로 한 자료 추가, 아니면 MM-dd형식의 날자를 key로 한 자료 추가
                                    //기간이 주단위인 경우에는 MM.dd형식의 날자를 Key로 한 자료 추가
                                    if (whichPeriod.equals(PERIOD_MONTH)) {
                                        if (day_iterating.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                            generatedChartData.put(formatDate.format(day_iterating.getTime()), Float.parseFloat(weightData.get(j).getWeightValue()));
                                        } else {
                                            generatedChartData.put(fakeFormatDate.format(day_iterating.getTime()), Float.parseFloat(weightData.get(j).getWeightValue()));
                                        }
                                    } else {
                                        generatedChartData.put(formatDate.format(day_iterating.getTime()), Float.parseFloat(weightData.get(j).getWeightValue()));
                                    }
                                }
                                else {
                                    //기간이 월단위인 경우 월요일이면 MM.dd형식의 날자를 key로 한 자료 추가, 아니면 MM-dd형식의 날자를 key로 한 자료 추가
                                    //기간이 주단위인 경우에는 MM.dd형식의 날자를 Key로 한 자료 추가
                                    if (whichPeriod.equals(PERIOD_MONTH)) {
                                        if (day_iterating.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                            if (weightData.get(j).getFatRateValue() == null)
                                                generatedChartData.put(formatDate.format(day_iterating.getTime()), null);
                                            else
                                                generatedChartData.put(formatDate.format(day_iterating.getTime()), Float.parseFloat(weightData.get(j).getFatRateValue()));
                                        } else {
                                            if (weightData.get(j).getFatRateValue() == null)
                                                generatedChartData.put(fakeFormatDate.format(day_iterating.getTime()), null);
                                            else
                                                generatedChartData.put(fakeFormatDate.format(day_iterating.getTime()), Float.parseFloat(weightData.get(j).getFatRateValue()));
                                        }
                                    } else {
                                        if (weightData.get(j).getFatRateValue() == null)
                                            generatedChartData.put(formatDate.format(day_iterating.getTime()), null);
                                        else
                                            generatedChartData.put(formatDate.format(day_iterating.getTime()), Float.parseFloat(weightData.get(j).getFatRateValue()));
                                    }
                                }
                                hasDayData = true;
                                break label;
                            }
                            break;
                        case "water":
                            if (day_iterating.get(Calendar.DAY_OF_MONTH) == waterData.get(j).getMeasureDateTime().getDayOfMonth()) {
                                //기간이 월단위인 경우 월요일이면 MM.dd형식의 날자를 key로 한 자료 추가, 아니면 MM-dd형식의 날자를 key로 한 자료 추가
                                //기간이 주단위인 경우에는 MM.dd형식의 날자를 Key로 한 자료 추가
                                if (whichPeriod.equals(PERIOD_MONTH)) {
                                    if (day_iterating.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                        generatedChartData.put(formatDate.format(day_iterating.getTime()), (float) waterData.get(j).getGlasses());
                                    } else {
                                        generatedChartData.put(fakeFormatDate.format(day_iterating.getTime()), (float) waterData.get(j).getGlasses());
                                    }
                                } else {
                                    generatedChartData.put(formatDate.format(day_iterating.getTime()), (float) waterData.get(j).getGlasses());
                                }
                                hasDayData = true;
                                break label;
                            }
                            break;
                        case "exercise":
                            if (day_iterating.get(Calendar.DAY_OF_MONTH) == exerciseData.get(j).getStartTime().getDayOfMonth()) {
                                //기간이 월단위인 경우 월요일이면 MM.dd형식의 날자를 key로 한 자료 추가, 아니면 MM-dd형식의 날자를 key로 한 자료 추가
                                //기간이 주단위인 경우에는 MM.dd형식의 날자를 Key로 한 자료 추가
                                if (whichPeriod.equals(PERIOD_MONTH)) {
                                    if (day_iterating.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                                        generatedChartData.put(formatDate.format(day_iterating.getTime()), exerciseData.get(j).getDistance());
                                    } else {
                                        generatedChartData.put(fakeFormatDate.format(day_iterating.getTime()), exerciseData.get(j).getDistance());
                                    }
                                } else {
                                    generatedChartData.put(formatDate.format(day_iterating.getTime()), exerciseData.get(j).getDistance());
                                }
                                hasDayData = true;
                                break label;
                            }
                            break;
                    }
                }
                if (!hasDayData) {
                    if (whichPeriod.equals(PERIOD_MONTH)) {
                        if (day_iterating.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                            generatedChartData.put(formatDate.format(day_iterating.getTime()), null);
                        } else {
                            generatedChartData.put(fakeFormatDate.format(day_iterating.getTime()), null);
                        }
                    } else {
                        generatedChartData.put(formatDate.format(day_iterating.getTime()), null);
                    }
                }
                if (i != forCount - 1) {
                    day_iterating.add(Calendar.DAY_OF_MONTH, 1);
                }
            } else if (whichPeriod.equals(PERIOD_YEAR)) {
                boolean hasMonthData = false;
                if (whichValue.equals("exercise")) {
                    for (int j = 0; j < data.size(); j++) {
                        if (exerciseData.get(j).getStartTime().getMonthOfYear() == i + 1) {
                            generatedChartData.put(String.valueOf(i + 1), exerciseData.get(j).getDistance());
                            hasMonthData = true;
                            break;
                        }
                    }
                } else {
                    for (int j = 0; j < data.size(); j ++) {
                        if (weightData.get(j).getMeasureDateTime().getMonthOfYear() == i + 1) {
                            if (whichValue.equals("weight")) {
                                generatedChartData.put(String.valueOf(i + 1), Float.parseFloat(weightData.get(j).getWeightValue()));
                            } else {
                                generatedChartData.put(String.valueOf(i + 1), weightData.get(j).getFatRateValue() != null ? Float.parseFloat(weightData.get(j).getFatRateValue()) : null);
                            }
                            hasMonthData = true;
                            break;
                        }
                    }
                }
                if (!hasMonthData) {
                    generatedChartData.put(String.valueOf(i + 1), null);
                }
            } else {
                boolean hasYearData = false;
                for (int j = 0; j < data.size(); j ++) {
                    if (exerciseData.get(j).getStartTime().getYear() == day_iterating.get(Calendar.YEAR)) {
                        generatedChartData.put(yearFormat.format(day_iterating.getTime()), exerciseData.get(j).getDistance());
                        hasYearData = true;
                        break;
                    }
                }
                if (!hasYearData)
                    generatedChartData.put(yearFormat.format(day_iterating.getTime()), null);
                if (i != forCount - 1) {
                    day_iterating.add(Calendar.YEAR, 1);
                }
            }
        }
        generatedChartData.put(" ", null);
    }

    /**
     * 이전단추를 눌렀을때의 처리
     */
    @Override
    public void onPrevClicked() {
        if (whichPeriod.equals(PERIOD_WEEK)) {
            firstDay.add(Calendar.WEEK_OF_YEAR, -1);
            lastDay.add(Calendar.WEEK_OF_YEAR, -1);
        } else if (whichPeriod.equals(PERIOD_MONTH)){
            firstDay.add(Calendar.MONTH, -1);
            lastDay = (Calendar) firstDay.clone();
            lastDay.add(Calendar.DAY_OF_MONTH, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
            lastDay.set(Calendar.HOUR_OF_DAY, 23);
            lastDay.set(Calendar.MINUTE, 59);
            lastDay.set(Calendar.SECOND, 59);
        }
        measureDataLayout.updatePeriod(firstDay, lastDay, whichPeriod.equals(PERIOD_WEEK));
    }

    /**
     * 다음단추를 눌렀을때의 처리
     */
    @Override
    public void onNextClicked() {
        if (whichPeriod.equals(PERIOD_WEEK)) {
            firstDay.add(Calendar.WEEK_OF_YEAR, 1);
            lastDay.add(Calendar.WEEK_OF_YEAR, 1);
        } else if (whichPeriod.equals(PERIOD_MONTH)) {
            firstDay.add(Calendar.MONTH, 1);
            lastDay = (Calendar) firstDay.clone();
            lastDay.add(Calendar.DAY_OF_MONTH, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
            lastDay.set(Calendar.HOUR_OF_DAY, 23);
            lastDay.set(Calendar.MINUTE, 59);
            lastDay.set(Calendar.SECOND, 59);
        }
        measureDataLayout.updatePeriod(firstDay, lastDay, whichPeriod.equals(PERIOD_WEEK));
    }

    /**
     * 몸무게현시화면에서 몸무게를 선택하였을때의 처리
     */
    @Override
    public void onWeightClicked() {
        measureDataLayout.showWeightValue("", "", typeLayout.getWeightSelected(), false);
        generateChartData(typeLayout.getWeightSelected() ? "weight" : "fatRate");
        updateWeightChart("weight");
    }

    /**
     * 몸무게현시화면에서 체지방률을 선택하였을때의 처리
     */
    @Override
    public void onFatRateClicked() {
        measureDataLayout.showWeightValue("", "", typeLayout.getWeightSelected(), false);
        generateChartData(typeLayout.getWeightSelected() ? "weight" : "fatRate");
        updateWeightChart("fatRate");
    }

    /**
     * 현시기간 초기화 함수
     */
    public void initPeriod() {
        if (whichPeriod.equals(PERIOD_WEEK)) {
            firstDay = Calendar.getInstance();
            firstDay.set(Calendar.DAY_OF_WEEK, firstDay.getFirstDayOfWeek());
            firstDay.set(Calendar.HOUR_OF_DAY, 0);
            firstDay.set(Calendar.MINUTE, 0);
            firstDay.set(Calendar.SECOND, 0);
            firstDay.set(Calendar.MILLISECOND, 0);
            lastDay = (Calendar) firstDay.clone();
            lastDay.set(Calendar.HOUR_OF_DAY, 23);
            lastDay.set(Calendar.MINUTE, 59);
            lastDay.set(Calendar.SECOND, 59);
            lastDay.add(Calendar.DAY_OF_MONTH, 6);
        } else if (whichPeriod.equals(PERIOD_MONTH)) {
            firstDay = Calendar.getInstance();
            firstDay.set(Calendar.DAY_OF_MONTH, 1);
            firstDay.set(Calendar.HOUR_OF_DAY, 0);
            firstDay.set(Calendar.MINUTE, 0);
            firstDay.set(Calendar.SECOND, 0);
            lastDay = (Calendar) firstDay.clone();
            lastDay.add(Calendar.DAY_OF_MONTH, firstDay.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
            lastDay.set(Calendar.HOUR_OF_DAY, 23);
            lastDay.set(Calendar.MINUTE, 59);
            lastDay.set(Calendar.SECOND, 59);
        } else {
            firstDay = Calendar.getInstance();
            firstDay.add(Calendar.YEAR, -4);
            firstDay.set(Calendar.MONTH, 0);
            firstDay.set(Calendar.DAY_OF_MONTH, 1);
            firstDay.set(Calendar.HOUR_OF_DAY, 0);
            firstDay.set(Calendar.MINUTE, 0);
            firstDay.set(Calendar.SECOND, 0);
            lastDay = Calendar.getInstance();
            lastDay.set(Calendar.MONTH, 11);
            lastDay.set(Calendar.DAY_OF_MONTH, 31);
            lastDay.set(Calendar.HOUR_OF_DAY, 23);
            lastDay.set(Calendar.MINUTE, 59);
            lastDay.set(Calendar.SECOND, 59);
        }
    }

    public class ArrayListAxisValueFormatter extends IndexAxisValueFormatter {
        private List<String> values;

        public ArrayListAxisValueFormatter(List<String> values) {
            this.values = values;
        }

        @Override
        public String getFormattedValue(float value) {
            if (this.values.size() <= (int) value || (int) value < 0) {
                return "--";
            }
            //날자형식이 MM-dd인 경우 chart x label을 빈 문자렬로 표시
            if (this.values.get((int) value).contains("-"))
                return "";
            return this.values.get((int) value);
        }
    }

    public class DoubleValueFormatter extends ValueFormatter {

        private DecimalFormat mFormat;

        public DoubleValueFormatter(String formatPattern) {
            mFormat = new DecimalFormat(formatPattern);
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            if (value == 0) {
                return "0";
            } else {
                return mFormat.format(value);
            }
        }
    }
}