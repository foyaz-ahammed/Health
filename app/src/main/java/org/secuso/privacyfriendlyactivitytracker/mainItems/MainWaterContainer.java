package org.secuso.privacyfriendlyactivitytracker.mainItems;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 건강화면에 물자료를 현시하는 클라스
 */
public class MainWaterContainer extends MainItemContainer implements View.OnClickListener {

    int target = 8; // 하루 섭취할 물량 (단위는 고뿌)
    List<WaterInfo> waterData = new ArrayList<>();  // 현시할 물자료
    RoundedHorizontalProgressBar waterProgress;

    public MainWaterContainer(Context context) {
        this(context, null);
    }

    public MainWaterContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainWaterContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    /**
     * 물자료현시
     */
    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        waterProgress = findViewById(R.id.water_progress);

        if (mData.size() > 0) {
            mVisualArea.setVisibility(View.VISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_normal_background));
            waterData = new ArrayList<>();
            for (int i = 0; i < mData.size(); i ++)
                waterData.add((WaterInfo) mData.get(i));
            int month = waterData.get(0).getMeasureDateTime().getMonthOfYear();
            int day = waterData.get(0).getMeasureDateTime().getDayOfMonth();
            mDate.setText(getResources().getString(R.string.date_format4, month, day));
            mValue.setText(String.valueOf(waterData.get(0).getGlasses()));
            waterProgress.setMax(target * 250);
            waterProgress.animateProgress(500, 0, waterData.get(0).getGlasses() * 250);
            waterProgress.setProgress(waterData.get(0).getGlasses() * 250);
        } else {
            mVisualArea.setVisibility(View.INVISIBLE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_water_background));
        }
    }

    /**
     * 물목표값 변경하는 함수
     * @param newTarget 새로 갱신된 물목표
     */
    public void changeTarget(int newTarget) {
        target = newTarget > 0 ? newTarget : 8;
        waterProgress.setMax(target * 250);
    }

    @Override
    public void onClick(View view) {

    }
}
