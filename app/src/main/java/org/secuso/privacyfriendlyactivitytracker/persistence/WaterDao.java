package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WaterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Water water);

    @Query("SELECT * FROM water WHERE measureDate == :date")
    LiveData<Water> getDayData(long date);

    @Query("SELECT * FROM water WHERE measureDate >= :start AND measureDate <= :end ORDER BY measureDate ASC")
    LiveData<List<Water>> getWeekData(long start, long end);

    @Query("SELECT * FROM water WHERE measureDate >= :start AND measureDate <= :end ORDER BY measureDate ASC")
    LiveData<List<Water>> getMonthData(long start, long end);

    @Query("SELECT * FROM water ORDER BY measureDate DESC LIMIT 1")
    LiveData<List<Water>> getLatestData();
}
