package org.secuso.privacyfriendlyactivitytracker.models;

import android.view.View;

/**
 * 개별적인 운동기록자료를 담고 있는 Object
 */
public class ExerciseInfo {
    int exerciseType; // 운동형태 0: 모두 1: 달리기 2: 걷기 3: 자전거타기 5: 수영
    // 현시형태 "total": 총자료 "workout": 운동항목 "workout_divider": 운동항목별 가름선
    // "month": 월별자료 "month_divider": 월별 가름선
    String viewType;
    WorkoutInfo info; // 운동자료
    TotalInfo totalRunning = new TotalInfo(); // 월별 총 달린 자료
    TotalInfo totalWalking = new TotalInfo(); // 월별 총 걸은 자료
    TotalInfo totalCycling = new TotalInfo(); // 월별 총 자전거타기한 자료
    TotalInfo totalSwimming = new TotalInfo(); // 월별 총 수영한 거리
    int visibility = View.VISIBLE; // 월 보임상태

    public ExerciseInfo() {}

    public ExerciseInfo(int exerciseType, String type, WorkoutInfo info, int visibility) {
        this.exerciseType = exerciseType;
        this.viewType = type;
        this.info = info;
        this.visibility = visibility;
    }

    public void setExerciseType(int exerciseType) {
        this.exerciseType = exerciseType;
    }

    public void setViewType(String type) {
        this.viewType = type;
    }

    public void setInfo(WorkoutInfo info) {
        this.info = info;
    }

    public void setTotalRunning(TotalInfo totalRunning) {
        this.totalRunning = totalRunning;
    }

    public void setTotalWalking(TotalInfo totalWalking) {
        this.totalWalking = totalWalking;
    }

    public void setTotalCycling(TotalInfo totalCycling) {
        this.totalCycling = totalCycling;
    }

    public void setTotalSwimming(TotalInfo totalSwimming) {
        this.totalSwimming = totalSwimming;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public int getExerciseType() {
        return exerciseType;
    }

    public String getViewType() {
        return viewType;
    }

    public WorkoutInfo getInfo() {
        return info;
    }

    public TotalInfo getTotalRunning() {
        return totalRunning;
    }

    public TotalInfo getTotalWalking() {
        return totalWalking;
    }

    public TotalInfo getTotalSwimming() {
        return totalSwimming;
    }

    public TotalInfo getTotalCycling() {
        return totalCycling;
    }

    public int getVisibility() {
        return visibility;
    }

    public static class TotalInfo {
        float total;
        int count;

        public TotalInfo() {}

        public TotalInfo(float total, int count) {
            this.total = total;
            this.count = count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public void setTotal(float total) {
            this.total = total;
        }

        public float getTotal() {
            return total;
        }

        public int getCount() {
            return count;
        }
    }
}
