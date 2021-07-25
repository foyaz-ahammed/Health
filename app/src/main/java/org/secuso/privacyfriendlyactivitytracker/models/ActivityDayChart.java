package org.secuso.privacyfriendlyactivitytracker.models;

import java.util.Map;

/**
 * 일별 걸음수그라프자료를 담고있는 Object
 */
public class ActivityDayChart {
    private String title; // 기간
    private Map<String, ActivityChartDataSet> steps; // 걸음수자료
    private Map<String, ActivityChartDataSet> distance; // 걸은 거리자료
    private Map<String, ActivityChartDataSet> calories; // 소비한 카로리자료
    private DataType displayedDataType; // 현시할 그라프형태 0: 걸음수, 1: 거리, 2: 카로리
    private int goal; // 하루 걸음목표

    public ActivityDayChart(Map<String, ActivityChartDataSet> steps, Map<String, ActivityChartDataSet> distance, Map<String, ActivityChartDataSet> calories, String title) {
        this.steps = steps;
        this.title = title;
        this.distance = distance;
        this.calories = calories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, ActivityChartDataSet> getSteps() {
        return steps;
    }

    public void setSteps(Map<String, ActivityChartDataSet> steps) {
        this.steps = steps;
    }

    public Map<String, ActivityChartDataSet> getDistance() {
        return distance;
    }

    public void setDistance(Map<String, ActivityChartDataSet> distance) {
        this.distance = distance;
    }

    public Map<String, ActivityChartDataSet> getCalories() {
        return calories;
    }

    public void setCalories(Map<String, ActivityChartDataSet> calories) {
        this.calories = calories;
    }

    public DataType getDisplayedDataType() {
        return displayedDataType;
    }

    public void setDisplayedDataType(DataType displayedDataType) {
        this.displayedDataType = displayedDataType;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public enum DataType {
        STEPS, DISTANCE, CALORIES
    }
}
