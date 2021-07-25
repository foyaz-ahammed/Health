package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HealthOrderDao {
    @Query("SELECT * FROM healthOrder ORDER BY `order` ASC")
    LiveData<List<HealthOrder>> getHealthOrder();

    @Query("SELECT * FROM healthOrder ORDER BY `order` ASC")
    List<HealthOrder> getOrders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(HealthOrder eachItemOrder);

    @Query("Delete FROM healthOrder")
    void deleteAll();
}
