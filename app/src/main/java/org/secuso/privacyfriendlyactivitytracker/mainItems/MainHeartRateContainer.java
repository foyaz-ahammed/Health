package org.secuso.privacyfriendlyactivitytracker.mainItems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRate;
import org.secuso.privacyfriendlyactivitytracker.utils.TriangularIndicatorBar;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HeartRateViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 건강화면에 심박수자료를 현시하는 클라스
 */
public class MainHeartRateContainer extends MainItemContainer implements View.OnClickListener {
    LineChart heartRateChart;
    List<List<HeartRate>> data = new ArrayList<>();

    public MainHeartRateContainer(Context context) {
        this(context, null);
    }

    public MainHeartRateContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainHeartRateContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 심박수자료현시
     */
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        heartRateChart = findViewById(R.id.heart_rate_chart);
        heartRateChart.setTouchEnabled(false);
        heartRateChart.setPinchZoom(false);

        if (data.size() > 0) {
            mVisualArea.setVisibility(View.VISIBLE);
            mDesc.setVisibility(View.GONE);
            mDate.setVisibility(View.VISIBLE);
            mValueArea.setVisibility(View.VISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_normal_background));

            HeartRate latestData = data.get(data.size() - 1).get(data.get(data.size() - 1).size() - 1);
            DateTime dateTime = new DateTime(latestData.getMeasureTime());
            mDate.setText(getResources().getString(R.string.date_format4, dateTime.getMonthOfYear(), dateTime.getDayOfMonth()));
            mValue.setText(String.valueOf(latestData.getPulseValue()));

            ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
            int barXI = 0;
            for (int i = 0; i < data.size(); i ++) {
                List<Entry> dataEntries = new ArrayList<>();
                for (int j = 0; j < data.get(i).size(); j ++) {
                    dataEntries.add(new Entry(barXI, data.get(i).get(j).getPulseValue()));
                    barXI ++;
                }

                LineDataSet lineDataSet;
                lineDataSet = new LineDataSet(dataEntries, "DataSet 1");
                lineDataSet.setLineWidth(2.0f);
                lineDataSet.setCircleRadius(2);
                lineDataSet.setDrawCircleHole(false);
                if (dataEntries.size() != 1)
                    lineDataSet.setDrawCircles(false);
                lineDataSet.setCircleColor(getResources().getColor(R.color.heart_rate_line_chart_color));
                lineDataSet.setColor(getResources().getColor(R.color.heart_rate_line_chart_color));
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.heart_rate_chart_gradient);
                lineDataSet.setFillDrawable(drawable);
                lineDataSet.setDrawFilled(true);
                lineDataSets.add(lineDataSet);
            }

            LineData lineData = new LineData(lineDataSets);
            lineData.setDrawValues(false);
            heartRateChart.setData(lineData);
            heartRateChart.getAxisLeft().setEnabled(false);
            heartRateChart.getAxisRight().setEnabled(false);
            heartRateChart.getXAxis().setDrawGridLines(false);
            heartRateChart.getAxisRight().setDrawGridLines(false);
            heartRateChart.getXAxis().setEnabled(false);
            heartRateChart.getLegend().setEnabled(false);
            heartRateChart.setDescription(null);
            heartRateChart.invalidate();

        } else {
            mDesc.setVisibility(View.VISIBLE);
            mDate.setVisibility(View.GONE);
            mValueArea.setVisibility(View.GONE);
            mVisualArea.setVisibility(View.INVISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_heart_rate_background));
        }
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * 자료갱신
     * @param data 새로 현시할 자료
     */
    public void updateData(List<List<HeartRate>> data) {
        this.data = data;
        onFinishInflate();
    }
}
