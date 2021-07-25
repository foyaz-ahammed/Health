package org.secuso.privacyfriendlyactivitytracker.models;

/**
 * 걸음수상태를 보여주는 부분에 표시할 자료들을 담고있는 Object
 */
public class ActivitySummary {
    private int steps; // 걸음수
    private double distance; // 걸은 거리
    private double calories; // 소비한 카로리
    private String title; // 현시할 기간
    private Float currentSpeed = null; // 속도
    // 다음에 현시할 후임기간이 있는지 판별
    private boolean hasSuccessor;
    // 다음에 현시할 이전 기간이 있는지 판별
    private boolean hasPredecessor;

    public ActivitySummary(int steps, double distance, int calories) {
        this(steps, distance, calories, "");
    }

    public ActivitySummary(int steps, double distance, double calories, String title) {
        this.steps = steps;
        this.distance = distance;
        this.calories = calories;
        this.title = title;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getCalories() {
        return calories;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHasSuccessor() {
        return hasSuccessor;
    }

    public void setHasSuccessor(boolean hasSuccessor) {
        this.hasSuccessor = hasSuccessor;
    }

    public boolean isHasPredecessor() {
        return hasPredecessor;
    }

    public void setHasPredecessor(boolean hasPredecessor) {
        this.hasPredecessor = hasPredecessor;
    }

    public Float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(Float currentSpeed) {
        this.currentSpeed = currentSpeed;
    }
}
