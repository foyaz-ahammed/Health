package com.lany.picker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import org.joda.time.DateTime;

public class DateFragment extends Fragment {
    DateTime mDateTime;
    DatePicker mView;

    DatePicker.OnDateChangedListener mDateChangeListener;

    public DateFragment() {
        // Required empty public constructor
    }

    public DateFragment(DateTime dateTime, DatePicker.OnDateChangedListener listener) {
        mDateTime = new DateTime(dateTime);
        mDateChangeListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = (DatePicker) inflater.inflate(R.layout.picker_tab_date, container, false);
        mView.updateDate(mDateTime.getYear(), mDateTime.getMonthOfYear() - 1, mDateTime.getDayOfMonth());

        if(mDateChangeListener != null)
            mView.setOnDateChangedListener(mDateChangeListener);

        return mView;
    }

    public int[] getDate(){
        int year, month, day;
        year = mView.getYear();
        month = mView.getMonth() + 1;
        day = mView.getDayOfMonth();

        return new int[]{year, month, day};
    }
}

