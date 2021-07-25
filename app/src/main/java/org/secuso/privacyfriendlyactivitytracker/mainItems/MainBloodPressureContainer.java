package org.secuso.privacyfriendlyactivitytracker.mainItems;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.TriangularIndicatorBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 건강화면에 혈압자료를 현시하는 클라스
 */
public class MainBloodPressureContainer extends MainItemContainer implements View.OnClickListener {

    public MainBloodPressureContainer(Context context) {
        this(context, null);
    }

    public MainBloodPressureContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainBloodPressureContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 혈압자료현시
     */
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        TriangularIndicatorBar indicatorBar = findViewById(R.id.indicator_bar);

        if (mData.size() > 0) {
            mVisualArea.setVisibility(View.VISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_normal_background));
            List<BloodPressureInfo> bloodPressureData = new ArrayList<>();
            for (int i = 0; i < mData.size(); i ++)
                bloodPressureData.add((BloodPressureInfo)mData.get(i));
            int month = bloodPressureData.get(0).getMeasureDateTime().getMonthOfYear();
            int day = bloodPressureData.get(0).getMeasureDateTime().getDayOfMonth();
            mDate.setText(getResources().getString(R.string.date_format4, month, day));
            mValue.setText(String.valueOf(bloodPressureData.get(0).getSystolicValue()));
            mUnit.setText(getResources().getString(R.string.with_diastolic, bloodPressureData.get(0).getDiastolicValue()));
            if (bloodPressureData.get(0).getLevel(getContext()).get(1).equals("systolic")) {
                indicatorBar.setScore(bloodPressureData.get(0).getSystolicValue(), 200);
            } else {
                indicatorBar.setScore(bloodPressureData.get(0).getDiastolicValue(), 120);
            }
        } else {
            mVisualArea.setVisibility(View.INVISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_blood_pressure_background));
        }
    }

    @Override
    public void onClick(View view) {

    }
}
