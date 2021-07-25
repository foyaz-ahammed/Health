package org.secuso.privacyfriendlyactivitytracker.exercise;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.ToolbarActivity;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.ActivityPickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomDatePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomHourMinutePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomTimeDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.DistancePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.viewModel.ExerciseViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 운동기록추가화면
 */
public class AddWorkoutActivity extends ToolbarActivity implements View.OnClickListener {
    ImageView mChooseActivityImg;
    LinearLayout mActivityPicker;
    LinearLayout mDatePicker;
    LinearLayout mTimePicker;
    LinearLayout mDurationPicker;
    LinearLayout mDistancePicker;
    TextView mActivity;
    TextView mDuration;
    TextView mDistance;
    TextView mDate;
    TextView mTime;
    TextView mAddRecordDone;
    // 기록측정시간자료
    int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;
    DateTime date;
    long duration;
    // 운동을 끝낸 시간이 현재시간보다 이전인가를 판단하기 위해 운동시간을 처음으로 설정하였을때를 보관한다.
    boolean isDurationFirstChanged = false;
    int activity = 1; //운동종목
    boolean isMeter; //운동종목이 수영일때에는 거리를 메터단위로 표시해야 한다.
    float distance; //운동한 거리
    List<String> activityArray = new ArrayList<>();
    List<Drawable> activityImgArray = new ArrayList<>();

    ExerciseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_workout);
        super.onCreate(savedInstanceState);

        mChooseActivityImg = findViewById(R.id.choose_activity_img);
        mActivityPicker = findViewById(R.id.activity_picker);
        mDatePicker = findViewById(R.id.date_picker);
        mTimePicker = findViewById(R.id.time_picker);
        mDurationPicker = findViewById(R.id.duration_picker);
        mDistancePicker = findViewById(R.id.distance_picker);
        mActivity = findViewById(R.id.activity);
        mDate = findViewById(R.id.date);
        mTime = findViewById(R.id.time);
        mDuration = findViewById(R.id.duration);
        mDistance = findViewById(R.id.distance);
        mAddRecordDone = findViewById(R.id.add_record_done);

        selectedYear = Calendar.getInstance().get(Calendar.YEAR);
        selectedMonth = Calendar.getInstance().get(Calendar.MONTH);
        selectedDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        selectedHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        selectedMinute = Calendar.getInstance().get(Calendar.MINUTE);
        date = new DateTime();

        initArray();

        viewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);

        mActivity.setText(activityArray.get(0));
        mDate.setText(Utils.getDateString(date, this));
        mTime.setText(Utils.getTimeString(this, date.getHourOfDay(), date.getMinuteOfHour()));
    }

    @Override
    public void onClick(View view) {
        if (view == mDatePicker) {
            CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(selectedYear, selectedMonth, selectedDay, this);
            datePickerDialog.setOnDateSelectedListener((_year, _monthOfYear, _dayOfMonth) -> {
                Calendar current = Calendar.getInstance();
                DateTime dateTime = new DateTime(_year, _monthOfYear + 1, _dayOfMonth, selectedHour, selectedMinute);
                if (duration + dateTime.getMillis() > current.getTimeInMillis()) {
                    Toast.makeText(this, R.string.time_overflow_warning, Toast.LENGTH_SHORT).show();
                } else {
                    date = new DateTime(_year, _monthOfYear + 1, _dayOfMonth, selectedHour, selectedMinute);
                    mDate.setText(Utils.getDateString(date, getApplicationContext()));
                    selectedYear = _year;
                    selectedMonth = _monthOfYear;
                    selectedDay = _dayOfMonth;
                }
            });
            datePickerDialog.show();
        } else if (view == mTimePicker) {
            CustomHourMinutePickerDialog hourMinutePickerDialog = new CustomHourMinutePickerDialog(selectedHour, selectedMinute, this);
            hourMinutePickerDialog.setOnTimeSelectedListener((_hour, _minute) -> {
                DateTime dateTime = new DateTime(selectedYear, selectedMonth + 1, selectedDay, _hour, _minute);
                Calendar current = Calendar.getInstance();
                if (duration + dateTime.getMillis() > current.getTimeInMillis()) {
                    Toast.makeText(this, R.string.time_overflow_warning, Toast.LENGTH_SHORT).show();
                } else {
                    isDurationFirstChanged = true;
                    mTime.setText(Utils.getTimeString(this, _hour, _minute));
                    date = new DateTime(selectedYear, selectedMonth + 1, selectedDay, _hour, _minute);
                    selectedHour = _hour;
                    selectedMinute = _minute;
                }
            });
            hourMinutePickerDialog.show();
        } else if (view == mDurationPicker) {
            int _hour, _minute, _second;
            _hour = (int) duration / 3600000;
            _minute = (int) (duration - 3600000 * _hour) / 60000;
            _second = (int) (duration - 3600000 * _hour - 60000 * _minute) / 1000;
            CustomTimeDialog timePickerDialog = new CustomTimeDialog(this, _hour, _minute, _second);
            timePickerDialog.setOnTimeSelectedListener((hour, minute, second) -> {
                if (hour != 0 || minute != 0 || second != 0) {
                    mDuration.setText(getResources().getString(R.string.duration_format, hour, minute, second));
                    duration = hour * 3600000 + minute * 60000 + second * 1000;

                    if (!isDurationFirstChanged) {
                        isDurationFirstChanged = true;
                        long milliSec = date.getMillis() - duration;
                        date = new DateTime(milliSec);
                        selectedYear = date.getYear();
                        selectedMonth = date.getMonthOfYear() - 1;
                        selectedDay = date.getDayOfMonth();
                        selectedHour = date.getHourOfDay();
                        selectedMinute = date.getMinuteOfHour();
                        mDate.setText(Utils.getDateString(date, getApplicationContext()));
                        mTime.setText(Utils.getTimeString(getApplicationContext(), date.getHourOfDay(), date.getMinuteOfHour()));
                    }
                }
            });
            timePickerDialog.show();
        } else if (view == mDistancePicker) {
            final DistancePickerDialog distancePickerDialog = new DistancePickerDialog(
                    this, getString(R.string.distance), isMeter, isMeter ? 25 : (float) 0.1, isMeter ? 50000 : 500);
            distancePickerDialog.setOnDistanceSelectedListener(dist -> {
                distance = dist;
                mDistance.setText(getResources().getString(isMeter ? R.string.with_meter : R.string.with_kilometer, distance));
            });
            distancePickerDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            distancePickerDialog.show();
        } else if (view == mActivityPicker || view == mChooseActivityImg) {
            ActivityPickerDialog activityPickerDialog = new ActivityPickerDialog(this);
            activityPickerDialog.setOnActivitySelectedListener(whichActivity -> {
                isMeter = whichActivity == 5;
                activity = whichActivity;
                mActivity.setText(activityArray.get(whichActivity - 1));
                mChooseActivityImg.setImageDrawable(activityImgArray.get(whichActivity - 1));
            });
            activityPickerDialog.show();
        } else if (view == mAddRecordDone) {
            if (mDuration.getText().toString().equals("") || mDistance.getText().toString().equals("")) {
                Toast.makeText(this, R.string.complete_workout_warning, Toast.LENGTH_SHORT).show();
            } else {
                if (duration + date.getMillis() > Calendar.getInstance().getTimeInMillis()) {
                    Toast.makeText(this, R.string.time_overflow_warning, Toast.LENGTH_SHORT).show();
                } else {
                    WorkoutInfo workoutInfo = new WorkoutInfo();
                    workoutInfo.setActivity(activity);
                    workoutInfo.setDuration(mDuration.getText().toString());
                    workoutInfo.setLongDuration(duration);
                    if (activity == 5 && mDistance.getText().toString().contains(getResources().getString(R.string.kilometer))) {
                        distance = distance * 1000;
                    }
                    workoutInfo.setDistance(distance);
                    SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                    SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    SimpleDateFormat simpleYearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
                    Date month = new Date(selectedYear - 1900, selectedMonth, selectedDay);
                    workoutInfo.setYear(simpleYearFormat.format(month));
                    workoutInfo.setMonth(simpleMonthFormat.format(month));
                    workoutInfo.setDate(simpleDayFormat.format(month));
                    workoutInfo.setStartTime(date);

                    viewModel.insertOrUpdate(workoutInfo);
                    Intent intent = new Intent(this, WorkoutShowActivity.class);
                    intent.putExtra("workoutInfo", workoutInfo);
                    startActivity(intent);

//                    for (int i = 0; i < new Random().nextInt(21) + 500; i ++) {
//                        WorkoutInfo workoutInfo = new WorkoutInfo();
//                        workoutInfo.setActivity(new Random().nextInt(5) + 1);
//                        workoutInfo.setDuration(mDuration.getText().toString());
//                        if (activity == 5 && mDistance.getText().toString().contains(getResources().getString(R.string.kilometer))) {
//                            distance = distance * 1000;
//                        }
//                        workoutInfo.setDistance(distance);
//                        workoutInfo.setLongDuration(duration);
//                        SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
//                        SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//                        SimpleDateFormat simpleYearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
//                        int newYear, newMonth, newDay, newHour, newMin;
//                        newMonth = new Random().nextInt(12) + 1;
//                        newDay = new Random().nextInt(28) + 1;
//                        newHour = new Random().nextInt(12) + 1;
//                        newMin = new Random().nextInt(60);
//                        Date month = new Date(selectedYear - 1900, newMonth - 1, newDay);
//                        workoutInfo.setYear(simpleYearFormat.format(month));
//                        workoutInfo.setMonth(simpleMonthFormat.format(month));
//                        workoutInfo.setDate(simpleDayFormat.format(month));
//                        workoutInfo.setStartTime(new DateTime(selectedYear, newMonth, newDay, newHour, newMin));
//                        viewModel.insertOrUpdate(workoutInfo);
//                    }

                    finish();
                }
            }
        }
    }

    /**
     * 운동항목 초기화
     */
    private void initArray() {
        activityArray.add(getResources().getString(R.string.outdoor_run));
        activityArray.add(getResources().getString(R.string.outdoor_walk));
        activityArray.add(getResources().getString(R.string.outdoor_cycle));
        activityArray.add(getResources().getString(R.string.indoor_run));
        activityArray.add(getResources().getString(R.string.pool_swim));

        activityImgArray.add(ContextCompat.getDrawable(this, R.drawable.outdoor_run));
        activityImgArray.add(ContextCompat.getDrawable(this, R.drawable.outdoor_walk));
        activityImgArray.add(ContextCompat.getDrawable(this, R.drawable.outdoor_cycle));
        activityImgArray.add(ContextCompat.getDrawable(this, R.drawable.indoor_run));
        activityImgArray.add(ContextCompat.getDrawable(this, R.drawable.pool_swim));
    }
}