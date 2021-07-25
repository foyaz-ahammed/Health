package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WalkingModesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(WalkingModes walkingModes);

    @Query("SELECT * FROM walkingModes WHERE isActive = 1")
    WalkingModes getActiveWalkingMode();

    @Query("SELECT * FROM walkingModes WHERE _id = :id")
    WalkingModes getWalkingModeById(int id);

    @Query("SELECT * FROM walkingModes")
    List<WalkingModes> getAllWalkingModes();

    @Query("SELECT * FROM walkingModes")
    LiveData<List<WalkingModes>> getWalkingModes();

    @Query("UPDATE walkingModes SET stepSize = :walkingStepSize WHERE name == 'Walking'")
    void updateWalkingStepSize(Double walkingStepSize);

    @Query("UPDATE walkingModes SET stepSize = :runningStepSize WHERE name == 'Running'")
    void updateRunningStepSize(Double runningStepSize);

    @Query("SELECT stepSize FROM walkingModes WHERE name == 'Walking' LIMIT 1")
    float getWalkingStepLength();

    @Query("SELECT stepSize FROM walkingModes WHERE name == 'Running' LIMIT 1")
    float getRunningStepLength();
}