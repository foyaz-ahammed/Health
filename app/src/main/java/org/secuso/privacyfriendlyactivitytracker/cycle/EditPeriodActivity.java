package org.secuso.privacyfriendlyactivitytracker.cycle;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.persistence.CycleLength;
import org.secuso.privacyfriendlyactivitytracker.utils.LengthPickerDialog;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleLengthViewModel;

/**
 * 생리주기설정화면
 */
public class EditPeriodActivity extends ToolbarActivity implements View.OnClickListener {
    LinearLayout mPeriodPicker;
    LinearLayout mCyclePicker;
    TextView mSelectedPeriodDays;
    TextView mSelectedCycleDays;

    CycleLengthViewModel cycleLengthViewModel;
    // 생리기간 및 생리주기기간
    int mCycleLength, mPeriodLength;
    // 이전생리기간 및 생리주기기간
    int mBeforeCycleLength, mBeforePeriodLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_edit_period);
        super.onCreate(savedInstanceState);

        mPeriodPicker = findViewById(R.id.period_picker);
        mCyclePicker = findViewById(R.id.cycle_picker);
        mSelectedPeriodDays = findViewById(R.id.selected_period_days);
        mSelectedCycleDays = findViewById(R.id.selected_cycle_days);
        mPeriodPicker.setOnClickListener(this);
        mCyclePicker.setOnClickListener(this);

        cycleLengthViewModel = new ViewModelProvider(this).get(CycleLengthViewModel.class);
        cycleLengthViewModel.cycleLengthLiveData.observe(this, new Observer<CycleLength>() {
            @Override
            public void onChanged(CycleLength cycleLength) {
                if (cycleLength != null) {
                    mPeriodLength = cycleLength.getPeriodLength();
                    mCycleLength = cycleLength.getCycleLength();
                    mBeforePeriodLength = cycleLength.getPeriodLength();
                    mBeforeCycleLength = cycleLength.getCycleLength();
                } else {
                    mPeriodLength = 5;
                    mCycleLength = 28;
                    mBeforePeriodLength = 5;
                    mBeforeCycleLength = 28;
                }
                mSelectedPeriodDays.setText(getResources().getString(R.string.with_days, mPeriodLength));
                mSelectedCycleDays.setText(getResources().getString(R.string.with_days, mCycleLength));
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.period_picker:
                LengthPickerDialog periodLengthPickerDialog = new LengthPickerDialog(this, getResources().getString(R.string.period_length), 2, 15, mPeriodLength);
                periodLengthPickerDialog.setOnLengthChangedListener(new LengthPickerDialog.OnLengthChangedListener() {
                    @Override
                    public void onLengthChanged(int value) {
                        mPeriodLength = value;
                        mSelectedPeriodDays.setText(getResources().getString(R.string.with_days, value));
                    }
                });
                periodLengthPickerDialog.show();
                break;
            case R.id.cycle_picker:
                LengthPickerDialog cycleLengthPickerDialog = new LengthPickerDialog(this, getResources().getString(R.string.cycle_length), 20, 90, mCycleLength);
                cycleLengthPickerDialog.setOnLengthChangedListener(new LengthPickerDialog.OnLengthChangedListener() {
                    @Override
                    public void onLengthChanged(int value) {
                        mCycleLength = value;
                        mSelectedCycleDays.setText(getResources().getString(R.string.with_days, value));
                    }
                });
                cycleLengthPickerDialog.show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //생리기간 혹은 생리주기길이가 실지 갱신되면 생리기간 및 생리주기길이 갱신
        if (mBeforeCycleLength != mCycleLength || mBeforePeriodLength != mPeriodLength)
            cycleLengthViewModel.insertOrUpdate(new CycleLength(1, mPeriodLength, mCycleLength));
        super.onBackPressed();
    }
}