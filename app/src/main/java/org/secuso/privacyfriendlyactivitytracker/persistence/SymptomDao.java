package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SymptomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Symptom symptom);

    @Query("SELECT * FROM symptom WHERE date = :date")
    LiveData<Symptom> getSymptomByDate(int date);

    @Query("SELECT * FROM symptom WHERE date >= :start AND date <= :end")
    LiveData<List<Symptom>> getMonthData(int start, int end);

    @Query("DELETE FROM symptom WHERE _id =:id")
    void deleteById(int id);
}
