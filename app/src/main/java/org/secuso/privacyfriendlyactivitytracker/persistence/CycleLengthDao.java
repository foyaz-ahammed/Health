package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CycleLengthDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(CycleLength cycleLength);

    @Query("SELECT * FROM cycleLength LIMIT 1")
    LiveData<CycleLength> getCycleLengthLiveData();

    @Query("SELECT * FROM cycleLength LIMIT 1")
    CycleLength getCycleLengthData();
}
