package org.secuso.privacyfriendlyactivitytracker.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.SetGoalActivity;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomDatePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomHourMinutePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.DistancePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.GenderPickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.HeightPickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.StepDetectionServiceHelper;
import org.secuso.privacyfriendlyactivitytracker.viewModel.StepCountViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * 설정화면
 */
public class SettingsFragment extends Fragment implements View.OnClickListener,
        SharedPreferences.OnSharedPreferenceChangeListener {
    LinearLayout mGoals;
    LinearLayout mGender;
    LinearLayout mBirth;
    LinearLayout mHeight;
    LinearLayout mWalkingLength;
    LinearLayout mRunningLength;
    LinearLayout mNotificationTime;
    TextView mGenderText;
    TextView mBirthText;
    TextView mHeightText;
    TextView mWalkingLengthText;
    TextView mRunningLengthText;
    TextView mNotificationTimeText;

    SharedPreferences sharedPref;

    StepCountViewModel mViewModel;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(requireActivity()).get(StepCountViewModel.class);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.action_settings);

        mGoals = view.findViewById(R.id.goals);
        mGoals.setOnClickListener(this);
        mGender = view.findViewById(R.id.gender);
        mGender.setOnClickListener(this);
        mGenderText = view.findViewById(R.id.gender_text);
        mBirth = view.findViewById(R.id.birth);
        mBirth.setOnClickListener(this);
        mBirthText = view.findViewById(R.id.birth_text);
        mHeight = view.findViewById(R.id.height);
        mHeight.setOnClickListener(this);
        mHeightText = view.findViewById(R.id.height_text);
        mWalkingLength = view.findViewById(R.id.walking_length);
        mWalkingLength.setOnClickListener(this);
        mWalkingLengthText = view.findViewById(R.id.walking_length_text);
        mRunningLength = view.findViewById(R.id.running_length);
        mRunningLength.setOnClickListener(this);
        mRunningLengthText = view.findViewById(R.id.running_length_text);
        mNotificationTime = view.findViewById(R.id.notification_time);
        mNotificationTime.setOnClickListener(this);
        mNotificationTimeText = view.findViewById(R.id.notification_time_text);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        String gender = sharedPref.getString(getString(R.string.pref_gender), "");
        if (gender.equals(""))
            mGenderText.setText("");
        else {
            mGenderText.setText(gender.equals("male") ? getString(R.string.male) : getString(R.string.female));
        }

        long dateOfBirth = sharedPref.getLong(getString(R.string.pref_birthday), 0);
        if (dateOfBirth != 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            mBirthText.setText(dateFormat.format(new Date(dateOfBirth)));
        }

        int height = sharedPref.getInt(getString(R.string.pref_height), 0);
        if (height != 0)
            mHeightText.setText(getString(R.string.with_centimeter, height));

        mViewModel.getWalkingModes().observe(getViewLifecycleOwner(), walkingModes -> {
            if (walkingModes.size() > 0) {
                mWalkingLengthText.setText(getString(R.string.with_meter, walkingModes.get(0).getStepSize()));
                mRunningLengthText.setText(getString(R.string.with_meter, walkingModes.get(1).getStepSize()));
            }
        });

        updateNotificationTime(sharedPref);

        return view;
    }

    @Override
    public void onDestroy() {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (view.getId()) {
            case R.id.goals:
                Intent intent = new Intent(getContext(), SetGoalActivity.class);
                Objects.requireNonNull(getContext()).startActivity(intent);
                break;
            case R.id.gender:
                GenderPickerDialog genderPickerDialog = new GenderPickerDialog(getContext(), getString(R.string.gender));
                genderPickerDialog.show();
                break;
            case R.id.birth:
                long dateOfBirth = sharedPref.getLong(getString(R.string.pref_birthday), 0);
                Calendar birthday = Calendar.getInstance();
                if (dateOfBirth != 0)
                    birthday.setTimeInMillis(dateOfBirth);
                CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(birthday.get(Calendar.YEAR),
                        birthday.get(Calendar.MONTH), birthday.get(Calendar.DAY_OF_MONTH), getContext());
                datePickerDialog.setOnDateSelectedListener((year, monthOfYear, dayOfMonth) -> {
                    birthday.set(Calendar.YEAR, year);
                    birthday.set(Calendar.MONTH, monthOfYear);
                    birthday.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    editor.putLong(getString(R.string.pref_birthday), birthday.getTimeInMillis());
                    editor.apply();
                });
                datePickerDialog.show();
                break;
            case R.id.height:
                int height = sharedPref.getInt(getString(R.string.pref_height), 0);
                HeightPickerDialog heightPickerDialog = new HeightPickerDialog(getContext(), getString(R.string.height),
                        50, 250, height != 0 ? height : 170);
                heightPickerDialog.setOnHeightChangedListener(value -> {
                    editor.putInt(getString(R.string.pref_height), value);
                    editor.apply();
                });
                heightPickerDialog.show();
                break;
            case R.id.walking_length:
                DistancePickerDialog walkingLengthPickerDialog = new DistancePickerDialog(getContext(),
                        getString(R.string.walking_step_length), true, (float) 0.3, 2);
                walkingLengthPickerDialog.setOnDistanceSelectedListener(distance ->
                        mViewModel.updateWalkingStepSize(Math.round(distance * 100) / 100.0));
                walkingLengthPickerDialog.show();
                break;
            case R.id.running_length:
                DistancePickerDialog runningLengthPickerDialog = new DistancePickerDialog(getContext(),
                        getString(R.string.running_step_length), true, (float) 0.3, 2);
                runningLengthPickerDialog.setOnDistanceSelectedListener(distance ->
                        mViewModel.updateRunningStepSize(Math.round(distance * 100) / 100.0));
                runningLengthPickerDialog.show();
                break;
            case R.id.notification_time:
                Calendar notificationTime = Calendar.getInstance();
                notificationTime.set(Calendar.HOUR_OF_DAY, 20);
                notificationTime.set(Calendar.MINUTE, 0);
                notificationTime.set(Calendar.SECOND, 0);
                notificationTime.set(Calendar.MILLISECOND, 0);
                long notifyTime = sharedPref.getLong(getString(R.string.pref_notification_time), notificationTime.getTimeInMillis());
                notificationTime.setTimeInMillis(notifyTime);
                CustomHourMinutePickerDialog hourMinutePickerDialog = new CustomHourMinutePickerDialog(
                        notificationTime.get(Calendar.HOUR_OF_DAY), notificationTime.get(Calendar.MINUTE), getContext());
                hourMinutePickerDialog.setOnTimeSelectedListener((hour, minute) -> {
                    notificationTime.set(Calendar.HOUR_OF_DAY, hour);
                    notificationTime.set(Calendar.MINUTE, minute);
                    editor.putLong(getString(R.string.pref_notification_time), notificationTime.getTimeInMillis());
                    editor.apply();
                });
                hourMinutePickerDialog.show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        if (key.equals(getString(R.string.pref_gender))) {
            String gender = sharedPref.getString(getString(R.string.pref_gender), "");
            if (gender.equals(""))
                mGenderText.setText("");
            else {
                mGenderText.setText(gender.equals("male") ? getString(R.string.male) : getString(R.string.female));
            }
        } else if (key.equals(getString(R.string.pref_birthday))) {
            long dateOfBirth = sharedPref.getLong(getString(R.string.pref_birthday), 0);
            if (dateOfBirth != 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                mBirthText.setText(dateFormat.format(new Date(dateOfBirth)));
            }
        } else if (key.equals(getString(R.string.pref_height))) {
            int height = sharedPref.getInt(getString(R.string.pref_height), 0);
            if (height != 0)
                mHeightText.setText(getString(R.string.with_centimeter, height));
        } else if (key.equals(getString(R.string.pref_notification_time))) {
            updateNotificationTime(sharedPref);
            StepDetectionServiceHelper.scheduleStepNotification(getContext());
        }
    }

    /**
     * 알림시간 갱신하는 함수
     * @param sharedPref 알림시간을 보관할 위치
     */
    private void updateNotificationTime(SharedPreferences sharedPref) {
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.HOUR_OF_DAY, 20);
        notificationTime.set(Calendar.MINUTE, 0);
        notificationTime.set(Calendar.SECOND, 0);
        notificationTime.set(Calendar.MILLISECOND, 0);
        long notifyTime = sharedPref.getLong(getString(R.string.pref_notification_time), notificationTime.getTimeInMillis());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        mNotificationTimeText.setText(timeFormat.format(new Date(notifyTime)));
    }
}