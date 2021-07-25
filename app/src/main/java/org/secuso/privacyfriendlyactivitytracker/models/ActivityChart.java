package org.secuso.privacyfriendlyactivitytracker.models;

import java.util.Map;

/**
 * 걸음수관련 자료를 담고있는 Object
 */
public class ActivityChart {
    private String title; // 기간
    private Map<String, Double> steps; // 걸음수자료
    private Map<String, Double> distance; // 걸은 거리자료
    private Map<String, Double> calories; // 소비한 카로리자료
    private ActivityDayChart.DataType displayedDataType; // 현시할 그라프형태 0: 걸음수, 1: 거리, 2: 카로리
    private int goal; // 하루 걸음목표

    public ActivityChart(Map<String, Double> steps, Map<String, Double> distance, Map<String, Double> calories, String title) {
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

    public Map<String, Double> getSteps() {
        return steps;
    }

    public void setSteps(Map<String, Double> steps) {
        this.steps = steps;
    }

    public Map<String, Double> getDistance() {
        return distance;
    }

    public void setDistance(Map<String, Double> distance) {
        this.distance = distance;
    }

    public Map<String, Double> getCalories() {
        return calories;
    }

    public void setCalories(Map<String, Double> calories) {
        this.calories = calories;
    }

    public ActivityDayChart.DataType getDisplayedDataType() {
        return displayedDataType;
    }

    public void setDisplayedDataType(ActivityDayChart.DataType displayedDataType) {
        this.displayedDataType = displayedDataType;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

}
