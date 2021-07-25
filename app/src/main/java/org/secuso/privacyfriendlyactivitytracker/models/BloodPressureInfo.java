package org.secuso.privacyfriendlyactivitytracker.models;

import android.content.Context;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.persistence.Blood;

import java.util.ArrayList;
import java.util.List;

/**
 * 개별적인 혈압기록자료를 담고 있는 Object
 */
public class BloodPressureInfo extends HistoryItemContainer.HistoryItemInfo {
    private int systolicValue; // 수축기값
    private int diastolicValue; // 확장기값
    private int pulseValue; // 맥박값
    private String date; // 기록한 날자
    private String time; // 기록한 시간
    private DateTime measureDateTime; // 기록한 날자와 시간
    private int Id;
    int type; // 화면에 현시할 형태 0: 기록자료 1: divider

    public BloodPressureInfo() {}

    public BloodPressureInfo(int type, Blood blood) {
        this.Id = blood.getId();
        this.systolicValue = blood.getSystolicValue();
        this.diastolicValue = blood.getDiastolicValue();
        this.pulseValue = blood.getPulseValue();
        this.date = blood.getDate();
        this.time = blood.getTime();
        this.measureDateTime = new DateTime(blood.getMeasureMilliTime());
        this.modifiedTime = blood.getModifiedTime();
        this.type = type;
    }

    public BloodPressureInfo(int type, long modifiedTime) {
        this.type = type;
        this.modifiedTime = modifiedTime;
    }

    public void setId(int id) {
        Id = id;
    }

    public void setSystolicValue(int systolicValue) {
        this.systolicValue = systolicValue;
    }

    public void setDiastolicValue(int diastolicValue) {
        this.diastolicValue = diastolicValue;
    }

    public void setPulseValue(int pulseValue) {
        this.pulseValue = pulseValue;
    }

    public void setMeasureDateTime(DateTime measureDateTime) {
        this.measureDateTime = measureDateTime;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return Id;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getSystolicValue() {
        return systolicValue;
    }

    public int getDiastolicValue() {
        return diastolicValue;
    }

    public int getPulseValue() {
        return pulseValue;
    }

    public DateTime getMeasureDateTime() {
        return measureDateTime;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getType() {
        return type;
    }

    /**
     * 현재 혈압상태를 얻는 함수
     * @param context The application context
     * @return 얻어진 혈압상태
     */
    public List<String> getLevel(Context context) {
        String hypotension = context.getString(R.string.hypotension);
        String normal = context.getString(R.string.normal);
        String normalHigh = context.getString(R.string.normal_high);
        String mildHypertension = context.getString(R.string.mild_hypertension);
        String moderateHypertension = context.getString(R.string.moderate_hypertension);
        String severeHypertension = context.getString(R.string.severe_hypertension);

        String systolic = "systolic";
        String diastolic = "diastolic";

        List<String> level = new ArrayList<>();

        if (systolicValue < 90) {
            if (diastolicValue < 80) {
                level.add(hypotension);
                level.add(systolic);
            } else {
                level.add(normalHigh);
                level.add(diastolic);
            }
        } else if (systolicValue < 120) {
            if (diastolicValue < 60) {
                level.add(hypotension);
                level.add(diastolic);
            } else if (diastolicValue < 80) {
                level.add(normal);
                level.add(systolic);
            } else if (diastolicValue < 90) {
                level.add(normalHigh);
                level.add(diastolic);
            } else if (diastolicValue < 100) {
                level.add(mildHypertension);
                level.add(diastolic);
            } else if (diastolicValue < 110) {
                level.add(moderateHypertension);
                level.add(diastolic);
            } else {
                level.add(severeHypertension);
                level.add(diastolic);
            }
        } else if (systolicValue < 140) {
            if (diastolicValue < 60) {
                level.add(hypotension);
                level.add(diastolic);
            } else if (diastolicValue < 90) {
                level.add(normalHigh);
                level.add(systolic);
            } else if (diastolicValue < 100) {
                level.add(mildHypertension);
                level.add(diastolic);
            } else if (diastolicValue < 110) {
                level.add(moderateHypertension);
                level.add(diastolic);
            } else {
                level.add(severeHypertension);
                level.add(diastolic);
            }
        } else if (systolicValue < 160) {
            if (diastolicValue < 60) {
                level.add(hypotension);
                level.add(diastolic);
            } else if (diastolicValue < 100) {
                level.add(mildHypertension);
                level.add(systolic);
            } else if (diastolicValue < 110) {
                level.add(moderateHypertension);
                level.add(diastolic);
            } else {
                level.add(severeHypertension);
                level.add(diastolic);
            }
        } else if (systolicValue < 180) {
            if (diastolicValue < 60) {
                level.add(hypotension);
                level.add(diastolic);
            } else if (diastolicValue < 110) {
                level.add(moderateHypertension);
                level.add(systolic);
            } else {
                level.add(severeHypertension);
                level.add(diastolic);
            }
        } else {
            if (diastolicValue < 60) {
                level.add(hypotension);
                level.add(diastolic);
            } else {
                level.add(severeHypertension);
                level.add(systolic);
            }
        }
        return level;
    }
}
