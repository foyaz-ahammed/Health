package org.secuso.privacyfriendlyactivitytracker.layout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 몸무게 및 체지방률 변경사항과 최소 최대값을 현시하기 위한 layout
 */
public class TypeLayout extends LinearLayout {
    TextView weightTitle;
    TextView fatRateTitle;
    TextView weightRange;
    TextView weightUnit;
    TextView weightEmptyView;
    LinearLayout weightRangeArea;
    TextView fatRateRange;
    TextView fatRateUnit;
    TextView fatRateEmptyView;
    LinearLayout fatRateRangeArea;
    LinearLayout weightSelectArea;
    LinearLayout fatRateSelectArea;
    TextView weightDiffView;
    TextView fatRateDiffView;

    float maxWeightValue, minWeightValue, maxFatRateValue, minFatRateValue;
    private OnViewClickListener mViewClickListener;
    boolean isWeightSelected = true;

    public TypeLayout(Context context) {
        super(context);
    }

    public TypeLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TypeLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        weightTitle = findViewById(R.id.weight_title);
        fatRateTitle = findViewById(R.id.fat_rate_title);
        weightRangeArea = findViewById(R.id.weight_range_area);
        weightRange = findViewById(R.id.weight_range);
        weightEmptyView = findViewById(R.id.weight_empty_view);
        fatRateRangeArea = findViewById(R.id.fat_rate_range_area);
        fatRateRange = findViewById(R.id.fat_rate_range);
        fatRateEmptyView = findViewById(R.id.fat_rate_empty_view);
        weightSelectArea = findViewById(R.id.weight_select_area);
        weightUnit = findViewById(R.id.weight_unit);
        fatRateUnit = findViewById(R.id.fat_rate_unit);
        fatRateSelectArea = findViewById(R.id.fat_rate_select_area);
        weightDiffView = findViewById(R.id.weight_diff);
        fatRateDiffView = findViewById(R.id.fat_rate_diff);

        weightSelectArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTypeClicked(true);
            }
        });

        fatRateSelectArea.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                changeTypeClicked(false);
            }
        });

    }

    /**
     * 몸무게 및 체지방률을 눌렀을때 배경 및 글자색상, 그라프갱신을 위한 함수
     * @param isWeightSelected true 이면 몸무게선택, false 이면 체지방률선택
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    public void changeTypeClicked(boolean isWeightSelected) {
        this.isWeightSelected = isWeightSelected;

        weightSelectArea.setClickable(!isWeightSelected);
        fatRateSelectArea.setClickable(isWeightSelected);
        weightSelectArea.setBackground(isWeightSelected ? getResources().getDrawable(R.drawable.shadow_background) :
                getResources().getDrawable(R.drawable.shadow_white_background));
        fatRateSelectArea.setBackground(isWeightSelected ? getResources().getDrawable(R.drawable.shadow_white_background) :
                getResources().getDrawable(R.drawable.shadow_background));
        weightTitle.setTextColor(isWeightSelected? Color.WHITE : Color.BLACK);
        weightRange.setTextColor(isWeightSelected? Color.WHITE : Color.BLACK);
        weightUnit.setTextColor(isWeightSelected? Color.WHITE : Color.BLACK);
        weightEmptyView.setTextColor(isWeightSelected? Color.WHITE : Color.BLACK);
        fatRateTitle.setTextColor(isWeightSelected? Color.BLACK : Color.WHITE);
        fatRateRange.setTextColor(isWeightSelected? Color.BLACK : Color.WHITE);
        fatRateUnit.setTextColor(isWeightSelected? Color.BLACK : Color.WHITE);
        fatRateEmptyView.setTextColor(isWeightSelected? Color.BLACK : Color.WHITE);

        if (mViewClickListener != null) {
            if (isWeightSelected) {
                mViewClickListener.onWeightClicked();
            } else {
                mViewClickListener.onFatRateClicked();
            }
        }
    }

    /**
     * 주어진 자료에 기초하여 몸무게 및 체지방률의 최대최소값을 현시하는 함수
     * @param data 자료목록
     */
    @SuppressLint({"StringFormatInvalid", "DefaultLocale"})
    public void setMinMaxValue(List<WeightInfo> data) {
        float weightDiff = 0;
        float fatRateDiff = 0;
        List<String> fatRateData = new ArrayList<>();
        for (int i = 0; i < data.size(); i ++) {
            if (data.get(i).getFatRateValue() != null)
                fatRateData.add(data.get(i).getFatRateValue());
        }

        if (data.size() > 0) {
            weightRangeArea.setVisibility(View.VISIBLE);
            weightEmptyView.setVisibility(View.GONE);
            fatRateRangeArea.setVisibility(View.VISIBLE);
            fatRateEmptyView.setVisibility(View.GONE);

            if (data.size() > 1) {
                weightDiff = Float.parseFloat(data.get(data.size() - 1).getWeightValue()) - Float.parseFloat(data.get(data.size() - 2).getWeightValue());
            }

            if (fatRateData.size() > 0) {
                fatRateRangeArea.setVisibility(View.VISIBLE);
                fatRateEmptyView.setVisibility(View.GONE);

                if (fatRateData.size() > 1) {
                    fatRateDiff = Float.parseFloat(fatRateData.get(fatRateData.size() - 1)) - Float.parseFloat(fatRateData.get(fatRateData.size() - 2));
                }

                minFatRateValue = maxFatRateValue = Float.parseFloat(fatRateData.get(0));
                for (int i = 1; i < fatRateData.size(); i++) {
                    float weightValue = Float.parseFloat(fatRateData.get(i));
                    if (maxFatRateValue < weightValue)
                        maxFatRateValue = weightValue;
                    if (minFatRateValue > weightValue)
                        minFatRateValue = weightValue;
                }
            } else {
                fatRateRangeArea.setVisibility(View.GONE);
                fatRateEmptyView.setVisibility(View.VISIBLE);
            }

            minWeightValue = maxWeightValue = Float.parseFloat(data.get(0).getWeightValue());
            for (int i = 1; i < data.size(); i++) {
                float weightValue = Float.parseFloat(data.get(i).getWeightValue());
                if (maxWeightValue < weightValue)
                    maxWeightValue = weightValue;
                if (minWeightValue > weightValue)
                    minWeightValue = weightValue;
            }

            weightRange.setText(data.size() > 1 ? getResources().getString(R.string.min_max, minWeightValue, maxWeightValue) :
                    String.format("%.1f", minWeightValue));
            fatRateRange.setText(fatRateData.size() > 1 ? getResources().getString(R.string.min_max, minFatRateValue, maxFatRateValue) :
                    String.format("%.1f", minFatRateValue));
        } else {
            weightRangeArea.setVisibility(View.GONE);
            weightEmptyView.setVisibility(View.VISIBLE);
            fatRateRangeArea.setVisibility(View.GONE);
            fatRateEmptyView.setVisibility(View.VISIBLE);
        }

        weightDiffView.setText(data.size() > 1 ? (weightDiff > 0 ? "+" : "") + getResources().getString(R.string.with_kilogram, weightDiff) : getResources().getString(R.string.empty_value));
        fatRateDiffView.setText(fatRateData.size() > 1 ? (fatRateDiff > 0 ? "+" : "") + getResources().getString(R.string.with_percent, fatRateDiff) : getResources().getString(R.string.empty_value));
    }

    /**
     * 몸무게선택여부를 얻는 함수
     * @return true 이면 몸무게선택, false 이면 체지방률선택
     */
    public boolean getWeightSelected() {
        return isWeightSelected;
    }

    public void setOnViewClickListener(final OnViewClickListener onViewClickListener) {
        this.mViewClickListener = onViewClickListener;
    }

    public interface OnViewClickListener {
        void onWeightClicked();

        void onFatRateClicked();
    }

}
