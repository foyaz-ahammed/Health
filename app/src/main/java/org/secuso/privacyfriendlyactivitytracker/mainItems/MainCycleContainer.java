package org.secuso.privacyfriendlyactivitytracker.mainItems;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;
import org.secuso.privacyfriendlyactivitytracker.utils.MainCycleView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 건강화면에 생리자료 현시하는 클라스
 */
public class MainCycleContainer extends LinearLayout {
    TextView mDesc, mDate, mValue;
    LinearLayout mVisualArea;
    MainCycleView mCircleView;

    public MainCycleContainer(Context context) {
        this(context, null);
    }

    public MainCycleContainer(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainCycleContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        mDesc = findViewById(R.id.desc);
        mDate = findViewById(R.id.date);
        mValue = findViewById(R.id.value);
        mVisualArea = findViewById(R.id.visual_area);
        mCircleView = findViewById(R.id.circle_calendar);
    }

    /**
     * 생리자료현시하는 함수
     * @param cycleData 새로 받은 생리자료
     */
    public void setData(Ovulation cycleData) {
        if (cycleData != null) {
            mDesc.setVisibility(View.GONE);
            mDate.setVisibility(View.VISIBLE);
            mVisualArea.setVisibility(View.VISIBLE);
            mValue.setVisibility(View.VISIBLE);
            Calendar today = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd", Locale.getDefault());
            mDate.setText(simpleDateFormat.format(today.getTime()));
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_normal_background));
            mCircleView.updateData(cycleData);
        } else {
            mDesc.setVisibility(View.VISIBLE);
            mDate.setVisibility(View.GONE);
            mVisualArea.setVisibility(View.GONE);
            mValue.setVisibility(View.GONE);
            setBackground(ContextCompat.getDrawable(getContext(), R.drawable.main_cycle_background));
        }
    }

    //현재날자에 준하여 상태표시
    public void showValue(Ovulation cycleData, int cycleLength) {
        if (cycleData != null) {
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);
            int currentDate = Utils.getIntDate(today);
            int index = 0, diffDays = 0;
            String fullText, subText;
            if (currentDate >= cycleData.periodStart && currentDate <= cycleData.periodEnd) {
                diffDays = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.periodStart), today) + 1;
                fullText = getResources().getString( cycleData.getIsPredict() == 1 ?
                                R.string.main_predicted_period_day_status : R.string.main_period_day_status, diffDays);
            } else {
                if (cycleData.fertileStart > 0) {
                    if (currentDate < cycleData.fertileStart) {
                        diffDays = Utils.getDiffDays(today, Utils.convertIntDateToCalendar(cycleData.fertileStart));
                        if (diffDays == 1) {
                            fullText = getResources().getString(R.string.main_fertile_closer_status_one);
                        } else {
                            fullText = getResources().getString(R.string.main_fertile_closer_status, diffDays);
                        }
                    } else if (currentDate <= cycleData.fertileEnd) {
                        diffDays = Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.fertileStart), today) + 1;
                        fullText = getResources().getString(R.string.main_fertile_day_status, diffDays);
                    } else {
                        diffDays = cycleLength - Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.periodStart), today);
                        fullText = diffDays == 1 ? getResources().getString(R.string.main_predicted_period_closer_status_one) :
                                getResources().getString(R.string.main_predicted_period_closer_status, diffDays);
                    }
                } else {
                    diffDays = cycleLength - Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.periodStart), today);
                    fullText = diffDays == 1 ? getResources().getString(R.string.main_predicted_period_closer_status_one) :
                            getResources().getString(R.string.main_predicted_period_closer_status, diffDays);
                }
            }
            subText = String.valueOf(diffDays);
            index = fullText.indexOf(subText);

            Spannable str = new SpannableString(fullText);
            str.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.main_value_text_size)),
                    index, index + subText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            str.setSpan(new ForegroundColorSpan(Color.BLACK), index, index + subText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mValue.setText(str);
        }
    }

    /**
     * 생리주기기간 갱신하는 함수
     * @param cycleLength 새로받은 생리주기기간
     */
    public void setCycleLength(int cycleLength) {
        mCircleView.updateCycleLength(cycleLength);
    }
}
