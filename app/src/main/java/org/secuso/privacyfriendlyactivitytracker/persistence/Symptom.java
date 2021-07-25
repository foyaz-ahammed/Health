package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "symptom")
public class Symptom {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "date")
    private int date;

    @ColumnInfo(name = "symptoms")
    private String symptoms;

    @ColumnInfo(name = "flowIntensity")
    private int flowIntensity;

    @ColumnInfo(name = "painIntensity")
    private int painIntensity;

    public Symptom(int id, int date, String symptoms, int flowIntensity, int painIntensity) {
        this._id = id;
        this.date = date;
        this.symptoms = symptoms;
        this.flowIntensity = flowIntensity;
        this.painIntensity = painIntensity;
    }

    public int getId() {
        return _id;
    }

    public int getDate() {
        return date;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public int getFlowIntensity() {
        return flowIntensity;
    }

    public int getPainIntensity() {
        return painIntensity;
    }
}
