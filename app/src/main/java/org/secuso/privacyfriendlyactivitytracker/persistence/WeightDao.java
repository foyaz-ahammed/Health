package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WeightDao {

    @Query("SELECT * FROM weight ORDER BY measureMilliTime DESC, modifiedTime DESC")
    LiveData<List<Weight>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Weight weight);

    @Query("SELECT * FROM weight WHERE measureDate = :date ORDER BY measureMilliTime DESC, modifiedTime DESC")
    List<Weight> getDayData(String date);

    @Query("SELECT AVG(CAST(weightValue as REAL)) AS weightAvg, AVG(CAST(fatRateValue as REAL)) AS fatRateAvg, measureDate, measureMilliTime " +
            "FROM weight WHERE measureMilliTime >= :start AND measureMilliTime <= :end GROUP BY measureDate ORDER BY measureMilliTime ASC")
    LiveData<List<AvgType>> getWeekData(long start, long end);

    @Query("SELECT AVG(CAST(weightValue as REAL)) AS weightAvg, AVG(CAST(fatRateValue as REAL)) AS fatRateAvg, measureDate, measureMilliTime " +
            "FROM weight WHERE measureMilliTime >= :start AND measureMilliTime <= :end GROUP BY measureDate ORDER BY measureMilliTime ASC")
    LiveData<List<AvgType>> getMonthData(long start, long end);

    @Query("SELECT AVG(CAST(weightValue as REAL)) AS weightAvg, AVG(CAST(fatRateValue as REAL)) AS fatRateAvg, measureDate, measureMilliTime " +
            "FROM weight WHERE month LIKE '%' || :year || '%' GROUP BY month ORDER BY measureMilliTime ASC, modifiedTime ASC")
    LiveData<List<AvgType>> getYearData(String year);

    @Query("SELECT * FROM weight ORDER BY measureMilliTime DESC, modifiedTime DESC LIMIT 3")
    LiveData<List<Weight>> getThreeData();

    @Query("SELECT AVG(CAST(weightValue as REAL)) as weightAvg, measureDate, measureMilliTime " +
            "FROM weight GROUP BY measureDate ORDER BY measureMilliTime DESC")
    List<AvgType> getDayList();

    @Query("SELECT * FROM (SELECT * FROM weight ORDER BY measureMilliTime ASC, modifiedTime ASC) as T " +
            "GROUP BY T.measureDate ORDER BY T.measureMilliTime DESC, T.modifiedTime DESC")
    LiveData<List<Weight>> getAllDayData();

    @Query("DELETE FROM weight WHERE _id IN (:selectedItems)")
    void delete(List<Integer> selectedItems);

    @Query("SELECT * FROM weight ORDER BY measureMilliTime DESC, modifiedTime DESC LIMIT 1")
    LiveData<Weight> getLatestData();

    @Query("SELECT * FROM weight ORDER BY measureMilliTime DESC, modifiedTime DESC LIMIT 1")
    Weight getLatestWeight();

    class AvgType {
        public String weightAvg;
        public String fatRateAvg;
        public String measureDate;
        public long measureMilliTime;

        public String getWeightAvg() {
            return weightAvg;
        }

        public String getFatRateAvg() {
            return fatRateAvg;
        }

        public String getMeasureDate() {
            return measureDate;
        }

        public long getMeasureMilliTime() {
            return measureMilliTime;
        }
    }

}
