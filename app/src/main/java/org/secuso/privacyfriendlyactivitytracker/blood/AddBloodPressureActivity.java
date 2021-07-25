package org.secuso.privacyfriendlyactivitytracker.blood;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.BloodRepository;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomDatePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomHourMinutePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomRulerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

/**
 * 혈압추가화면
 */
public class AddBloodPressureActivity extends ToolbarActivity implements View.OnClickListener {
    LinearLayout mDatePicker;
    LinearLayout mTimePicker;
    TextView mSelectedDate;
    TextView mSelectedTime;
    CustomRulerView mSystolicRuler;
    TextView mSystolicValue;
    CustomRulerView mDiastolicRuler;
    TextView mDiastolicValue;
    CustomRulerView mPulseRuler;
    TextView mPulseValue;
    TextView mAddRecordDone;
    LinearLayout mPulseArea;
    ImageView mPulseShow;
    ImageView mPulseHide;
    RelativeLayout mAddPulseArea;
    ImageView mArrowDate;
    ImageView mArrowTime;

    DateTime mMeasureTime;
    DateTime mDate;
    int _id;
    // 기록측정시간자료
    int year, month, day, hour, minute;
    // 기록변경시 취소단추를 눌렀을때의 처리를 위한 이미 보관된 이전 시간자료들
    int beforeYear, beforeMonth, beforeDay, beforeHour, beforeMinute;
    int systolicValue, diastolicValue, pulseValue;
    boolean isUpdate = false; // 현재 화면이 기록편집을 위한 화면인지 판별
    boolean isEditEnabled = false; // 기록편집가능상태판별

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_blood_pressure);
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();

        if (bundle != null) {
            isUpdate = true;
            _id = (int) bundle.getInt("_id");
            systolicValue = (int) bundle.getInt("systolicValue");
            diastolicValue = (int) bundle.getInt("diastolicValue");
            pulseValue = (int) bundle.getInt("pulseValue");
            long millisec = (long) bundle.getLong("millisec");
            mMeasureTime = new DateTime(millisec);
            year = beforeYear = mMeasureTime.getYear();
            month = beforeMonth =  mMeasureTime.getMonthOfYear() - 1;
            day = beforeDay =  mMeasureTime.getDayOfMonth();
            hour = beforeHour = mMeasureTime.getHourOfDay();
            minute = beforeMinute = mMeasureTime.getMinuteOfHour();

            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.history);
        } else {
            mMeasureTime = new DateTime();
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH);
            day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            minute = Calendar.getInstance().get(Calendar.MINUTE);
        }

        mDatePicker = findViewById(R.id.date_picker);
        mDatePicker.setOnClickListener(this);
        mTimePicker = findViewById(R.id.time_picker);
        mTimePicker.setOnClickListener(this);
        mSelectedDate = findViewById(R.id.selected_date);
        mSelectedDate.setText(Utils.getDateString(mMeasureTime, this));
        mSelectedTime = findViewById(R.id.selected_time);
        mSelectedTime.setText(Utils.getTimeString(this, mMeasureTime.getHourOfDay(), mMeasureTime.getMinuteOfHour()));
        mSystolicRuler = findViewById(R.id.systolic_ruler);
        mSystolicValue = findViewById(R.id.systolic_value);
        mDiastolicRuler = findViewById(R.id.diastolic_ruler);
        mDiastolicValue = findViewById(R.id.diastolic_value);
        mPulseRuler = findViewById(R.id.pulse_ruler);
        mPulseValue = findViewById(R.id.pulse_value);
        mAddRecordDone = findViewById(R.id.add_record_done);
        mAddRecordDone.setOnClickListener(this);
        mPulseArea = findViewById(R.id.pulse_area);
        mPulseShow = findViewById(R.id.pulse_show);
        mPulseShow.setOnClickListener(this);
        mPulseHide = findViewById(R.id.pulse_hide);
        mPulseHide.setOnClickListener(this);
        mAddPulseArea = findViewById(R.id.add_pulse_area);
        mArrowDate = findViewById(R.id.arrow_date);
        mArrowTime = findViewById(R.id.arrow_time);

        mSystolicValue.setText(String.valueOf(120));
        mDiastolicValue.setText(String.valueOf(80));
        mPulseValue.setText(String.valueOf(75));
        mSystolicRuler.setOnValueChangedListener(value -> mSystolicValue.setText(String.valueOf((int) value)));
        mDiastolicRuler.setOnValueChangedListener(value -> mDiastolicValue.setText(String.valueOf((int) value)));
        mPulseRuler.setOnValueChangedListener(value -> mPulseValue.setText(String.valueOf((int) value)));

        if (bundle != null) {
            mSystolicRuler.setCurrentValue(systolicValue);
            mDiastolicRuler.setCurrentValue(diastolicValue);
            if (pulseValue > 0) {
                mPulseArea.setVisibility(View.VISIBLE);
                mPulseHide.setVisibility(View.VISIBLE);
                mPulseShow.setVisibility(View.GONE);
                mPulseRuler.setCurrentValue(pulseValue);
            } else {
                mAddPulseArea.setVisibility(View.GONE);
            }

            setEditEnable(false);
            mAddRecordDone.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (isEditEnabled) {
            TypedArray backBtn = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.homeAsUpIndicator});
            Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(backBtn.getResourceId(0, 0));
            isEditEnabled = false;
            setEditEnable(false);
            invalidateOptionsMenu();

            year = beforeYear;
            month = beforeMonth;
            day = beforeDay;
            hour = beforeHour;
            minute = beforeMinute;

            DateTime date = new DateTime(year, month + 1, day, 0, 0);
            mSelectedDate.setText(Utils.getDateString(date, this));
            mSelectedTime.setText(Utils.getTimeString(getApplicationContext(), hour, minute));

            mSystolicRuler.setCurrentValue(systolicValue);
            mDiastolicRuler.setCurrentValue(diastolicValue);
            if (pulseValue > 0) {
                mPulseRuler.setCurrentValue(pulseValue);
            } else {
                mAddPulseArea.setVisibility(View.GONE);
            }
        } else {
            onBackPressed();
        }
        return true;
    }

    /**
     * Menu생성 함수
     * @param menu 생성될 menu
     * @return true이면 menu 보여주기, false이면 보여주지 않기
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isUpdate) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_edit, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Menu초기화함수
     * @param menu 생성된 Menu
     * @return true이면 menu 보여주기, false이면 보여주지 않기
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (isUpdate) {
            MenuItem editItem = menu.findItem(R.id.menu_edit);
            MenuItem saveItem = menu.findItem(R.id.menu_save);
            editItem.setVisible(!isEditEnabled);
            saveItem.setVisible(isEditEnabled);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Menu 항목을 눌렀을때 처리를 진행하는 함수
     * @param item 눌러진 항목
     * @return 처리가 진행되였으면 true, 아니면 false
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                isEditEnabled = true;
                Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
                setEditEnable(true);
                invalidateOptionsMenu();

                mAddPulseArea.setVisibility(View.VISIBLE);
                return true;
            case R.id.menu_save:
                DateTime mDateTime = new DateTime(year, month + 1, day, hour, minute);
                DateTime current = new DateTime();
                if (mDateTime.getMillis() > current.getMillis()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
                    return false;
                }
                else {
                    isEditEnabled = false;
                    TypedArray backBtn = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {R.attr.homeAsUpIndicator});
                    Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(backBtn.getResourceId(0, 0));
                    setEditEnable(false);
                    invalidateOptionsMenu();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = new Date(year - 1900, month, day);

                    BloodPressureInfo bloodPressureInfo = new BloodPressureInfo();
                    bloodPressureInfo.setId(_id);
                    bloodPressureInfo.setSystolicValue(Integer.parseInt(mSystolicValue.getText().toString()));
                    bloodPressureInfo.setDiastolicValue(Integer.parseInt(mDiastolicValue.getText().toString()));
                    bloodPressureInfo.setPulseValue(mPulseArea.getVisibility() == View.VISIBLE ? Integer.parseInt(mPulseValue.getText().toString()) : -1);
                    bloodPressureInfo.setDate(dateFormat.format(date));
                    bloodPressureInfo.setTime(getResources().getString(R.string.time_format, hour, minute));
                    bloodPressureInfo.setMeasureDateTime(mDateTime);

                    BloodRepository repository = new BloodRepository(getApplication());
                    repository.insertOrUpdateBlood(bloodPressureInfo);

                    systolicValue = bloodPressureInfo.getSystolicValue();
                    diastolicValue = bloodPressureInfo.getDiastolicValue();
                    pulseValue = bloodPressureInfo.getPulseValue();
                    beforeYear = year;
                    beforeMonth = month;
                    beforeDay = day;
                    beforeHour = hour;
                    beforeMinute = minute;
                    if (pulseValue < 0) mAddPulseArea.setVisibility(View.GONE);

                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view == mDatePicker) {
            CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(year, month, day, this);
            datePickerDialog.setOnDateSelectedListener((_year, _monthOfYear, _dayOfMonth) -> {
                mDate = new DateTime(_year, _monthOfYear + 1, _dayOfMonth, 0, 0);
                mSelectedDate.setText(Utils.getDateString(mDate, getApplicationContext()));
                year = _year;
                month = _monthOfYear;
                day = _dayOfMonth;
            });
            datePickerDialog.show();
        } else if (view == mTimePicker) {
            CustomHourMinutePickerDialog hourMinutePickerDialog = new CustomHourMinutePickerDialog(hour, minute, this);
            hourMinutePickerDialog.setOnTimeSelectedListener((_hour, _minute) -> {
                mSelectedTime.setText(Utils.getTimeString(getApplicationContext(), _hour, _minute));
                hour = _hour;
                minute = _minute;
            });
            hourMinutePickerDialog.show();
        } else if (view == mAddRecordDone) {
            DateTime mDateTime = new DateTime(year, month + 1, day, hour, minute);
            Date date = new Date(year - 1900, month, day);
            DateTime current = new DateTime();
            String time = getResources().getString(R.string.time_format, hour, minute);
            if (mDateTime.getMillis() > current.getMillis()) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(mSystolicValue.getText().toString()) < Integer.parseInt(mDiastolicValue.getText().toString())) {
                Toast.makeText(getApplicationContext(),getString(R.string.blood_pressure_error_msg), Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                BloodRepository repository = new BloodRepository(getApplication());

                BloodPressureInfo info = new BloodPressureInfo();
                info.setSystolicValue(Integer.parseInt(mSystolicValue.getText().toString()));
                info.setDiastolicValue(Integer.parseInt(mDiastolicValue.getText().toString()));
                info.setPulseValue(mPulseArea.getVisibility() == View.VISIBLE ? Integer.parseInt(mPulseValue.getText().toString()) : -1);
                info.setDate(dateFormat.format(date));
                info.setTime(time);
                info.setMeasureDateTime(mDateTime);
                repository.insertOrUpdateBlood(info);

                //시험자료생성
//                for (int i = 0; i < new Random().nextInt(21) + 520; i ++) {
//                    BloodPressureInfo info = new BloodPressureInfo();
//                    info.setSystolicValue((new Random().nextInt(30) + 80));
//                    info.setDiastolicValue((new Random().nextInt(30) + 60));
//                    info.setPulseValue(-1);
//                    int newYear, newMonth, newDay, newHour, newMin;
//                    newYear = new Random().nextInt(2) + 2019;
//                    newMonth = new Random().nextInt(11) + 1;
//                    newDay = new Random().nextInt(28) + 1;
//                    newHour = new Random().nextInt(12) + 1;
//                    newMin = new Random().nextInt(60);
//                    Date month = new Date(newYear - 1900, newMonth - 1, newDay);
//                    info.setDate(dateFormat.format(month));
//                    String newTime = getResources().getString(R.string.time_format, newHour, newMin);
//                    info.setTime(newTime);
//                    info.setMeasureDateTime(new DateTime(newYear, newMonth, newDay, newHour, newMin));
//                    repository.insertOrUpdateBlood(info);
//                }

                finish();
            }
        } else if (view == mPulseShow) {
            mPulseArea.setVisibility(View.VISIBLE);
            mPulseShow.setVisibility(View.GONE);
            mPulseHide.setVisibility(View.VISIBLE);
        } else if (view == mPulseHide) {
            mPulseArea.setVisibility(View.GONE);
            mPulseShow.setVisibility(View.VISIBLE);
            mPulseHide.setVisibility(View.GONE);
        }
    }

    /**
     * 기록수정상태변경함수
     * @param isEditEnabled true이면 기록수정, false이면 새로 추가
     */
    private void setEditEnable(boolean isEditEnabled) {
        mSystolicRuler.setEnableTouchable(isEditEnabled);
        mDiastolicRuler.setEnableTouchable(isEditEnabled);
        mPulseRuler.setEnableTouchable(isEditEnabled);
        mDatePicker.setClickable(isEditEnabled);
        mTimePicker.setClickable(isEditEnabled);
        mArrowDate.setVisibility(isEditEnabled ? View.VISIBLE : View.GONE);
        mArrowTime.setVisibility(isEditEnabled ? View.VISIBLE : View.GONE);
    }
}