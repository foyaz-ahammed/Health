package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BloodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(Blood blood);

    @Query("SELECT * FROM blood ORDER BY measureMilliTime DESC, modifiedTime DESC")
    LiveData<List<Blood>> getAllData();

    @Query("DELETE FROM blood WHERE _id IN (:selectedItems)")
    void delete(List<Integer> selectedItems);

    @Query("SELECT * FROM blood ORDER BY measureMilliTime DESC, modifiedTime DESC LIMIT 1")
    LiveData<List<Blood>> getLatestData();
}
