package org.secuso.privacyfriendlyactivitytracker.adapters;

import android.content.Context;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.ActivityChart;
import org.secuso.privacyfriendlyactivitytracker.models.ActivityChartDataSet;
import org.secuso.privacyfriendlyactivitytracker.models.ActivityDayChart;
import org.secuso.privacyfriendlyactivitytracker.models.ActivitySummary;
import org.secuso.privacyfriendlyactivitytracker.utils.ChartMarkerView;
import org.secuso.privacyfriendlyactivitytracker.utils.UnitHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 일별, 주별, 월별 걸음수자료를 현시하기 위한 adapter
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {
    private static final int TYPE_SUMMARY = 0;
    private static final int TYPE_DAY_CHART = 1;
    private static final int TYPE_CHART = 2;
    private List<Object> mItems;
    private OnItemClickListener mItemClickListener;

    /**
     * 새 adapter 생성
     *
     * @param items 현시할 자료
     */
    public ReportAdapter(List<Object> items) {
        mItems = items;
    }

    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v;
        ViewHolder vh;
        switch (viewType) {
            case TYPE_CHART:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_activity_bar_chart, parent, false);
                vh = new CombinedChartViewHolder(v);
                break;
            case TYPE_DAY_CHART:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_activity_chart, parent, false);
                vh = new ChartViewHolder(v);
                break;
            case TYPE_SUMMARY:
            default:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.card_activity_summary, parent, false);
                vh = new SummaryViewHolder(v);
                break;
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_CHART:
                ActivityChart barChartData = (ActivityChart) mItems.get(position);
                CombinedChartViewHolder barChartViewHolder = (CombinedChartViewHolder) holder;
                barChartViewHolder.mTitleTextView.setText(barChartData.getTitle());
                int barChartI = 0;
                ArrayList<String> barChartXValues = new ArrayList<>();
                Map<String, Double> barChartDataMap;
                String barChartLabel;
                if (barChartData.getDisplayedDataType() == null) {
                    barChartDataMap = barChartData.getSteps();
                    barChartLabel = barChartViewHolder.context.getString(R.string.steps);
                } else {
                    switch (barChartData.getDisplayedDataType()) {
                        case DISTANCE:
                            barChartDataMap = barChartData.getDistance();
                            barChartLabel = barChartViewHolder.context.getString(R.string.action_distance);
                            break;
                        case CALORIES:
                            barChartDataMap = barChartData.getCalories();
                            barChartLabel = barChartViewHolder.context.getString(R.string.calories);
                            break;
                        case STEPS:
                        default:
                            barChartDataMap = barChartData.getSteps();
                            barChartLabel = barChartViewHolder.context.getString(R.string.steps);
                            break;
                    }
                }
                boolean isWeek = true;
                if (barChartDataMap.size() > 10)
                    isWeek = false;
                List<BarEntry> dataEntries = new ArrayList<>();
                List<BarEntry> dataEntriesReachedDailyGoal = new ArrayList<>();
                for (Map.Entry<String, Double> dataEntry : barChartDataMap.entrySet()) {
                    barChartXValues.add(barChartI, dataEntry.getKey());
                    if (dataEntry.getValue() != null) {
                        float val = dataEntry.getValue().floatValue();
                        if (barChartData.getDisplayedDataType() == ActivityDayChart.DataType.DISTANCE) {
                            val = Double.valueOf(UnitHelper.kilometerToUsersLengthUnit(UnitHelper.metersToKilometers(val), barChartViewHolder.context)).floatValue();
                        }
                        if (dataEntry.getValue() >= barChartData.getGoal() && barChartData.getDisplayedDataType() == ActivityDayChart.DataType.STEPS) {
                            dataEntriesReachedDailyGoal.add(new BarEntry(barChartI, val));
                        } else {
                            dataEntries.add(new BarEntry(barChartI, val));
                        }
                    }
                    barChartI++;
                }
                BarDataSet barDataSet = new BarDataSet(dataEntries, barChartLabel);
                barDataSet.setDrawValues(isWeek);
                BarDataSet barDataSetReachedDailyGoal = new BarDataSet(dataEntriesReachedDailyGoal, barChartLabel);
