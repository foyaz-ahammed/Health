package org.secuso.privacyfriendlyactivitytracker.models;

import android.content.Context;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer.HistoryItemInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.Exercise;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao.DayTotal;

import java.io.Serializable;

/**
 * 개별적인 운동기록자료 Object
 */
public class WorkoutInfo extends HistoryItemInfo implements Serializable {
    int id;
    int activity; // 운동형태
    String duration; // 운동한 시간
    long longDuration; // 운동한 시간
    float distance; // 운동한 거리
    String year; // 운동한 년
    String month; // 운동한 월
    String date; // 운동한 날자
    DateTime startTime; // 운동시작시간

    int OUTDOOR_RUN = 1;
    int OUTDOOR_WALK = 2;
    int OUTDOOR_CYCLE = 3;
    int INDOOR_RUN = 4;
    int POOL_SWIM = 5;

    public WorkoutInfo() {}

    public WorkoutInfo(DayTotal dayTotal) {
        this.distance = dayTotal.getTotalDistance();
        this.startTime = new DateTime(dayTotal.getStartTime());
    }

    public WorkoutInfo(Exercise exercise) {
        this.id = exercise.getId();
        this.activity = exercise.getActivity();
        this.duration = exercise.getDuration();
        this.longDuration = exercise.getLongDuration();
        this.distance = exercise.getDistance();
        this.year = exercise.getYear();
        this.month = exercise.getMonth();
        this.date = exercise.getDate();
        this.startTime = new DateTime(exercise.getStartTime());
        this.modifiedTime = exercise.getModifiedTime();
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setActivity(int activity) {
        this.activity = activity;
    }

    public int getActivity() {
        return activity;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDuration() {
        return duration;
    }

    public void setLongDuration(long longDuration) {
        this.longDuration = longDuration;
    }

    public long getLongDuration() {
        return longDuration;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getDistance() {
        return distance;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return year;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getMonth() {
        return month;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getStartTime() {
        return startTime;
    }

    /**
     * 평균 pace 를 얻는 함수
     * @param context The application context
     * @return 얻은 평균 pace
     */
    public String getAveragePace(Context context) {
        int durationHour, durationMinute, durationSeconds, durationTime;
        float pace;
        int paceMinute, paceSecond;

        durationHour = Integer.parseInt(duration.substring(0,2));
        durationMinute = Integer.parseInt(duration.substring(3,5));
        durationSeconds = Integer.parseInt(duration.substring(6,8));

        //초수에 관하여 pace 값 계산
        durationTime = durationSeconds + durationMinute * 60 + durationHour * 60 * 60;
        if (activity == POOL_SWIM)
            pace = durationTime * 100 / distance;
        else
            pace = durationTime / distance;
        paceMinute = (int) (pace / 60);
        paceSecond = (int) (pace - paceMinute * 60);
        return context.getResources().getString(R.string.average_pace_format, paceMinute, paceSecond);
    }

    /**
     * 평균속도를 얻는 함수
     * @return 계산된 평균속도
     */
    public float getAverageSpeed() {
        int durationHour, durationMinute, durationSeconds, durationTime;
        float speed;

        durationHour = Integer.parseInt(duration.substring(0,2));
        durationMinute = Integer.parseInt(duration.substring(3,5));
        durationSeconds = Integer.parseInt(duration.substring(6,8));

        //초수에 관하여 평균속도 계산
        durationTime = durationSeconds + durationMinute * 60 + durationHour * 60 * 60;
        speed = distance / durationTime * 3600;
        return speed;
    }

    /**
     * 운동한 거리에 준하여 소비한 카로리를 계산하는 함수
     * @return 계산된 카로리수
     */
    public int getCalories() {
        if (activity == OUTDOOR_RUN || activity == INDOOR_RUN) {
            return (int) (distance * 60);
        } else if (activity == OUTDOOR_WALK) {
            return (int) (distance * 30);
        } else if (activity == OUTDOOR_CYCLE) {
            return (int) (distance * 19);
        } else {
            return (int) (distance / 500 * 78);
        }
    }

}
