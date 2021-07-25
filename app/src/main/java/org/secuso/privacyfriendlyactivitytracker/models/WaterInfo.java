package org.secuso.privacyfriendlyactivitytracker.models;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;

/**
 * 개별적인 물기록 object
 */
public class WaterInfo extends HistoryItemContainer.HistoryItemInfo {
    private int glasses; // 고뿌수
    private DateTime measureDate; // 측정한 날자
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public void setGlasses(int glasses) {
        this.glasses = glasses;
    }

    public void setMeasureDateTime(DateTime measureDate) {
        this.measureDate = measureDate;
    }

    public int getId() {
        return id;
    }

    public int getGlasses() {
        return glasses;
    }

    public DateTime getMeasureDateTime() {
        return measureDate;
    }
}
