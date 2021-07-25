package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * 걸음수관련 repository
 */
public class StepCountRepository {
    public StepCountDao mStepCountDao;
    public WalkingModesDao mWalkingModesDao;

    public StepCountRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mStepCountDao = db.stepCountDao();
        mWalkingModesDao = db.walkingModesDao();
    }

    /**
     * 걸음수 자료 추가 및 갱신함수
     * @param step 추가 및 갱신될 걸음수
     */
    public void insertOrUpdateStep(Step step) {
        AsyncTask.execute(() -> mStepCountDao.insertOrUpdate(step));
    }

    /**
     * 일별 걸음수 자료 얻는 함수
     * @param date 날자
     * @return 걸음수 목록
     */
    public LiveData<List<Step>> getDayStepData(int date) {
        return mStepCountDao.getDayStepData(date);
    }

    /**
     * 기간별 걸음수자료 얻는 함수
     * @param start 시작날자
     * @param end 마감날자
     * @return 걸음수 목록
     */
    public LiveData<List<Step>> getWeekStepData(int start, int end) {
        return mStepCountDao.getWeekStepData(start, end);
    }

    public LiveData<List<Step>> getMonthStepData(int start, int end) {
        return mStepCountDao.getWeekStepData(start, end);
    }

    /**
     * 마지막 걸음수 자료 얻는 함수
     * @param date 날자
     * @return 걸음수
     */
    public Step getLatestStepData(int date) {
        return mStepCountDao.getLatestStepData(date);
    }

    /**
     * 날자에 관한 걸음수 얻는 함수
     * @param date 날자
     * @return 걸음수
     */
    public int getTotalStepsByDate(int date) {
        return mStepCountDao.getTotalStepsByDate(date);
    }

    /**
     * 첫 걸음수자료를 얻는 함수
     * @return 첫 걸음수
     */
    public Step getFirstStepData() {
        return mStepCountDao.getFirstStepData();
    }

    /**
     * 설정된 걸음방식 얻는 함수
     * @return 설정된 걸음방식
     */
    public WalkingModes getActiveWalkingMode() {
        return mWalkingModesDao.getActiveWalkingMode();
    }

    /**
     * 모든 걸음방식을 얻는 함수 (LiveData)
     * @return 걸음방식 목록
     */
    public List<WalkingModes> getAllWalkingModes() {
        return mWalkingModesDao.getAllWalkingModes();
    }

    /**
     * 모든 걸음방식을 얻는 함수
     * @return 걸음방식 목록
     */
    public LiveData<List<WalkingModes>> getWalkingModes() {
        return mWalkingModesDao.getWalkingModes();
    }

    /**
     * 현재 리용하는 걸음방식을 설정하는 함수
     * @param walkingMode 설정될 걸음방식
     */
    public void updateActiveMode(WalkingModes walkingMode) {
        AsyncTask.execute(() -> mWalkingModesDao.insertOrUpdate(walkingMode));
    }

    /**
     * 걸음보폭수 갱신함수
     * @param walkingStepSize 걸음보폭수
     */
    public void updateWalkingStepSize(Double walkingStepSize) {
        AsyncTask.execute(() -> mWalkingModesDao.updateWalkingStepSize(walkingStepSize));
    }

    /**
     * 달리기보폭수 갱신함수
     * @param runningStepSize 달리기보폭수
     */
    public void updateRunningStepSize(Double runningStepSize) {
        AsyncTask.execute(() -> mWalkingModesDao.updateRunningStepSize(runningStepSize));
    }
}
