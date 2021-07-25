package org.secuso.privacyfriendlyactivitytracker.persistence;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "healthOrder")
public class HealthOrder {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    public int _id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "order")
    public int order;

    @ColumnInfo(name = "isShown")
    public int isShown;

    public HealthOrder(int id, String name, int order, int isShown) {
        this._id = id;
        this.name = name;
        this.order = order;
        this.isShown = isShown;
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return name;
    }

    public int getOrder() {
        return order;
    }

    public int getIsShown() {
        return isShown;
    }
}
