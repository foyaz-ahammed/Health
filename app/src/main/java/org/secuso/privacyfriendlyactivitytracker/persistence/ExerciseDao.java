package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Exercise exercise);

    @Query("SELECT * FROM exercise GROUP BY month ORDER BY startTime DESC")
    List<Exercise> getMonthList();

    @Query("SELECT * FROM exercise WHERE activity =:type GROUP BY month ORDER BY startTime DESC")
    List<Exercise> getMonthListByType(int type);

    @Query("SELECT * FROM exercise WHERE activity = 1 OR activity = 4 GROUP BY month ORDER BY startTime DESC")
    List<Exercise> getRunningMonthList();

    @Query("SELECT * FROM exercise ORDER BY startTime DESC")
    LiveData<List<Exercise>> getAllData();

    @Query("SELECT * FROM exercise WHERE activity = 1 OR activity = 4 ORDER BY startTime DESC")
    LiveData<List<Exercise>> getRunningData();

    @Query("SELECT * FROM exercise WHERE activity = 2 ORDER BY startTime DESC")
    LiveData<List<Exercise>> getWalkingData();

    @Query("SELECT * FROM exercise WHERE activity = 3 ORDER BY startTime DESC")
    LiveData<List<Exercise>> getCyclingData();

    @Query("SELECT * FROM exercise WHERE activity = 5 ORDER BY startTime DESC")
    LiveData<List<Exercise>> getSwimmingData();

    @Query("SELECT SUM(distance) as sum, count(*) as exercise_count, (CASE WHEN activity = 1 OR activity = 4 THEN 1 ELSE activity END) " +
            "as new_activity FROM exercise WHERE month = :month GROUP BY new_activity")
    List<DistanceTotal> getTotalData(String month);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE startTime >= :start AND startTime <= :end AND (activity == 1 OR activity == 4) " +
            "GROUP BY date ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getRunningWeekData(long start, long end);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE startTime >= :start AND startTime <= :end AND activity = :activity " +
            "GROUP BY date ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getWeekDataByActivity(long start, long end, int activity);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE startTime >= :start AND startTime <= :end AND (activity == 1 OR activity == 4) " +
            "GROUP BY date ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getRunningMonthData(long start, long end);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE startTime >= :start AND startTime <= :end AND activity = :activity " +
            "GROUP BY date ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getMonthDataByActivity(long start, long end, int activity);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE month LIKE '%' || :year || '%' AND (activity == 1 OR activity == 4) " +
            "GROUP BY month ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getRunningYearData(String year);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE month LIKE '%' || :year || '%' AND activity = :activity " +
            "GROUP BY month ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getYearDataByActivity(String year, int activity);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE startTime >= :start AND startTime <= :end AND (activity == 1 OR activity == 4) " +
            "GROUP BY year ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getRunningTotalData(long start, long end);

    @Query("SELECT activity, SUM(distance) as totalDistance, SUM(longDuration) as totalDuration, COUNT(*) as count, startTime " +
            "FROM exercise WHERE startTime >= :start AND startTime <= :end AND activity = :activity " +
            "GROUP BY year ORDER BY startTime ASC")
    LiveData<List<DayTotal>> getTotalDataByActivity(long start, long end, int activity);

    @Query("SELECT * FROM exercise ORDER BY startTime DESC, modifiedTime DESC LIMIT 1")
    LiveData<List<Exercise>> getLatestData();

    @Query("SELECT SUM(distance) FROM exercise WHERE activity == 1")
    LiveData<Float> getTotalOutdoorRunDistance();

    @Query("SELECT SUM(distance) FROM exercise WHERE activity == 4")
    LiveData<Float> getTotalIndoorRunDistance();

    @Query("SELECT SUM(distance) FROM exercise WHERE activity == 2")
    LiveData<Float> getTotalWalkDistance();

    @Query("DELETE FROM exercise WHERE _id = :id")
    void deleteById(int id);

    class DistanceTotal {
        float sum;
        int new_activity;
        int exercise_count;

        public float getSum() {
            return sum;
        }

        public int getActivity() {
            return new_activity;
        }

        public int getCount() {
            return exercise_count;
        }
    }

    class DayTotal {
        int activity;
        float totalDistance;
        long totalDuration;
        int count;
        long startTime;

        public int getActivity() {
            return activity;
        }

        public float getTotalDistance() {
            return totalDistance;
        }

        public long getTotalDuration() {
            return totalDuration;
        }

        public int getCount() {
            return count;
        }

        public long getStartTime() {
            return startTime;
        }
    }
}
