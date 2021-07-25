package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.akexorcist.roundcornerprogressbar.TextRoundCornerProgressBar;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 생리통계화면
 */
public class CycleStatsActivity extends ToolbarActivity {
    TextView mAveragePeriod;
    TextView mAverageCycle;
    LinearLayout mStats;
    CycleLengthShowLayout mProgressArea;
    TextView mFirstPeriodItem;
    TextView mAverageCycleLength;
    LinearLayout mEmptyContent;

    CycleViewModel cycleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cycle_stats);
        super.onCreate(savedInstanceState);

        mAveragePeriod = findViewById(R.id.average_period);
        mAverageCycle = findViewById(R.id.average_cycle);
        mStats = findViewById(R.id.stats);
        mProgressArea = findViewById(R.id.progress_area);
        mFirstPeriodItem = findViewById(R.id.first_period_item);
        mAverageCycleLength = findViewById(R.id.average_cycle_length);
        mEmptyContent = findViewById(R.id.empty_content);

        cycleViewModel = new ViewModelProvider(this).get(CycleViewModel.class);

        //생리상황을 보여주기 위해 한개의 예상자료와 6개의 실지생리자료를 얻기
        cycleViewModel.getOvulationDataForStats().observe(this, data -> {
            if (data.size() > 0) {
                mStats.setVisibility(View.VISIBLE);
                mEmptyContent.setVisibility(View.GONE);
                addStats(data);
            } else {
                mStats.setVisibility(View.GONE);
                mEmptyContent.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * 통계추가함수
     * @param data 통계자료
     */
    public void addStats(List<Ovulation> data) {
        List<Ovulation> realCycleData = data.subList(1, data.size());
        List<Integer> cycleLengths = new ArrayList<>();
        int maxCycleLength, avgPeriodLength, avgCycleLength, periodSum = 0, cycleSum = 0;

        //얻은 자료에 기초하여 생리길이목록 얻기
        for (int i = 0; i < data.size() - 1; i ++) {
            cycleLengths.add(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(i + 1).getPeriodStart()), Utils.convertIntDateToCalendar(data.get(i).getPeriodStart())) + 1);
        }
        maxCycleLength = cycleLengths.get(0);
        for (int i = 0; i < cycleLengths.size(); i ++) {
            periodSum += Utils.getDiffDays(Utils.convertIntDateToCalendar(realCycleData.get(i).getPeriodStart()),
                    Utils.convertIntDateToCalendar(realCycleData.get(i).getPeriodEnd())) + 1;
            cycleSum += cycleLengths.get(i);
            if (cycleLengths.get(i) > maxCycleLength)
                maxCycleLength = cycleLengths.get(i);
        }
        avgPeriodLength = (int) (periodSum / cycleLengths.size());
        avgCycleLength = (int) (cycleSum / cycleLengths.size());

        mAveragePeriod.setText(String.valueOf(avgPeriodLength));
        mAverageCycle.setText(String.valueOf(avgCycleLength));

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        for (int i = 0; i < data.size() - 1; i ++) {
            TextView periodShow = (TextView) View.inflate(this, R.layout.period_show_view, null);
            Calendar cycleStart = Utils.convertIntDateToCalendar(data.get(i + 1).getPeriodStart());
            Calendar cycleEnd = Utils.convertIntDateToCalendar(data.get(i).getPeriodStart());
            cycleEnd.add(Calendar.DAY_OF_MONTH, -1);
            String txt = dateFormat.format(new Date(cycleStart.getTimeInMillis())) + " - " +
                    dateFormat.format(new Date(cycleEnd.getTimeInMillis()));

            //생리주기 textView 추가 및 progress view 순차적으로 추가
            if (i == 0) {
                mFirstPeriodItem.setText(txt);
                mAverageCycleLength.setText(getResources().getString(R.string.average_cycle_length, avgCycleLength));
            } else {
                periodShow.setText(txt);
                mProgressArea.addView(periodShow);
            }

            TextRoundCornerProgressBar cycleShow = (TextRoundCornerProgressBar) View.inflate(this, R.layout.cycle_progress_view, null);
            cycleShow.setLayoutParams(new LinearLayout.LayoutParams((width - Utils.dip2px(40)) / maxCycleLength * cycleLengths.get(i), Utils.dip2px(15)));
            cycleShow.setMax(cycleLengths.get(i));
            cycleShow.setProgress(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(i + 1).getPeriodStart()),
                    Utils.convertIntDateToCalendar(data.get(i + 1).getPeriodEnd())) + 1);
            cycleShow.setProgressEndText(String.valueOf(cycleLengths.get(i)));
            cycleShow.setProgressText(String.valueOf(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(i + 1).getPeriodStart()),
                    Utils.convertIntDateToCalendar(data.get(i + 1).getPeriodEnd())) + 1));
            mProgressArea.addView(cycleShow);
        }
        mProgressArea.setDotLinePosition((width - Utils.dip2px(40)) / maxCycleLength * avgCycleLength);
    }
}