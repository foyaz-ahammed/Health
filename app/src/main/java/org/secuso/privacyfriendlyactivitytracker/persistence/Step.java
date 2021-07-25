package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stepCount")
public class Step {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "stepCount")
    int stepCount;

    @ColumnInfo(name = "walkingMode")
    int walkingMode;

    @ColumnInfo(name = "date")
    int date;

    @ColumnInfo(name = "timeStamp")
    long timeStamp;

    public Step(int id, int stepCount, int walkingMode, int date, long timeStamp) {
        this._id = id;
        this.stepCount = stepCount;
        this.walkingMode = walkingMode;
        this.date = date;
        this.timeStamp = timeStamp;
    }

    public int getId() {
        return _id;
    }

    public int getStepCount() {
        return stepCount;
    }

    public int getWalkingMode() {
        return walkingMode;
    }

    public int getDate() {
        return date;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
