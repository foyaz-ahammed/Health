package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cycleLength")
public class CycleLength {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "periodLength")
    private int periodLength;

    @ColumnInfo(name = "cycleLength")
    private int cycleLength;

    public CycleLength(int id, int periodLength, int cycleLength) {
        this._id = id;
        this.periodLength = periodLength;
        this.cycleLength = cycleLength;
    }

    public int getId() {
        return _id;
    }

    public int getPeriodLength() {
        return periodLength;
    }

    public int getCycleLength() {
        return cycleLength;
    }
}
