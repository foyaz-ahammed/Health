package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.secuso.privacyfriendlyactivitytracker.persistence.CycleRepository;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;

import java.util.List;

/**
 * 생리관련 viewModel
 */
public class CycleViewModel extends AndroidViewModel {
    private final CycleRepository mRepository;

    public LiveData<List<Ovulation>> ovulationLiveData; // 날자에 해당하는 생리자료
    public LiveData<List<Ovulation>> ovulationDataForStatus; // 통계자료
    public LiveData<List<Ovulation>> ovulationDataForTotal; // 총날자 현시를 위한 생리자료
    public MutableLiveData<Integer> date = new MutableLiveData<>(); // 선택한 날자
    public MutableLiveData<List<Integer>> monthPeriod = new MutableLiveData<>(); // 월의 시작 및 마감날자

    public LiveData<List<Ovulation>> ovulationMonthData; // 선택한 날자에 준하여 주변 석달 자료

    public CycleViewModel(@NonNull Application application) {
        super(application);
        this.mRepository = new CycleRepository(application);
        ovulationLiveData = Transformations.switchMap(date, mRepository::getOvulation);

        ovulationDataForStatus = Transformations.switchMap(date, mRepository::getOvulationForStatus);

        ovulationDataForTotal = Transformations.switchMap(date, mRepository::getOvulationForTotal);

        ovulationMonthData = Transformations.switchMap(monthPeriod, period -> mRepository.getMonthOvulation(period.get(0), period.get(1)));
    }

    /**
     * 통계자료를 위한 생리자료얻는 함수
     * @return 생리자료
     */
    public LiveData<List<Ovulation>> getOvulationDataForStats() {
        return mRepository.getOvulationForStats();
    }

    /**
     * 주어진 날자의 이전 생리자료를 얻는 함수
     * @param date 날자
     * @return 생리자료
     */
    public Ovulation getPrevOvulation(int date) {
        return mRepository.getPrevOvulation(date);
    }

    /**
     * 주어진 날자의 다음 생리자료를 얻는 함수
     * @param date 날자
     * @return 생리자료
     */
    public Ovulation getNextOvulation(int date) {
        return mRepository.getNextOvulation(date);
    }

    /**
     * 마지막 생리자료를 얻는 함수
     * @return 생리자료
     */
    public Ovulation getLastOvulation() {
        return mRepository.getLastOvulation();
    }

    /**
     * 날자설정함수
     * @param date 설정될 날자
     */
    public void setDate(int date) {
        this.date.setValue(date);
    }

    /**
     * 생리자료 추가 및 갱신함수
     * @param ovulation 새로 추가 및 갱신될 생리자료
     */
    public void insertOrUpdateOvulation(Ovulation ovulation) {
        mRepository.insertOrUpdateOvulation(ovulation);
    }

    /**
     * 생리자료삭제 함수
     * @param id 삭제될 생리자료의 id
     */
    public void deleteOvulation(int id) {
        mRepository.deleteOvulation(id);
    }

    /**
     * 생리예상자료 삭제하는 함수
     */
    public void deletePredictOvulation() {
        mRepository.deletePredictOvulation();
    }

    /**
     * 생리자료의 개수 얻는 함수
     * @return
     */
    public int getOvulationCount() {
        return mRepository.getOvulationCount();
    }

    /**
     * 월설정함수
     * @param month 설정될 월
     */
    public void setMonth(List<Integer> month) {
        this.monthPeriod.setValue(month);
    }
}
