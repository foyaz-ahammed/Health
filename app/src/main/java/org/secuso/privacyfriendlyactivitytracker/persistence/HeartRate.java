package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.models.HeartRateInfo;

@Entity(tableName = "heartRate")
public class HeartRate {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "pulseValue")
    int pulseValue;

    @ColumnInfo(name = "measureTime")
    long measureTime;

    @ColumnInfo(name = "month")
    String month;

    @ColumnInfo(name = "day")
    String day;

    @ColumnInfo(name = "note")
    String note;

    @ColumnInfo(name = "status")
    int status;

    public HeartRate(HeartRateInfo info) {
        this.pulseValue = info.getPulseValue();
        this.measureTime = info.getMeasureTime();
        this.month = info.getMonth();
        this.day = info.getDay();
        this.note = info.getNote();
        this.status = info.getStatus();
    }

    public HeartRate(int pulseValue, long measureTime, String month, String day, String note, int status) {
        this.pulseValue = pulseValue;
        this.measureTime = measureTime;
        this.month = month;
        this.day = day;
        this.note = note;
        this.status = status;
    }

    public int getId() {
        return _id;
    }

    public int getPulseValue() {
        return pulseValue;
    }

    public long getMeasureTime() {
        return measureTime;
    }

    public int getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }
}
