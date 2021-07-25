package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;

@Entity(tableName = "weight")
public class Weight {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "weightValue")
    private final String weightValue;

    @ColumnInfo(name = "fatRateValue")
    private final String fatRateValue;

    @ColumnInfo(name = "year")
    String year;

    @ColumnInfo(name = "month")
    String month;

    @ColumnInfo(name = "measureDate")
    private final String date;

    @ColumnInfo(name = "measureTime")
    private final String time;

    @ColumnInfo(name = "measureMilliTime")
    private final long measureMilliTime;

    @ColumnInfo(name = "modifiedTime")
    private final long modifiedTime;


    public Weight(String weightValue, String fatRateValue, String year, String month, String date, String time, long measureMilliTime, long modifiedTime) {
        this.weightValue = weightValue;
        this.fatRateValue = fatRateValue;
        this.year = year;
        this.month = month;
        this.date = date;
        this.time = time;
        this.measureMilliTime = measureMilliTime;
        this.modifiedTime = modifiedTime;

    }

    @Ignore
    public Weight(WeightInfo weightInfo) {
        this._id = weightInfo.getId();
        this.weightValue = weightInfo.getWeightValue();
        this.fatRateValue = weightInfo.getFatRateValue();
        this.year = weightInfo.getYear();
        this.month = weightInfo.getMonth();
        this.date = weightInfo.getDate();
        this.time = weightInfo.getTime();
        this.measureMilliTime = weightInfo.getMeasureDateTime().getMillis();
        this.modifiedTime = System.currentTimeMillis();
    }

    public int getId() {
        return _id;
    }

    public String getWeightValue() {
        return weightValue;
    }

    public String getFatRateValue() {
        return fatRateValue;
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

    public long getMeasureMilliTime() {
        return measureMilliTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }
}
