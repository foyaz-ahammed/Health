package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StepCountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Step step);

    @Query("SELECT * FROM stepCount WHERE date =:date ORDER BY timeStamp ASC")
    LiveData<List<Step>> getDayStepData(int date);

    @Query("SELECT * FROM stepCount WHERE date =:date ORDER BY timeStamp ASC")
    List<Step> getDayData(int date);

    @Query("SELECT * FROM stepCount WHERE date >= :start AND date <= :end ORDER BY timeStamp ASC")
    LiveData<List<Step>> getWeekStepData(int start, int end);

    @Query("SELECT * FROM stepCount WHERE date >= :start AND date <= :end ORDER BY timeStamp ASC")
    LiveData<List<Step>> getMonthStepData(int start, int end);

    @Query("SELECT * FROM stepCount WHERE date = :date ORDER BY timeStamp DESC LIMIT 1")
    Step getLatestStepData(int date);

    @Query("SELECT SUM(stepCount) FROM stepCount WHERE date = :date")
    int getTotalStepsByDate(int date);

    @Query("SELECT * FROM stepCount ORDER BY timeStamp ASC LIMIT 1")
    Step getFirstStepData();
}
