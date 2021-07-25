package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRate;
import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRateDao;
import org.secuso.privacyfriendlyactivitytracker.persistence.HeartRateRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 심박수관련 viewModel
 */
public class HeartRateViewModel extends AndroidViewModel {
    private final HeartRateRepository mRepository;

    public MutableLiveData<List<Long>> weekPeriod = new MutableLiveData<>();
    public LiveData<List<HeartRateDao.MaxMin>> weekData;

    public MutableLiveData<List<Long>> monthPeriod = new MutableLiveData<>();
    public LiveData<List<HeartRateDao.MaxMin>> monthData;

    public MutableLiveData<Integer> year = new MutableLiveData<>();
    public LiveData<List<HeartRateDao.MaxMin>> yearData;

    public MutableLiveData<String> day = new MutableLiveData<>();
    public LiveData<List<HeartRate>> originDayData;
    public MediatorLiveData<List<List<HeartRate>>> dayData = new MediatorLiveData<>();

    public MediatorLiveData<String> latestDate = new MediatorLiveData<>();
    public MediatorLiveData<List<List<HeartRate>>> mainData = new MediatorLiveData<>();

    public static final long THIRTY_MINUTE = 1800000;
    public static final long ONE_MINUTE = 60000;

    public HeartRateViewModel(@NonNull Application application) {
        super(application);
        mRepository = new HeartRateRepository(application);
    }

    /**
     * 기본화면에 표시할 자료 얻기
     */
    public void instanceForMain() {
        latestDate.addSource(mRepository.getLatestData(), heartRate -> {
            if (heartRate != null) {
                latestDate.setValue(heartRate.getDay());
            } else {
                latestDate.setValue("");
            }
        });
        mainData.addSource(getLatestDayData(), data -> AsyncTask.execute(() -> {
            List<List<HeartRate>> generatedData = new ArrayList<>();
            List<HeartRate> newData = new ArrayList<>();
            for (int i = 0; i < data.size(); i ++) {
                if (i + 1 < data.size()) {
                    if (data.get(i + 1).getMeasureTime() - data.get(i).getMeasureTime() < THIRTY_MINUTE) {
                        newData.add(data.get(i));
                    } else {
                        newData.add(data.get(i));
                        generatedData.add(new ArrayList<>(newData));
                        newData.clear();
                    }
                } else {
                    newData.add(data.get(i));
                    generatedData.add(new ArrayList<>(newData));
                    newData.clear();
                }
            }

            mainData.postValue(generatedData);
        }));
    }

    /**
     * 제일 마지막 날자의 자료 얻기
     * @return
     */
    private LiveData<List<HeartRate>> getLatestDayData() {
        return Transformations.switchMap(latestDate, mRepository::getDayData);
    }

    /**
     * 일별 자료를 얻기 위한 instance
     */
    public void instanceForDay() {
        originDayData = Transformations.switchMap(day, mRepository::getDayData);
        dayData.addSource(originDayData, data -> {
            List<List<HeartRate>> chartData = new ArrayList<>();
            List<HeartRate> newData = new ArrayList<>();
            for (int i = 0; i < data.size(); i ++) {
                if (i + 1 < data.size()) {
                    if (data.get(i + 1).getMeasureTime() - data.get(i).getMeasureTime() < THIRTY_MINUTE) {
                        newData.add(data.get(i));
                    } else {
                        newData.add(data.get(i));
                        chartData.add(new ArrayList<>(newData));
                        newData.clear();
                    }
                } else {
                    newData.add(data.get(i));
                    chartData.add(new ArrayList<>(newData));
                    newData.clear();
                }
            }

            dayData.postValue(chartData);
        });

    }

    /**
     * 주별 자료를 얻기 위한 instance
     */
    public void instanceForWeek() {
        weekData = Transformations.switchMap(weekPeriod, input ->
                mRepository.getPeriodData(input.get(0), input.get(1)));
    }

    /**
     * 월별 자료를 얻기 위한 instance
     */
    public void instanceForMonth() {
        monthData = Transformations.switchMap(monthPeriod, input ->
                mRepository.getPeriodData(input.get(0), input.get(1)));
    }

    /**
     * 년별 자료를 얻기 위한 instance
     */
    public void instanceForYear() {
        yearData = Transformations.switchMap(year, mRepository::getYearData);
    }

    /**
     * 자료기지에서 얻을 주기간 설정
     * @param start 시작시간
     * @param end 마감시간
     */
    public void setWeekPeriod(long start, long end) {
        List<Long> period = new ArrayList<>();
        period.add(start);
        period.add(end);
        this.weekPeriod.setValue(period);
    }

    /**
     * 자료기지에서 얻을 월기간 설정
     * @param start 시작시간
     * @param end 마감시간
     */
    public void setMonthPeriod(long start, long end) {
        List<Long> period = new ArrayList<>();
        period.add(start);
        period.add(end);
        this.monthPeriod.setValue(period);
    }

    /**
     * 자료기지에서 얻을 년 설정
     */
    public void setYear(int year) {
        this.year.setValue(year);
    }
}
