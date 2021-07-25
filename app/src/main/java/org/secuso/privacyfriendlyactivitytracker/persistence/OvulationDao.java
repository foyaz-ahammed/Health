package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OvulationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Ovulation ovulation);

    @Query("DELETE FROM ovulation WHERE _id = :id")
    void deleteById(int id);

    @Query("DELETE FROM ovulation WHERE isPredict = 1")
    void deletePredictData();

    @Query("SELECT * FROM (SELECT * FROM ovulation WHERE periodStart <= :date ORDER BY periodStart DESC LIMIT 1) AS A"
            + " UNION "
            + "SELECT * FROM (SELECT * FROM ovulation WHERE periodStart > :date ORDER BY periodStart ASC LIMIT 1) AS B ORDER BY periodStart ASC")
    LiveData<List<Ovulation>> getOvulationByDate(int date);

    @Query("SELECT * FROM ovulation WHERE periodStart < :date ORDER BY periodStart DESC LIMIT 1")
    Ovulation getPrevOvulation(int date);

    @Query("SELECT * FROM ovulation WHERE periodStart > :date AND isPredict = 0 ORDER BY periodStart ASC LIMIT 1")
    Ovulation getNextOvulation(int date);

    @Query("SELECT * FROM (SELECT * FROM ovulation WHERE periodStart < :date ORDER BY periodStart DESC LIMIT 1) AS A"
            + " UNION "
            + "SELECT * FROM (SELECT * FROM ovulation WHERE periodStart >= :date ORDER BY periodStart ASC LIMIT 1) AS B ORDER BY periodStart ASC")
    LiveData<List<Ovulation>> getOvulationForStatus(int date);

    @Query("SELECT * FROM (SELECT * FROM ovulation WHERE periodStart < :date ORDER BY periodStart DESC LIMIT 1) AS A"
            + " UNION "
            + "SELECT * FROM (SELECT * FROM ovulation WHERE periodStart >= :date ORDER BY periodStart ASC LIMIT 2) AS B ORDER BY periodStart ASC")
    LiveData<List<Ovulation>> getOvulationForTotal(int date);

    @Query("SELECT * FROM ovulation WHERE (periodStart >= :start AND periodStart <= :end) OR " +
            "(periodEnd >= :start AND periodEnd <= :end) OR (fertileStart >= :start AND fertileStart <= :end) OR " +
            "(fertileEnd >= :start AND fertileEnd <= :end) ORDER BY periodStart")
    LiveData<List<Ovulation>> getMonthData(int start, int end);

    @Query("SELECT * FROM ovulation WHERE isPredict = 0 ORDER BY periodStart DESC LIMIT 1")
    Ovulation getLastOvulation();

    @Query("SELECT * FROM (SELECT * FROM ovulation WHERE isPredict = 0 ORDER BY periodStart DESC LIMIT 6) AS A " +
            " UNION " +
            "SELECT * FROM (SELECT * FROM ovulation WHERE isPredict = 1 ORDER BY periodStart ASC LIMIT 1) AS B ORDER BY periodStart DESC")
    LiveData<List<Ovulation>> getOvulationForStats();

    @Query("SELECT COUNT(_id) FROM ovulation")
    int getOvulationCount();
}
