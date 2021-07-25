package org.secuso.privacyfriendlyactivitytracker.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.persistence.Weight;

/**
 * 개별적인 기록자료를 가지고 있는 Object
 */
public class WeightInfo extends HistoryItemContainer.HistoryItemInfo {
    String weightValue; // 몸무게값
    String fatRateValue; // 체지방률값
    String year, month; // 기록한 년, 월
    String date; // 기록한 날자
    String time; // 기록한 시간
    DateTime measureDateTime; //기록한 날자 및 시간
    int Id;
    // 현시형태 "day": 일별자료 "day_divider": 일별 가름선 "weight": 개별적인 기록자료 "weight_divider": 개별적인 기록의 가름선
    String type;
    boolean isExpand; // 일별 확장상태판별

    public WeightInfo() {}

    public WeightInfo(Weight weight) {
        this.Id = weight.getId();
        this.weightValue = weight.getWeightValue();
        this.fatRateValue = weight.getFatRateValue();
        this.date = weight.getDate();
        this.time = weight.getTime();
        this.measureDateTime = new DateTime(weight.getMeasureMilliTime());
        this.modifiedTime = weight.getModifiedTime();
        this.type = "weight";
    }

    public WeightInfo(String type, String weightValue, DateTime dateTime) {
        this.type = type;
        this.weightValue = weightValue;
        this.measureDateTime = dateTime;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setWeightValue(String weightValue) {
        this.weightValue = weightValue;
    }

    public void setFatRateValue(String fatRateValue) {
        this.fatRateValue = fatRateValue;
    }

    public void setMeasureDateTime(DateTime measureDateTime) {
        this.measureDateTime = measureDateTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public int getId() {
        return Id;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeightValue() {
        return weightValue;
    }

    public String getFatRateValue() {
        return fatRateValue;
    }

    public DateTime getMeasureDateTime() {
        return measureDateTime;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getType() {
        return type;
    }

    public boolean getExpand() {
        return isExpand;
    }

    /**
     * 체질량지수를 얻는 함수
     * @param context
     * @return
     */
    public float getBMI(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        float defaultHeight;
        defaultHeight = (float) (sharedPreferences.getInt(context.getString(R.string.pref_height), 170)) / 100;
        return Float.parseFloat(getWeightValue()) / ( defaultHeight * defaultHeight);
    }

    /**
     * 체질량지수에 관한 몸상태를 얻는 함수
     * @param context
     * @return
     */
    public String getLevel(Context context) {
        if (getBMI(context) < 18.5) {
            return context.getString(R.string.underweight);
        } else if (getBMI(context) < 23) {
            return context.getString(R.string.healthy);
        } else if (getBMI(context) < 25) {
            return context.getString(R.string.overweight);
        } else if (getBMI(context) < 35) {
            return context.getString(R.string.obese);
        } else {
            return context.getString(R.string.severe_obese);
        }
    }
}
