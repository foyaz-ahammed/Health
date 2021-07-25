package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.Water;
import org.secuso.privacyfriendlyactivitytracker.persistence.WaterRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 물관련 viewModel
 */
public class WaterViewModel extends AndroidViewModel {
    private final WaterRepository mRepository;

    MutableLiveData<DateTime> date = new MutableLiveData<>(); // 선택한 날자
    public LiveData<Water> dayData; // 일별 자료

    public MutableLiveData<List<Long>> weekPeriod = new MutableLiveData<List<Long>>(); // 선택한 주 시작 및 마감날자
    public MutableLiveData<List<Long>> monthPeriod = new MutableLiveData<List<Long>>(); // 선택한 월 시작 및 마감날자

    public LiveData<List<Water>> weekData; // 주별 자료
    public LiveData<List<Water>> monthData; // 월별 자료

    public LiveData<List<Water>> latestData; // 최신 자료

    public WaterViewModel(@NonNull Application application) {
        super(application);
        mRepository = new WaterRepository(application);
    }

    /**
     * 건강관리화면을 위한 초기화함수
     */
    public void instanceForMain() {
        latestData = mRepository.getLatestData();
    }

    /**
     * 일별 자료현시화면을 위한 초기화함수
     */
    public void instanceForDay() {
        dayData = Transformations.switchMap(date, mRepository::getDayData);
    }

    /**
     * 날자설정하는 함수
     * @param date 설정하려는 날자
     */
    public void setDate(DateTime date) {
        this.date.postValue(date);
    }

    /**
     * 자료추가및 갱신을 진행하는 함수
     * @param info 추가 및 갱신할 자료
     */
    public void insertOrUpdate(WaterInfo info) {
        mRepository.insertOrUpdateWater(info);
    }

    /**
     * 주별 현시화면을 위한 초기화함수
     */
    public void instanceForWeek() {
        weekData = Transformations.switchMap(weekPeriod, input ->
                mRepository.getWeekData(input.get(0), input.get(1)));
    }

    /**
     * 월별 현시화면을 위한 초기화함수
     */
    public void instanceForMonth() {
        monthData = Transformations.switchMap(monthPeriod, input ->
                mRepository.getMonthData(input.get(0), input.get(1)));
    }

    /**
     * 주 기간설정하는 함수
     * @param start 주의 시작시간
     * @param end 주의 마감시간
     */
    public void setWeekPeriod(long start, long end) {
        List<Long> data = new ArrayList<>();
        data.add(start);
        data.add(end);
        weekPeriod.setValue(data);
    }

    /**
     * 월 기간설정하는 함수
     * @param start 월의 시작시간
     * @param end 월의 마감시간
     */
    public void setMonthPeriod(long start, long end) {
        List<Long> data = new ArrayList<>();
        data.add(start);
        data.add(end);
        monthPeriod.setValue(data);
    }
}
