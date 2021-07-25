package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HeartRateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(HeartRate heartRate);

    @Query("SELECT * FROM heartRate WHERE day = :day ORDER BY measureTime ASC")
    LiveData<List<HeartRate>> getDayData(String day);

    @Query("SELECT MAX(pulseValue) as max, MIN(pulseValue) as min, measureTime FROM heartRate " +
            "WHERE measureTime >= :start AND measureTime <= :end GROUP BY day ORDER BY measureTime")
    LiveData<List<MaxMin>> getPeriodData(long start, long end);

    @Query("SELECT MAX(pulseValue) as max, MIN(pulseValue) as min, measureTime FROM heartRate " +
            "WHERE month LIKE '%' || :year || '%' GROUP BY month ORDER BY measureTime")
    LiveData<List<MaxMin>> getYearData(int year);

    @Query("SELECT * FROM heartRate ORDER BY measureTime DESC LIMIT 1")
    LiveData<HeartRate> getLatestData();

    class MaxMin {
        int max;
        int min;
        long measureTime;

        public int getMax() {
            return max;
        }

        public int getMin() {
            return min;
        }

        public long getMeasureTime() {
            return measureTime;
        }
    }
}
