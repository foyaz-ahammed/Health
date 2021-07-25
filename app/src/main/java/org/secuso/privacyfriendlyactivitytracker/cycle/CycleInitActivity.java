package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.persistence.CycleLength;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomCycleDatePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.LengthPickerDialog;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleLengthViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleViewModel;

import java.util.Calendar;

/**
 * 생리초기설정화면
 */
public class CycleInitActivity extends ToolbarActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    LinearLayout mSetLastPeriod;
    LinearLayout mSetPeriodLength;
    LinearLayout mSetCycleLength;
    TextView mLastPeriod;
    TextView mPeriodLength;
    TextView mCycleLength;
    TextView mCycleInitDone;

    CycleLengthViewModel cycleLengthViewModel;
    CycleViewModel cycleViewModel;
    //생리기간 및 생리주기기간
    int periodLength, cycleLength;
    Calendar lastPeriodDate; //마지막생리시작날자

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_cycle_init);
        super.onCreate(savedInstanceState);

        mSetLastPeriod = findViewById(R.id.set_last_period);
        mSetLastPeriod.setOnClickListener(this);
        mSetPeriodLength = findViewById(R.id.set_period_length);
        mSetPeriodLength.setOnClickListener(this);
        mSetCycleLength = findViewById(R.id.set_cycle_length);
        mSetCycleLength.setOnClickListener(this);
        mLastPeriod = findViewById(R.id.last_period);
        mPeriodLength = findViewById(R.id.period_length);
        mCycleLength = findViewById(R.id.cycle_length);
        mCycleInitDone = findViewById(R.id.cycle_init_done);
        mCycleInitDone.setOnClickListener(this);

        cycleViewModel = new ViewModelProvider(this).get(CycleViewModel.class);
        cycleLengthViewModel = new ViewModelProvider(this).get(CycleLengthViewModel.class);

        enableCycleDone();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_last_period:
                CustomCycleDatePickerDialog cycleDatePickerDialog = new CustomCycleDatePickerDialog(this);
                cycleDatePickerDialog.setOnDateSelectedListener((year, monthOfYear, dayOfMonth) -> {
                    lastPeriodDate = Calendar.getInstance();
                    lastPeriodDate.set(Calendar.YEAR, year);
                    lastPeriodDate.set(Calendar.MONTH, monthOfYear);
                    lastPeriodDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    mLastPeriod.setText(getResources().getString(R.string.date_format1, year, monthOfYear + 1, dayOfMonth));
                    enableCycleDone();
                });
                cycleDatePickerDialog.show();
                break;
            case R.id.set_period_length:
                LengthPickerDialog periodLengthPickerDialog = new LengthPickerDialog(this, getResources().getString(R.string.period_length), 2, 15, 5);
                periodLengthPickerDialog.setOnLengthChangedListener(value -> {
                    periodLength = value;
                    mPeriodLength.setText(getResources().getString(R.string.with_days, value));
                    enableCycleDone();
                });
                periodLengthPickerDialog.show();
                break;
            case R.id.set_cycle_length:
                LengthPickerDialog cycleLengthPickerDialog = new LengthPickerDialog(this, getResources().getString(R.string.cycle_length), 20, 90, 28);
                cycleLengthPickerDialog.setOnLengthChangedListener(value -> {
                    cycleLength = value;
                    mCycleLength.setText(getResources().getString(R.string.with_days, value));
                    enableCycleDone();
                });
                cycleLengthPickerDialog.show();
                break;
            case R.id.cycle_init_done:
                if (periodLength > 0 && cycleLength > 0 && lastPeriodDate != null) {
                    cycleLengthViewModel.insertOrUpdate(new CycleLength(1, periodLength, cycleLength));

                    //새 생리자료 추가
                    Calendar periodStart = (Calendar) lastPeriodDate.clone();
                    Calendar periodEnd = (Calendar) lastPeriodDate.clone();
                    periodEnd.add(Calendar.DAY_OF_MONTH, periodLength - 1);
                    Calendar fertileStart = (Calendar) periodStart.clone();
                    Calendar fertileEnd = (Calendar) periodStart.clone();
                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart),
                            Utils.getIntDate(periodEnd), Utils.getIntDate(fertileStart),
                            Utils.getIntDate(fertileEnd), 0));
                    addPredictOvulation(periodStart);

                    Intent intent = new Intent(this, CycleActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, R.string.complete_cycle_initial_warning, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //예상자료 생성함수
    private void addPredictOvulation(Calendar beforePeriodStart) {
        Calendar periodStart = (Calendar) beforePeriodStart.clone();
        periodStart.add(Calendar.DAY_OF_MONTH, cycleLength);
        Calendar periodEnd = (Calendar) periodStart.clone();
        Calendar fertileStart = (Calendar) periodStart.clone();
        Calendar fertileEnd = (Calendar) periodStart.clone();
        periodEnd.add(Calendar.DAY_OF_MONTH, periodLength - 1);
        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);

        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
        fertileStart.add(Calendar.DAY_OF_MONTH, cycleLength);
        fertileEnd.add(Calendar.DAY_OF_MONTH, cycleLength);
        periodStart.add(Calendar.DAY_OF_MONTH, cycleLength);
        periodEnd.add(Calendar.DAY_OF_MONTH, cycleLength);
        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
        fertileStart.add(Calendar.DAY_OF_MONTH, cycleLength);
        fertileEnd.add(Calendar.DAY_OF_MONTH, cycleLength);
        periodStart.add(Calendar.DAY_OF_MONTH, cycleLength);
        periodEnd.add(Calendar.DAY_OF_MONTH, cycleLength);
        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        lastPeriodDate = Calendar.getInstance();
        lastPeriodDate.set(Calendar.YEAR, year);
        lastPeriodDate.set(Calendar.MONTH, month);
        lastPeriodDate.set(Calendar.DAY_OF_MONTH, day);
        mLastPeriod.setText(getResources().getString(R.string.date_format1, year, month + 1, day));
        enableCycleDone();
    }

    /**
     * 확인단추 능동여부 설정하는 함수
     */
    private void enableCycleDone() {
        if (lastPeriodDate != null && periodLength > 0 && cycleLength > 0) {
//            mCycleInitDone.setClickable(true);
            mCycleInitDone.setAlpha(1.0f);
        } else {
//            mCycleInitDone.setClickable(false);
            mCycleInitDone.setAlpha(0.5f);
        }
    }
}