//                if (barChartData.getDisplayedDataType() == ActivityDayChart.DataType.DISTANCE) {
//                    String formatPattern = "###,###,##0.0";
//                    barDataSet.setValueFormatter(new DoubleValueFormatter(formatPattern));
//                    barDataSetReachedDailyGoal.setValueFormatter(new DoubleValueFormatter(formatPattern));
//                }
                if (barChartData.getDisplayedDataType() == ActivityDayChart.DataType.STEPS && barChartXValues.size() > 15) {
                    barDataSetReachedDailyGoal.setDrawValues(false);
                }
                ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
                if (barChartData.getDisplayedDataType() != ActivityDayChart.DataType.STEPS) {
                    // 첫자료와 마지막자료가 표시
                    Entry start = new Entry(0, 0);
                    Entry end = new Entry(barChartI - 1, 0);
                    LineDataSet chartLineDataSet = new LineDataSet(Arrays.asList(start, end), "");
                    chartLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    chartLineDataSet.setDrawCircles(false);
                    chartLineDataSet.setDrawValues(false);
                    chartLineDataSet.setColor(ContextCompat.getColor(barChartViewHolder.context, R.color.transparent), 0);
                    lineDataSets.add(chartLineDataSet);
                }
                // 일별 걸음수목표 추가
                if (barChartXValues.size() > 0 && barChartData.getDisplayedDataType() == ActivityDayChart.DataType.STEPS) {
                    Entry start = new Entry(0, barChartData.getGoal());
                    Entry end = new Entry(barChartXValues.size() - 1, barChartData.getGoal());
                    LineDataSet chartLineDataSet = new LineDataSet(Arrays.asList(start, end), barChartViewHolder.context.getString(R.string.activity_summary_chart_legend_stepgoal));
                    chartLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    chartLineDataSet.setLineWidth(1);
                    chartLineDataSet.setDrawCircles(false);
                    chartLineDataSet.setColor(ContextCompat.getColor(barChartViewHolder.context, R.color.chart_color), 200);
                    chartLineDataSet.setDrawValues(false);
                    lineDataSets.add(chartLineDataSet);
                }
                CombinedData combinedData = new CombinedData();
                BarData barData = new BarData(barDataSet, barDataSetReachedDailyGoal);
                barData.setBarWidth(0.5f);
                combinedData.setData(barData);
                combinedData.setData(new LineData(lineDataSets));
                barDataSet.setColor(ContextCompat.getColor(barChartViewHolder.context, R.color.chart_color));
                barDataSetReachedDailyGoal.setColor(ContextCompat.getColor(barChartViewHolder.context, R.color.chart_color));

                if (!isWeek) {
                    //그라프를 눌렀을때 marker 표시
                    boolean showFloat = false, isStep = false;
                    if (barChartData.getDisplayedDataType() == ActivityDayChart.DataType.DISTANCE ||
                            barChartData.getDisplayedDataType() == ActivityDayChart.DataType.CALORIES) {
                        showFloat = true;
                        isStep = true;
                    }
                    ChartMarkerView makerView = new ChartMarkerView(barChartViewHolder.context, R.layout.custom_marker_view, showFloat, isStep);
                    makerView.setChartView(barChartViewHolder.mChart);
                    barChartViewHolder.mChart.setMarker(makerView);
                    barChartViewHolder.mChart.setTouchEnabled(true);
                } else {
                    barChartViewHolder.mChart.setTouchEnabled(false);
                }

                barChartViewHolder.mChart.setData(combinedData);
                barChartViewHolder.mChart.getXAxis().setValueFormatter(new ArrayListAxisValueFormatter(barChartXValues));
                //월별그라프인 경우 모든 x label 표시
                if (!isWeek) {
                    barChartViewHolder.mChart.getXAxis().setLabelCount(barChartXValues.size());
                    barChartViewHolder.mChart.getXAxis().setDrawGridLines(false);
                }
                barChartViewHolder.mChart.getLegend().setEnabled(false);
                barChartViewHolder.mChart.getAxisLeft().setAxisMinimum(0f);
                barChartViewHolder.mChart.animateY(500, Easing.EaseInCubic);
                barChartViewHolder.mChart.invalidate();
                break;
            case TYPE_DAY_CHART:
                ActivityDayChart chartData = (ActivityDayChart) mItems.get(position);
                ChartViewHolder chartViewHolder = (ChartViewHolder) holder;
                chartViewHolder.mTitleTextView.setText(chartData.getTitle());

                final ArrayList<String> chartXValues = new ArrayList<>();
                int i = 0;
                Map<String, ActivityChartDataSet> dataMap;
                String label;
                if (chartData.getDisplayedDataType() == null) {
                    dataMap = chartData.getSteps();
                    label = chartViewHolder.context.getString(R.string.steps);
                } else {
                    switch (chartData.getDisplayedDataType()) {
                        case DISTANCE:
                            dataMap = chartData.getDistance();
                            label = chartViewHolder.context.getString(R.string.action_distance);
                            break;
                        case CALORIES:
                            dataMap = chartData.getCalories();
                            label = chartViewHolder.context.getString(R.string.calories);
                            break;
                        case STEPS:
                        default:
                            dataMap = chartData.getSteps();
                            label = chartViewHolder.context.getString(R.string.steps);
                            break;
                    }
                }
                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                float lastValue = 0;
                float maxValue = 0;
                // chart 선 자료 생성
                for (Map.Entry<String, ActivityChartDataSet> dataEntry : dataMap.entrySet()) {
                    if (dataEntry.getValue() != null) {
                        LineDataSet chartLineDataSet = getNewChartLineDataSet(chartViewHolder.context, label);
                        chartLineDataSet.setDrawCircles(false);
                        chartLineDataSet.setDrawCircleHole(true);
                        chartLineDataSet.setCircleColor(ContextCompat.getColor(chartViewHolder.context, R.color.chart_color));
                        chartLineDataSet.setColor(ContextCompat.getColor(chartViewHolder.context, R.color.chart_color));
                        chartLineDataSet.setHighLightColor(ContextCompat.getColor(chartViewHolder.context, R.color.chart_color));

                        if (dataEntry.getValue().getStepCount() != null && dataEntry.getValue().getStepCount().getWalkingMode() != null) {
                            int alpha = 85;
                            chartLineDataSet.setFillAlpha(alpha);
                            chartLineDataSet.setDrawFilled(true);
                            chartLineDataSet.setFillFormatter(new IFillFormatter() {
                                @Override
                                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                                    return 0;
                                }
                            });
                            chartLineDataSet.setFillDrawable(ContextCompat.getDrawable(chartViewHolder.context, R.drawable.chart_gradient));
                            chartLineDataSet.setDrawCircles(true);
                        }
                        dataSets.add(chartLineDataSet);
                        // chart 자료 추가
                        float val = Double.valueOf(dataEntry.getValue().getValue()).floatValue();
                        if (chartData.getDisplayedDataType() == ActivityDayChart.DataType.DISTANCE) {
                            val = Double.valueOf(UnitHelper.kilometerToUsersLengthUnit(UnitHelper.metersToKilometers(val), chartViewHolder.context)).floatValue();
                        }
                        if(lastValue > val){
                            Log.i("REPORT_ADAPTER", "lastvalue > val, using lastvalue");
                            val = lastValue;
                        }
                        Entry prevChartEntry, chartEntry;
                        if (i == 0) {
                            prevChartEntry = new Entry(0, 0);
                        }else{
                            prevChartEntry = new Entry(i - 1, lastValue);
                        }
                        chartEntry = new Entry(i++, val);
                        ((LineDataSet) dataSets.get(dataSets.size() - 1)).getValues().add(prevChartEntry);
                        ((LineDataSet) dataSets.get(dataSets.size() - 1)).getValues().add(chartEntry);
                        // remember variables
                        lastValue = val;
                        maxValue = Math.max(maxValue, val);
                    }
                    // x 값 추가
                    chartXValues.add(dataEntry.getKey());
                }
                // 일별 걸음수목표 추가
                if (chartXValues.size() > 0 && chartData.getDisplayedDataType() == ActivityDayChart.DataType.STEPS) {
                    Entry start = new Entry(0, chartData.getGoal());
                    Entry end = new Entry(chartXValues.size() - 1, chartData.getGoal());
                    LineDataSet chartLineDataSet = new LineDataSet(Arrays.asList(start, end), "");
                    chartLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    chartLineDataSet.setLineWidth(1f);
                    chartLineDataSet.setDrawCircles(false);
                    chartLineDataSet.setColor(ContextCompat.getColor(chartViewHolder.context, R.color.chart_color), 200);
                    chartLineDataSet.setDrawValues(false);
                    chartLineDataSet.setHighLightColor(ContextCompat.getColor(chartViewHolder.context, R.color.chart_color));
                    maxValue = Math.max(maxValue, chartData.getGoal());
                    dataSets.add(chartLineDataSet);
                }
                // Workaround since scaling does not work correctly in MPAndroidChart v3.0.0.0-beta1
                {
                    Entry start = new Entry(0, 0);
                    Entry end = new Entry(chartXValues.size() - 1, maxValue * 1.03f);
                    LineDataSet chartLineDataSet = new LineDataSet(Arrays.asList(start, end), "");
                    chartLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                    chartLineDataSet.setDrawCircles(false);
                    chartLineDataSet.setHighlightEnabled(false);
                    chartLineDataSet.setColor(ContextCompat.getColor(chartViewHolder.context, R.color.transparent), 0);
                    chartLineDataSet.setDrawValues(false);
                    dataSets.add(chartLineDataSet);
                }
                LineData data = new LineData(dataSets);
                chartViewHolder.mChart.setData(data);
                chartViewHolder.mChart.getXAxis().setValueFormatter(new ArrayListAxisValueFormatter(chartXValues));
                chartViewHolder.mChart.getLegend().setEnabled(false);

                //일별 그라프를 눌렀을때 marker 표시
                boolean isFloat = false, isStep = false;
                if (chartData.getDisplayedDataType() == ActivityDayChart.DataType.DISTANCE ||
                        chartData.getDisplayedDataType() == ActivityDayChart.DataType.CALORIES) {
                    isFloat = true;
                    isStep = true;
                }
                ChartMarkerView mv = new ChartMarkerView(chartViewHolder.context, R.layout.custom_marker_view, isFloat, isStep);
                mv.setChartView(chartViewHolder.mChart);
                chartViewHolder.mChart.setMarker(mv);

                YAxis yAxis = chartViewHolder.mChart.getAxisLeft();
                yAxis.setAxisMinimum(0f);

                // invalidate
                chartViewHolder.mChart.getData().notifyDataChanged();
                chartViewHolder.mChart.notifyDataSetChanged();
                chartViewHolder.mChart.invalidate();
            break;
            case TYPE_SUMMARY:
                ActivitySummary summaryData = (ActivitySummary) mItems.get(position);
                SummaryViewHolder summaryViewHolder = (SummaryViewHolder) holder;
                UnitHelper.FormattedUnitPair distance = UnitHelper.formatKilometers(UnitHelper.metersToKilometers(summaryData.getDistance()), summaryViewHolder.itemView.getContext());
                UnitHelper.FormattedUnitPair calories = UnitHelper.formatCalories(summaryData.getCalories(), summaryViewHolder.itemView.getContext());
                summaryViewHolder.mTitleTextView.setText(summaryData.getTitle());
                summaryViewHolder.mStepsTextView.setText(String.valueOf(summaryData.getSteps()));
                summaryViewHolder.mDistanceTextView.setText(distance.getValue());
                summaryViewHolder.mDistanceTitleTextView.setText(distance.getUnit());
                summaryViewHolder.mCaloriesTextView.setText(calories.getValue());
                summaryViewHolder.mCaloriesTitleTextView.setText(calories.getUnit());
                summaryViewHolder.mNextButton.setVisibility(summaryData.isHasSuccessor() ? View.VISIBLE : View.INVISIBLE);
                summaryViewHolder.mPrevButton.setVisibility(summaryData.isHasPredecessor() ? View.VISIBLE : View.INVISIBLE);
                if(summaryData.getCurrentSpeed() != null){
                    summaryViewHolder.mVelocityContainer.setVisibility(View.VISIBLE);
                    summaryViewHolder.mVelocityTextView.setText(String.valueOf(UnitHelper.formatKilometersPerHour(UnitHelper.metersPerSecondToKilometersPerHour(summaryData.getCurrentSpeed()), summaryViewHolder.context)));
                }else{
                    summaryViewHolder.mVelocityContainer.setVisibility(View.GONE);
                }
                break;
        }
    }

    //새 chartlinedata 얻는 함수
    private LineDataSet getNewChartLineDataSet(Context context, String label){
        LineDataSet chartLineDataSet = new LineDataSet(new ArrayList<Entry>(), label);
        chartLineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        chartLineDataSet.setLineWidth(1);
        chartLineDataSet.setCircleRadius(3.5f);
        chartLineDataSet.setDrawCircleHole(false);
        chartLineDataSet.setColor(ContextCompat.getColor(context, R.color.colorPrimary), 200);
        chartLineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.colorPrimary));
        chartLineDataSet.setDrawValues(false);

        return chartLineDataSet;
    }

    //자료개수를 돌려주는 함수
    @Override
    public int getItemCount() {
        return (mItems != null) ? mItems.size() : 0;
    }

    // view type얻기
    @Override
    public int getItemViewType(int position) {
        Object item = mItems.get(position);
        if (item instanceof ActivityDayChart) {
            return TYPE_DAY_CHART;
        } else if (item instanceof ActivitySummary) {
            return TYPE_SUMMARY;
        } else if (item instanceof ActivityChart) {
            return TYPE_CHART;
        } else {
            return -1;
        }
    }

    //adapter 항목 click listener
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public interface OnItemClickListener {
        void onActivityChartDataTypeClicked(ActivityDayChart.DataType newDataType);

        void setActivityChartDataTypeChecked(Menu popup);

        void onPrevClicked();

        void onNextClicked();

        void onTitleClicked();

        void inflateWalkingModeMenu(Menu menu);

        void onWalkingModeClicked(int id);
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        public Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
        }
    }

    //summary부분 현시하는 viewHolder
    public class SummaryViewHolder extends ViewHolder implements PopupMenu.OnMenuItemClickListener {

        public TextView mTitleTextView;
        public TextView mStepsTextView;
        public TextView mDistanceTextView;
        public TextView mCaloriesTextView;
        public TextView mVelocityTextView;
        public TextView mDistanceTitleTextView;
        public TextView mCaloriesTitleTextView;
        public RelativeLayout mVelocityContainer;
        public ImageButton mPrevButton;
        public ImageButton mNextButton;
        public ImageButton mMenuButton;

        public SummaryViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.period);
            mStepsTextView = (TextView) itemView.findViewById(R.id.stepCount);
            mDistanceTextView = (TextView) itemView.findViewById(R.id.distanceCount);
            mCaloriesTextView = (TextView) itemView.findViewById(R.id.calorieCount);
            mVelocityTextView = (TextView) itemView.findViewById(R.id.speed);
            mVelocityContainer = (RelativeLayout) itemView.findViewById(R.id.speedContainer);
            mDistanceTitleTextView = (TextView) itemView.findViewById(R.id.distanceTitle);
            mCaloriesTitleTextView = (TextView) itemView.findViewById(R.id.calorieTitle);
            mPrevButton = (ImageButton) itemView.findViewById(R.id.prev_btn);
            mNextButton = (ImageButton) itemView.findViewById(R.id.next_btn);
            mMenuButton = (ImageButton) itemView.findViewById(R.id.periodMoreButton);

            mMenuButton.setOnClickListener(v -> showPopup(mMenuButton, context));

            mPrevButton.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onPrevClicked();
            }
            });
            mNextButton.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onNextClicked();
            }
            });
            mTitleTextView.setOnClickListener(v -> {
            if (mItemClickListener != null) {
                mItemClickListener.onTitleClicked();
            }
            });
        }

        //popup창 현시
        public void showPopup(View v, Context c) {
            Context wrapper = new ContextThemeWrapper(c, R.style.PopupStyle);
            PopupMenu popup = new PopupMenu(wrapper, v);
            if (mItemClickListener != null) {
                mItemClickListener.inflateWalkingModeMenu(popup.getMenu());
            }
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            item.setChecked(!item.isChecked());
            if (mItemClickListener != null) {
                mItemClickListener.onWalkingModeClicked(item.getItemId());
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 일별, 주별, 월별 chart 자료 현시하는 기초 viewholder
     */
    public abstract class AbstractChartViewHolder extends ViewHolder implements PopupMenu.OnMenuItemClickListener {

        public TextView mTitleTextView;
        public ImageButton mMenuButton;

        public AbstractChartViewHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.period);
            mMenuButton = (ImageButton) itemView.findViewById(R.id.periodMoreButton);
            mMenuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPopup(mMenuButton, context);
                }
            });
        }

        //popup창 현시
        public void showPopup(View v, Context c) {
            Context wrapper = new ContextThemeWrapper(c, R.style.PopupStyle);
            PopupMenu popup = new PopupMenu(wrapper, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.menu_card_activity_summary, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            if (mItemClickListener != null) {
                mItemClickListener.setActivityChartDataTypeChecked(popup.getMenu());
            }
            popup.show();
        }

        //개별적 menu item을 눌렀을때 호출되는 callback
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            ActivityDayChart.DataType dataType;
            item.setChecked(!item.isChecked());

            switch (item.getItemId()) {
                case R.id.menu_steps:
                    dataType = ActivityDayChart.DataType.STEPS;
                    break;
                case R.id.menu_distance:
                    dataType = ActivityDayChart.DataType.DISTANCE;
                    break;
                case R.id.menu_calories:
                    dataType = ActivityDayChart.DataType.CALORIES;
                    break;
                default:
                    return false;
            }
            if (mItemClickListener != null) {
                mItemClickListener.onActivityChartDataTypeClicked(dataType);
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * 일별, 주별, 월별 걸음수자료 현시하는 viewholder
     */
    public class ChartViewHolder extends AbstractChartViewHolder {
        public LineChart mChart;

        public ChartViewHolder(View itemView) {
            super(itemView);
            mChart = (LineChart) itemView.findViewById(R.id.chart);
            mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            mChart.getAxisRight().setEnabled(false);
            mChart.setTouchEnabled(true);
            mChart.setDoubleTapToZoomEnabled(false);
            mChart.setPinchZoom(false);
            mChart.setDescription(null);
        }
    }

    /**
     * 주별, 월별 걸음수 자료를 현시하는 viewHolder
     */
    public class CombinedChartViewHolder extends AbstractChartViewHolder {
        public CombinedChart mChart;

        public CombinedChartViewHolder(View itemView) {
            super(itemView);
            mChart = (CombinedChart) itemView.findViewById(R.id.chart);
            mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            mChart.getAxisRight().setEnabled(false);
            mChart.setTouchEnabled(true);
            mChart.setDoubleTapToZoomEnabled(false);
            mChart.setPinchZoom(false);
            mChart.setDescription(null);
            mChart.setDrawOrder(new CombinedChart.DrawOrder[]{
                    CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.BUBBLE, CombinedChart.DrawOrder.CANDLE, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
            });
        }
    }

    /**
     * 일별 걸음수자료를 현시하는 클라스
     */
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
}