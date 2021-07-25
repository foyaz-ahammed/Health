package com.lany.picker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.joda.time.DateTime;

import java.util.Objects;

public class TabDateTimePicker extends LinearLayout
        implements DatePicker.OnDateChangedListener, HourMinutePicker.OnTimeChangedListener {
    ViewPager mViewPager;
    TabLayout mTabLayout;
    DateTime mDateTime;

    DateFragment mDateFragment;
    TimeFragment mTimeFragment;

    public TabDateTimePicker(Context context) {
        this(context, null);
    }

    public TabDateTimePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabDateTimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.picker_tab_date_time,this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onFinishInflate(){
        super.onFinishInflate();
        mViewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tabLayout);

        mViewPager.setOnTouchListener(null);
    }

    public void setup(FragmentManager fm, DateTime dateTime) {
        mDateTime = new DateTime(dateTime);
        //Add tabs
        ViewPagerAdapter adapter = new ViewPagerAdapter(fm);

        //Create fragments, and add
        mDateFragment = new DateFragment(mDateTime, this);
        mTimeFragment = new TimeFragment(mDateTime, this);

        adapter.addFrag(mDateFragment, getDateString(mDateTime.getYear(), mDateTime.getMonthOfYear(), mDateTime.getDayOfMonth()));
        adapter.addFrag(mTimeFragment, getTimeString(mDateTime.getHourOfDay(), mDateTime.getMinuteOfHour()));
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public DateTime getDateTime(){
        int[] date = mDateFragment.getDate();
        int[] time = mTimeFragment.getTime();
        return new DateTime(date[0], date[1], date[2], time[0], time[1]);
    }

    public static String getDateString(int year, int month, int day){
        @SuppressLint("DefaultLocale") String resultString = String.format("%1$d/%2$02d/%3$02d", year, month, day);
        return resultString;
    }

    public String getTimeString(int hour, int minute){
        boolean isAm = hour < 12;
        @SuppressLint("DefaultLocale") String resultString = String.format("%1$02d:%2$02d %3$s",
                get12Hour(hour), minute,
                isAm? getContext().getString(R.string.lany_am_label) : getContext().getString(R.string.lany_pm_label));
        return resultString;
    }

    public static int get12Hour(int hour){
        int res = hour % 12;
        return res == 0? 12 : res;
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String dateString = getDateString(year, monthOfYear + 1, dayOfMonth);
        Objects.requireNonNull(mTabLayout.getTabAt(0)).setText(dateString);
    }

    @Override
    public void onTimeChanged(HourMinutePicker view, int hourOfDay, int minute) {
        String timeString = getTimeString(hourOfDay, minute);
        Objects.requireNonNull(mTabLayout.getTabAt(1)).setText(timeString);
    }
}
