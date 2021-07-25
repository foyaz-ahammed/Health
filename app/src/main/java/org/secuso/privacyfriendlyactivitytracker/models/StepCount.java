package org.secuso.privacyfriendlyactivitytracker.models;

import org.secuso.privacyfriendlyactivitytracker.persistence.WalkingModes;
import org.secuso.privacyfriendlyactivitytracker.utils.UnitHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 걸음수관련 object
 */

public class StepCount {

    // from https://github.com/bagilevi/android-pedometer/blob/master/src/name/bagi/levente/pedometer/CaloriesNotifier.java
    private static double METRIC_RUNNING_FACTOR = 1.02784823;
    private static double METRIC_WALKING_FACTOR = 0.708;
    private static double METRIC_AVG_FACTOR = (METRIC_RUNNING_FACTOR + METRIC_WALKING_FACTOR) / 2;


    private int stepCount; // 걸음수
    private long startTime; // 시작시간
    private long endTime; // 마감시간
    private WalkingModes walkingMode; // 걸음방식

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public WalkingModes getWalkingMode() {
        return walkingMode;
    }

    public void setWalkingMode(WalkingModes walkingMode) {
        this.walkingMode = walkingMode;
    }

    /**
     * 걸은 거리를 얻는 함수
     * @return 걸은 거리 (m)
     */
    public double getDistance(){
        if(getWalkingMode() != null) {
            return getStepCount() * getWalkingMode().getStepSize();
        }else{
            return 0;
        }
    }

    /**
     * 소비한 카로리를 얻는 함수
     * @return 소비한 카로리 (cal)
     */
    public double getCalories(){
        if (getWalkingMode() != null)
            return UnitHelper.metersToKilometers(getDistance()) * (getWalkingMode().getName().equals("Walking") ? 30 : 60);
        else return 0;
    }

    @Override
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyy HH:mm:ss");
        return "StepCount{" + format.format(new Date(startTime)) +
                " - " + format.format(new Date(endTime)) +
                ": " + stepCount + " @ " + ((walkingMode == null) ? -1 : walkingMode.getId())+
                '}';
    }
}
