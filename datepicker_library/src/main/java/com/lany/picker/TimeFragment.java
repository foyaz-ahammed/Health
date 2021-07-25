package com.lany.picker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.joda.time.DateTime;

public class TimeFragment extends Fragment {
    DateTime mDateTime;
    HourMinutePicker mView;

    HourMinutePicker.OnTimeChangedListener mTimeChangeListener;

    public TimeFragment() {
        // Required empty public constructor
    }

    public TimeFragment(DateTime dateTime, HourMinutePicker.OnTimeChangedListener listener) {
        mDateTime = new DateTime(dateTime);
        mTimeChangeListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (HourMinutePicker) inflater.inflate(R.layout.picker_tab_time, container, false);

        //Set hour, and minute
        mView.setCurrentHour(mDateTime.getHourOfDay());
        mView.setCurrentMinute(mDateTime.getMinuteOfHour());

        if(mTimeChangeListener != null)
            mView.setOnTimeChangedListener(mTimeChangeListener);
        return mView;
    }

    public int[] getTime(){
        int hour, minute;
        hour = mView.getCurrentHour();
        minute = mView.getCurrentMinute();

        return new int[]{hour, minute};
    }
}
