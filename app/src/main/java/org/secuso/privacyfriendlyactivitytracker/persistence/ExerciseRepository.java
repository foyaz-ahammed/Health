package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao.DistanceTotal;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao.DayTotal;

import java.util.List;

/**
 * 운동관련 repository
 */
public class ExerciseRepository {
    public ExerciseDao mExerciseDao;

    public ExerciseRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mExerciseDao = db.ExerciseDao();
    }

    /**
     * 마지막자료 얻는 함수
     * @return 마지막자료
     */
    public LiveData<List<Exercise>> getLatestData () {
        return mExerciseDao.getLatestData();
    }

    /**
     * 월목록 얻는 함수
     * @return 월목록
     */
    public List<Exercise> getMonthList() {
        return mExerciseDao.getMonthList();
    }

    /**
     * 종목에 관해 월목록 얻는 함수
     * @param type 종목
     * @return 월목록
     */
    public List<Exercise> getMonthListByType(int type) {
        if (type != 1) {
            return mExerciseDao.getMonthListByType(type);
        } else {
            return mExerciseDao.getRunningMonthList();
        }
    }

    /**
     * 모든 자료 얻는 함수
     * @return 모든 자료
     */
    public LiveData<List<Exercise>> getAllData() {
        return mExerciseDao.getAllData();
    }

    /**
     * 달리기 자료 얻는 함수
     * @return 달리기자료
     */
    public LiveData<List<Exercise>> getRunningData() {
        return mExerciseDao.getRunningData();
    }

    /**
     * 걷기 자료 얻는 함수
     * @return 걷기자료
     */
    public LiveData<List<Exercise>> getWalkingData() {
        return mExerciseDao.getWalkingData();
    }

    /**
     * 자전거타기 자료 얻는 함수
     * @return 자전거타기 자료
     */
    public LiveData<List<Exercise>> getCyclingData() {
        return mExerciseDao.getCyclingData();
    }

    /**
     * 수영자료얻는함수
     * @return 수영자료
     */
    public LiveData<List<Exercise>> getSwimmingData() {
        return mExerciseDao.getSwimmingData();
    }

    /**
     * 총자료 얻는 함수
     * @param month 자료를 얻을 월
     * @return 자료
     */
    public List<DistanceTotal> getTotalData(String month) {
        return mExerciseDao.getTotalData(month);
    }

    /**
     * 주자료 얻는 함수
     * @param start 주시작
     * @param end 주마감
     * @param activity 종목
     * @return 자료
     */
    public LiveData<List<DayTotal>> getWeekData(long start, long end, int activity) {
        if (activity == 1)
            return mExerciseDao.getRunningWeekData(start, end);
        else return mExerciseDao.getWeekDataByActivity(start, end, activity);
    }

    /**
     * 월자료 얻는 함수
     * @param start 월시작
     * @param end 월마감
     * @param activity 종목
     * @return 자료
     */

    public LiveData<List<DayTotal>> getMonthData(long start, long end, int activity) {
        if (activity == 1)
            return mExerciseDao.getRunningMonthData(start, end);
        else return mExerciseDao.getMonthDataByActivity(start, end, activity);
    }

    /**
     * 년자료 얻는 함수
     * @param year 년도
     * @param activity 종목
     * @return 자료
     */
    public LiveData<List<DayTotal>> getYearData(int year, int activity) {
        if (activity == 1)
            return mExerciseDao.getRunningYearData(String.valueOf(year));
        else return mExerciseDao.getYearDataByActivity(String.valueOf(year), activity);
    }

    /**
     * 총자료 얻는 함수
     * @param start 시작년도
     * @param end 마감년도
     * @param activity 종목
     * @return 자료
     */
    public LiveData<List<DayTotal>> getTotalData(long start, long end, int activity) {
        if (activity == 1)
            return mExerciseDao.getRunningTotalData(start, end);
        else return mExerciseDao.getTotalDataByActivity(start, end, activity);
    }

    /**
     * 전체 실외달리기 거리 얻는 함수
     * @return 거리
     */
    public LiveData<Float> getTotalOutdoorRunDistance() {
        return mExerciseDao.getTotalOutdoorRunDistance();
    }

    /**
     * 전체 실내달리기 거리 얻는 함수
     * @return 거리
     */
    public LiveData<Float> getTotalIndoorRunDistance() {
        return mExerciseDao.getTotalIndoorRunDistance();
    }

    /**
     * 전체 걸은 거리 얻는 함수
     * @return 거리
     */
    public LiveData<Float> getTotalWalkDistance() {
        return mExerciseDao.getTotalWalkDistance();
    }

    /**
     * 자료 추가 및 갱신하는 함수
     */
    public void insertOrUpdate(WorkoutInfo info) {
        AsyncTask.execute(() -> {
            Exercise exercise = new Exercise(info);
            mExerciseDao.insertOrUpdate(exercise);
        });
    }

    /**
     * 자료삭제 하는 함수
     * @param id 삭제할 자료의 id
     */
    public void deleteData(int id) {
        AsyncTask.execute(() -> mExerciseDao.deleteById(id));
    }

}
