package org.secuso.privacyfriendlyactivitytracker.weight;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.WeightRepository;
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
 * 새 기록추가화면
 */
public class AddRecordActivity extends ToolbarActivity implements View.OnClickListener {
    private LinearLayout mDatePicker;
    private LinearLayout mTimePicker;
    private TextView mSelectedDate;
    private TextView mSelectedTime;
    private CustomRulerView mWeightRuler;
    private TextView mWeightValue;
    private CustomRulerView mFatRateRuler;
    private TextView mFatRateValue;
    private ImageView mFatRateShow;
    private ImageView mFatRateHide;
    private LinearLayout mFatRateArea;
    private TextView mAddRecordDone;
    private ImageView mArrowDate;
    private ImageView mArrowTime;
    private RelativeLayout mAddFatRateArea;

    DateTime mDate;

    int year, month, day, hour, minute;
    int beforeYear, beforeMonth, beforeDay, beforeHour, beforeMinute;

    int _id;
    String weightValue, fatRateValue;
    DateTime mMeasureTime;
    boolean isEditEnabled = false; // 편집가능상태판별
    boolean isUpdate = false; // 현재 화면이 편집을 위한 화면인지 판별

    WeightRepository repository;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_record);
        super.onCreate(savedInstanceState);

        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();

        if (bundle != null) {
            isUpdate = true;
            _id = (int) bundle.getInt("_id");
            weightValue = (String) bundle.get("weightValue");
            fatRateValue = (String) bundle.get("fatRateValue");
            long millisec = (long) bundle.getLong("millisec");
            mMeasureTime = new DateTime(millisec);
            year = beforeYear = mMeasureTime.getYear();
            month = beforeMonth =  mMeasureTime.getMonthOfYear() - 1;
            day = beforeDay =  mMeasureTime.getDayOfMonth();
            hour = beforeHour = mMeasureTime.getHourOfDay();
            minute = beforeMinute = mMeasureTime.getMinuteOfHour();
        } else {
            mMeasureTime = new DateTime();
            year = Calendar.getInstance().get(Calendar.YEAR);
            month = Calendar.getInstance().get(Calendar.MONTH);
            day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            minute = Calendar.getInstance().get(Calendar.MINUTE);
        }

        if (bundle != null)
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.history);

        mDatePicker = findViewById(R.id.date_picker);
        mDatePicker.setOnClickListener(this);
        mTimePicker = findViewById(R.id.time_picker);
        mTimePicker.setOnClickListener(this);
        mSelectedDate = findViewById(R.id.selected_date);
        mSelectedDate.setText(Utils.getDateString(mMeasureTime, this));
        mSelectedTime = findViewById(R.id.selected_time);
        mSelectedTime.setText(Utils.getTimeString(this, mMeasureTime.getHourOfDay(), mMeasureTime.getMinuteOfHour()));
        mWeightRuler = findViewById(R.id.weight_ruler);
        mWeightValue = findViewById(R.id.weight_value);
        mWeightValue.setText(String.valueOf(60.0));
        mWeightRuler.setOnValueChangedListener(value -> mWeightValue.setText(String.valueOf(value)));
        mFatRateRuler = findViewById(R.id.fat_rate_ruler);
        mFatRateValue = findViewById(R.id.fat_rate_value);
        mFatRateValue.setText(String.valueOf(20.0));
        mFatRateRuler.setOnValueChangedListener(value -> mFatRateValue.setText(String.valueOf(value)));

        mFatRateArea = findViewById(R.id.fat_rate_area);
        mFatRateShow = findViewById(R.id.fat_rate_show);
        mFatRateShow.setOnClickListener(this);
        mFatRateHide = findViewById(R.id.fat_rate_hide);
        mFatRateHide.setOnClickListener(this);
        mAddRecordDone = findViewById(R.id.add_record_done);
        mAddRecordDone.setOnClickListener(this);
        mArrowDate = findViewById(R.id.arrow_date);
        mArrowTime = findViewById(R.id.arrow_time);
        mAddFatRateArea = findViewById(R.id.add_fat_rate_area);

        repository = new WeightRepository(getApplication());

        if (bundle != null) {
            mWeightRuler.setCurrentValue(Float.parseFloat(weightValue));
            if (fatRateValue != null) {
                mFatRateArea.setVisibility(View.VISIBLE);
                mFatRateHide.setVisibility(View.VISIBLE);
                mFatRateShow.setVisibility(View.GONE);
                mFatRateRuler.setCurrentValue(Float.parseFloat(fatRateValue));
            } else {
                mAddFatRateArea.setVisibility(View.GONE);
            }

            setEditEnable(false);
            mAddRecordDone.setVisibility(View.GONE);
        } else {
            mWeightRuler.setCurrentValue(repository.getLatestWeight() != null ?
                    Float.parseFloat(repository.getLatestWeight().getWeightValue()) : 65);
        }
    }

    /**
     * 기록변경인 경우 back 단추를 눌렀을때의 처리
     * @return
     */
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

            mWeightRuler.setCurrentValue(Float.parseFloat(weightValue));
            if (fatRateValue != null) {
                mFatRateRuler.setCurrentValue(Float.parseFloat(fatRateValue));
            } else {
                mAddFatRateArea.setVisibility(View.GONE);
            }
        } else {
            onBackPressed();
        }
        return true;
    }

    /**
     * 편집 Menu를 창조하는 함수
     * @param menu
     * @return
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
     * menu 초기화함수
     * @param menu
     * @return
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

                mAddFatRateArea.setVisibility(View.VISIBLE);
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

                    SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                    SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat simpleYearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                    Date date = new Date(year - 1900, month, day);

                    WeightInfo weightInfo = new WeightInfo();
                    weightInfo.setId(_id);
                    weightInfo.setWeightValue(mWeightValue.getText().toString());
                    weightInfo.setFatRateValue(mFatRateArea.getVisibility() == View.VISIBLE ? mFatRateValue.getText().toString() : null);
                    weightInfo.setYear(simpleYearFormat.format(date));
                    weightInfo.setMonth(simpleMonthFormat.format(date));
                    weightInfo.setDate(simpleDayFormat.format(date));
                    weightInfo.setTime(getResources().getString(R.string.time_format, hour, minute));
                    weightInfo.setMeasureDateTime(mDateTime);

                    repository.insertWeight(weightInfo);

                    weightValue = weightInfo.getWeightValue();
                    fatRateValue = weightInfo.getFatRateValue();
                    beforeYear = year;
                    beforeMonth = month;
                    beforeDay = day;
                    beforeHour = hour;
                    beforeMinute = minute;
                    if (fatRateValue == null) mAddFatRateArea.setVisibility(View.GONE);

                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Activity를 변경가능상태로 만드는 함수
     * @param isEditEnabled
     */
    private void setEditEnable(boolean isEditEnabled) {
        mWeightRuler.setEnableTouchable(isEditEnabled);
        mFatRateRuler.setEnableTouchable(isEditEnabled);
        mDatePicker.setClickable(isEditEnabled);
        mTimePicker.setClickable(isEditEnabled);
        mArrowDate.setVisibility(isEditEnabled ? View.VISIBLE : View.GONE);
        mArrowTime.setVisibility(isEditEnabled ? View.VISIBLE : View.GONE);
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
        } else if (view == mFatRateShow) {
            mFatRateArea.setVisibility(View.VISIBLE);
            mFatRateHide.setVisibility(View.VISIBLE);
            mFatRateShow.setVisibility(View.GONE);
        } else if (view == mFatRateHide) {
            mFatRateArea.setVisibility(View.GONE);
            mFatRateHide.setVisibility(View.GONE);
            mFatRateShow.setVisibility(View.VISIBLE);
        } else if (view == mAddRecordDone) {
            DateTime mDateTime = new DateTime(year, month + 1, day, hour, minute);
            Date date = new Date(year - 1900, month, day);
            DateTime current = new DateTime();
            String time = getResources().getString(R.string.time_format, hour, minute);
            if (mDateTime.getMillis() > current.getMillis()) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat simpleYearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

                WeightInfo weightInfo = new WeightInfo();
                weightInfo.setWeightValue(mWeightValue.getText().toString());
                weightInfo.setFatRateValue(mFatRateArea.getVisibility() == View.VISIBLE ? mFatRateValue.getText().toString() : null);
                weightInfo.setYear(simpleYearFormat.format(date));
                weightInfo.setMonth(simpleMonthFormat.format(date));
                weightInfo.setDate(simpleDayFormat.format(date));
                weightInfo.setTime(time);
                weightInfo.setMeasureDateTime(mDateTime);
                repository.insertWeight(weightInfo);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = sharedPref.edit();
                float weightStart = sharedPref.getFloat(getString(R.string.pref_weight_start), 0);
                if (weightStart == 0) {
                    editor.putFloat(getString(R.string.pref_weight_start), Float.parseFloat(mWeightValue.getText().toString()));
                    editor.apply();
                }

//                AsyncTask.execute(() -> {
//                    MeasureDatabase db = MeasureDatabase.getInstance(getApplicationContext());
//
//                    if (db.goalDao().getGoal() == null || db.goalDao().getGoal().getWeightStartValue() == null) {
//                        Goal originGoal = db.goalDao().getGoal();
//                        Goal goal = new Goal(1, originGoal == null ? 10000 : originGoal.getStepGoal(),
//                                mWeightValue.getText().toString(), null,
//                                originGoal == null ? 0 : originGoal.getWaterTarget());
//                        db.goalDao().insertOrUpdate(goal);
//                    }
//                });

                //시험자료생성
//                for (int i = 0; i < new Random().nextInt(21) + 520; i ++) {
//                    WeightInfo weightInfo = new WeightInfo();
//                    weightInfo.setWeightValue(String.valueOf((new Random().nextInt(30) + 50)));
//                    weightInfo.setFatRateValue(mFatRateArea.getVisibility() == View.VISIBLE ? String.valueOf(new Random().nextInt(20) + 10) : null);
//                    int newYear, newMonth, newDay, newHour, newMin;
//                    newYear = new Random().nextInt(2) + 2019;
//                    newMonth = new Random().nextInt(11) + 1;
//                    newDay = new Random().nextInt(28) + 1;
//                    newHour = new Random().nextInt(12) + 1;
//                    newMin = new Random().nextInt(60);
//                    Date month = new Date(newYear - 1900, newMonth - 1, newDay);
//                    weightInfo.setYear(simpleYearFormat.format(month));
//                    weightInfo.setMonth(simpleMonthFormat.format(month));
//                    weightInfo.setDate(simpleDayFormat.format(month));
//                    String newTime = getResources().getString(R.string.time_format, newHour, newMin);
//                    weightInfo.setTime(newTime);
//                    weightInfo.setMeasureDateTime(new DateTime(newYear, newMonth, newDay, newHour, newMin));
//                    repository.insertWeight(weightInfo);
//                }

                finish();

                editor.putBoolean(getString(R.string.pref_is_new_weight_added), true);
                editor.apply();
            }
        }
    }

}