package org.secuso.privacyfriendlyactivitytracker.water;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 평균물량상태를 보여주는 layout
 */
public class WaterAverageLayout extends LinearLayout {
    TextView mGlasses;
    TextView mTarget;
    TextView mAmount;
    TextView mNoDataDesc;
    LinearLayout mAverageArea;

    public WaterAverageLayout(Context context) {
        super(context);
    }

    public WaterAverageLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WaterAverageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mGlasses = findViewById(R.id.glasses);
        mTarget = findViewById(R.id.target);
        mAmount = findViewById(R.id.amount);
        mNoDataDesc = findViewById(R.id.no_data_desc);
        mAverageArea = findViewById(R.id.average_area);
    }

    /**
     * 평균물량을 보여주는 함수
     * @param data 평균물량을 위한 자료
     */
    @SuppressLint("DefaultLocale")
    public void setAverage(List<WaterInfo> data) {
        if (data.size() > 0) {
            mAverageArea.setVisibility(View.VISIBLE);
            mNoDataDesc.setVisibility(View.GONE);

            List<Float> waterData = new ArrayList<>();
            for (int i = 0; i < data.size(); i ++) {
                waterData.add((float) data.get(i).getGlasses());
            }
            float average = Utils.getAverageValue(waterData);
            if (average > (int) average) {
                mGlasses.setText(String.format("%.1f", average));
            } else {
                mGlasses.setText(String.valueOf((int) average));
            }

            mAmount.setText(getResources().getString(R.string.water_format, (int) (average * 250)));
        } else {
            mAverageArea.setVisibility(View.GONE);
            mNoDataDesc.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 목표보임상태 변경함수
     * @param isShow true이면
     * @param target
     */
    public void changeTargetVisibility(boolean isShow, int target) {
        if (isShow) {
            mTarget.setVisibility(View.VISIBLE);
            mTarget.setText(getResources().getString(R.string.target_format, target));
        } else {
            mTarget.setVisibility(View.GONE);
        }
    }
}
