package org.secuso.privacyfriendlyactivitytracker.models;

import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRate;

public class HeartRateInfo {
    int id;
    int pulseValue;
    long measureTime;
    String month;
    String day;
    String note;
    int status;
    int type;

    public HeartRateInfo(int type) {
        measureTime = -1;
        this.type = type;
    }

    public HeartRateInfo(HeartRate heartRate) {
        id = heartRate.getId();
        pulseValue = heartRate.getPulseValue();
        measureTime = heartRate.getMeasureTime();
        note = heartRate.getNote();
        status = heartRate.getStatus();
        type = 1;
    }

    public HeartRateInfo(int pulseValue, long measureTime, String month, String day, String note, int status) {
        this.pulseValue = pulseValue;
        this.measureTime = measureTime;
        this.month = month;
        this.day = day;
        this.note = note;
        this.status = status;
    }

    public int getPulseValue() {
        return pulseValue;
    }

    public long getMeasureTime() {
        return measureTime;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getNote() {
        return note;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }
}
