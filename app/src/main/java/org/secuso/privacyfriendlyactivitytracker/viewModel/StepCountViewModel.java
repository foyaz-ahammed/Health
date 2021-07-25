package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.secuso.privacyfriendlyactivitytracker.persistence.Step;
import org.secuso.privacyfriendlyactivitytracker.persistence.StepCountRepository;
import org.secuso.privacyfriendlyactivitytracker.persistence.WalkingModes;

import java.util.List;

/**
 * 걸음수관련 viewModel
 */
public class StepCountViewModel extends AndroidViewModel {
    StepCountRepository mRepository;

    public LiveData<List<Step>> dayStepData; // 일별 걸음수자료
    MutableLiveData<Integer> date = new MutableLiveData<>(); // 선택한 날자

    public LiveData<List<Step>> weekStepData; // 주별 걸음수자료
    MutableLiveData<List<Integer>> week = new MutableLiveData<>(); // 주 시작 및 마감날자

    public LiveData<List<Step>> monthStepData; // 월별 걸음수자료
    MutableLiveData<List<Integer>> month = new MutableLiveData<>(); // 월 시작 및 마감날자

    public StepCountViewModel(@NonNull Application application) {
        super(application);
        mRepository = new StepCountRepository(application);
    }

    /**
     * 일별 현시화면을 위한 초기화함수
     */
    public void instanceForDay() {
        dayStepData = Transformations.switchMap(date, new Function<Integer, LiveData<List<Step>>>() {
            @Override
            public LiveData<List<Step>> apply(Integer date) {
                return mRepository.getDayStepData(date);
            }
        });
    }

    /**
     * 주별 현시화면을 위한 초기화함수
     */
    public void instanceForWeek() {
        weekStepData = Transformations.switchMap(week, new Function<List<Integer>, LiveData<List<Step>>>() {
            @Override
            public LiveData<List<Step>> apply(List<Integer> week) {
                return mRepository.getWeekStepData(week.get(0), week.get(1));
            }
        });
    }

    /**
     * 월별 현시화면을 위한 초기화함수
     */
    public void instanceForMonth() {
        monthStepData = Transformations.switchMap(month, new Function<List<Integer>, LiveData<List<Step>>>() {
            @Override
            public LiveData<List<Step>> apply(List<Integer> month) {
                return mRepository.getWeekStepData(month.get(0), month.get(1));
            }
        });
    }

    /**
     * 날자설정함수
     * @param date 날자
     */
    public void setDate(int date) {
        this.date.setValue(date);
    }

    /**
     * 주기간 설정 함수
     * @param week 주의 시작 및 마감시간
     */
    public void setWeek(List<Integer> week) {
        this.week.setValue(week);
    }

    /**
     * 월기간 설정 함수
     * @param month 월의 시작 및 마감시간
     */
    public void setMonth(List<Integer> month) {
        this.month.setValue(month);
    }

    public Step getLatestStepData(int date) {
        return mRepository.getLatestStepData(date);
    }

    /**
     * 걸음수 자료 추가 및 갱신함수
     * @param step 추가 및 갱신될 걸음수
     */
    public void insertOrUpdateStepData(Step step) {
        mRepository.insertOrUpdateStep(step);
    }

    public int getTotalStepsByDate(int date) {
        return mRepository.getTotalStepsByDate(date);
    }

    /**
     * 첫 걸음수 자료를 얻는 함수
     * @return 얻어진 자료
     */
    public Step getFirstStepData() {
        return mRepository.getFirstStepData();
    }

    public WalkingModes getActiveWalkingMode() {
        return mRepository.getActiveWalkingMode();
    }

    /**
     * 모든 걸음방식 얻는 함수
     * @return 걸음방식 목록
     */
    public List<WalkingModes> getAllWalkingModes() {
        return mRepository.getAllWalkingModes();
    }

    /**
     * 모든 걸음방식 얻는 함수 (LiveData)
     * @return 걸음방식 목록
     */
    public LiveData<List<WalkingModes>> getWalkingModes() {
        return mRepository.getWalkingModes();
    }

    /**
     * 현재 리용하는 걸음방식 갱신함수
     * @param walkingMode 설정될 걸음방식
     */
    public void updateActiveMode(WalkingModes walkingMode) {
        mRepository.updateActiveMode(walkingMode);
    }

    /**
     * 걸음보폭수 갱신함수
     * @param walkingStepSize 걸음보폭수
     */
    public void updateWalkingStepSize(Double walkingStepSize) {
        mRepository.updateWalkingStepSize(walkingStepSize);
    }

    /**
     * 달리기보폭수 갱신함수
     * @param runningStepSize 달리기보폭수
     */
    public void updateRunningStepSize(Double runningStepSize) {
        mRepository.updateRunningStepSize(runningStepSize);
    }
}
