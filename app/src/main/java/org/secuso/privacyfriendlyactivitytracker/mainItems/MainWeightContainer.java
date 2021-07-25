package org.secuso.privacyfriendlyactivitytracker.mainItems;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 건강화면에 몸무게자료를 현시하는 클라스
 */
public class MainWeightContainer extends MainItemContainer implements View.OnClickListener {
    LineChart weightChart;

    public MainWeightContainer(Context context) {
        this(context, null);
    }

    public MainWeightContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainWeightContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOnClickListener(this);
    }

    /**
     * 몸무게자료현시
     */
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        weightChart = findViewById(R.id.weight_chart);
        weightChart.setTouchEnabled(false);
        weightChart.setPinchZoom(false);

        if (mData.size() > 0) {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_normal_background));
            List<WeightInfo> weightData = new ArrayList<>();
            for (int i = 0; i < mData.size(); i ++)
                weightData.add((WeightInfo)mData.get(i));
            int month = weightData.get(0).getMeasureDateTime().getMonthOfYear();
            int day = weightData.get(0).getMeasureDateTime().getDayOfMonth();
            mDate.setText(getResources().getString(R.string.date_format4, month, day));
            mValue.setText(weightData.get(0).getWeightValue());
            mUnit.setText(getResources().getString(R.string.kilogram));

            ArrayList<Entry> values = new ArrayList<>();
            if (weightData.size() > 1) {
                for (int i = 0; i < weightData.size(); i++) {
                    float val = Float.parseFloat(weightData.get(weightData.size() - i - 1).getWeightValue());
                    values.add(new Entry(i, val));
                }
            } else {
                float val = Float.parseFloat(weightData.get(0).getWeightValue());
                values.add(new Entry(0, val));
                values.add(new Entry(1, val));
            }

            LineDataSet lineDataSet;
            lineDataSet = new LineDataSet(values, "DataSet 1");
            lineDataSet.setDrawCircles(weightData.size() >= 2);
            lineDataSet.setLineWidth(2.0f);
            lineDataSet.setCircleRadius(4);
            lineDataSet.setCircleColor(getResources().getColor(R.color.chart_color));
            lineDataSet.setColor(getResources().getColor(R.color.chart_color));
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.chart_gradient);
            lineDataSet.setFillDrawable(drawable);
            lineDataSet.setDrawFilled(true);

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(lineDataSet); // add the data sets

            // create a data object with the data sets
            LineData data = new LineData(dataSets);
            data.setDrawValues(false);

            // set data
            weightChart.setData(data);
            weightChart.getAxisLeft().setEnabled(false);
            weightChart.getAxisRight().setEnabled(false);
            weightChart.getXAxis().setDrawGridLines(false);
            weightChart.getAxisRight().setDrawGridLines(false);
            weightChart.getXAxis().setEnabled(false);
            weightChart.getLegend().setEnabled(false);
            weightChart.setDescription(null);
            weightChart.invalidate();
        } else {
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_weight_background));
            mDesc.setText(getResources().getString(R.string.main_weight_desc));
        }
    }

    @Override
    public void onClick(View view) {

    }
}
