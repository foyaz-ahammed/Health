package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;

@Entity(tableName = "water")
public class Water {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "glasses")
    public int glasses;

    @ColumnInfo(name = "measureDate")
    public long date;

    public Water(int glasses, long date) {
        this.glasses = glasses;
        this.date = date;
    }

    public Water(WaterInfo waterInfo) {
        this._id = waterInfo.getId();
        this.glasses = waterInfo.getGlasses();
        this.date = waterInfo.getMeasureDateTime().getMillis();
    }

    public int getId() {
        return _id;
    }

    public int getGlasses() {
        return glasses;
    }

    public long getDate() {
        return date;
    }
}
