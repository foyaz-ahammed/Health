package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "walkingModes")
public class WalkingModes {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name = "stepSize")
    double stepSize;

    @ColumnInfo(name = "stepFrequency")
    double stepFrequency;

    @ColumnInfo(name = "isActive")
    int isActive;

    @ColumnInfo(name = "deleted")
    int deleted;

    public WalkingModes(int id, String name, double stepSize, double stepFrequency, int isActive, int deleted) {
        this._id = id;
        this.name = name;
        this.stepSize = stepSize;
        this.stepFrequency = stepFrequency;
        this.isActive = isActive;
        this.deleted = deleted;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public double getStepSize() {
        return stepSize;
    }

    public double getStepFrequency() {
        return stepFrequency;
    }

    public int getIsActive() {
        return isActive;
    }

    public int getDeleted() {
        return deleted;
    }
}
