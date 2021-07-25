package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;

@Entity(tableName = "blood")
public class Blood {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "systolicValue")
    int systolicValue;

    @ColumnInfo(name = "diastolicValue")
    int diastolicValue;

    @ColumnInfo(name = "pulseValue")
    int pulseValue;

    @ColumnInfo(name = "measureDate")
    String date;

    @ColumnInfo(name = "measureTime")
    String time;

    @ColumnInfo(name = "measureMilliTime")
    long measureMilliTime;

    @ColumnInfo(name = "modifiedTime")
    long modifiedTime;

    public Blood(BloodPressureInfo info) {
        this._id = info.getId();
        this.systolicValue = info.getSystolicValue();
        this.diastolicValue = info.getDiastolicValue();
        this.pulseValue = info.getPulseValue();
        this.date = info.getDate();
        this.time = info.getTime();
        this.measureMilliTime = info.getMeasureDateTime().getMillis();
        this.modifiedTime = System.currentTimeMillis();
    }

    public Blood(int systolicValue, int diastolicValue, int pulseValue, String date, String time, long measureMilliTime, long modifiedTime) {
        this.systolicValue = systolicValue;
        this.diastolicValue = diastolicValue;
        this.pulseValue = pulseValue;
        this.date = date;
        this.time = time;
        this.measureMilliTime = measureMilliTime;
        this.modifiedTime = modifiedTime;
    }

    public int getId() {
        return _id;
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

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public long getMeasureMilliTime() {
        return measureMilliTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }
}
