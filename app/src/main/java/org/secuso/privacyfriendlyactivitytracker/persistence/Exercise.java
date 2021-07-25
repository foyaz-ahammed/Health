package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;

@Entity(tableName = "exercise")
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "activity")
    public int activity;

    @ColumnInfo(name = "duration")
    public String duration;

    @ColumnInfo(name = "longDuration")
    public long longDuration;

    @ColumnInfo(name = "distance")
    public float distance;

    @ColumnInfo(name = "year")
    public String year;

    @ColumnInfo(name = "month")
    public String month;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "startTime")
    public long startTime;

    @ColumnInfo(name = "modifiedTime")
    public long modifiedTime;

    public Exercise(int id, int activity, String duration, long longDuration, float distance, String year, String month, String date, long startTime, long modifiedTime) {
        this._id = id;
        this.activity = activity;
        this.duration = duration;
        this.longDuration = longDuration;
        this.distance = distance;
        this.year = year;
        this.month = month;
        this.date = date;
        this.startTime = startTime;
        this.modifiedTime = modifiedTime;
    }

    public Exercise(WorkoutInfo info) {
        this.activity = info.getActivity();
        this.duration = info.getDuration();
        this.longDuration = info.getLongDuration();
        this.distance = info.getDistance();
        this.year = info.getYear();
        this.month = info.getMonth();
        this.date = info.getDate();
        this.startTime = info.getStartTime().getMillis();
        this.modifiedTime = System.currentTimeMillis();
    }

    public int getId() {
        return _id;
    }

    public int getActivity() {
        return activity;
    }

    public float getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public long getLongDuration() {
        return longDuration;
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

    public long getStartTime() {
        return startTime;
    }

    public long getModifiedTime() {
        return modifiedTime;
    }
}
