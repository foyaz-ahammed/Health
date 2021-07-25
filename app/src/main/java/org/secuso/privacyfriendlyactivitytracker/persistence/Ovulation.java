package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "ovulation")
public class Ovulation {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "periodStart")
    public int periodStart;

    @ColumnInfo(name = "periodEnd")
    public int periodEnd;

    @ColumnInfo(name = "fertileStart")
    public int fertileStart;

    @ColumnInfo(name = "fertileEnd")
    public int fertileEnd;

    @ColumnInfo(name = "isPredict")
    public int isPredict;

    public Ovulation(int id, int periodStart, int periodEnd, int fertileStart, int fertileEnd, int isPredict) {
        this._id = id;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.fertileStart = fertileStart;
        this.fertileEnd = fertileEnd;
        this.isPredict = isPredict;
    }

    public int getId() {
        return _id;
    }

    public int getPeriodStart() {
        return periodStart;
    }

    public int getPeriodEnd() {
        return periodEnd;
    }

    public int getFertileStart() {
        return fertileStart;
    }

    public int getFertileEnd() {
        return fertileEnd;
    }

    public int getIsPredict() {
        return isPredict;
    }
}
