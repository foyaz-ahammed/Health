package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.secuso.privacyfriendlyactivitytracker.persistence.CycleRepository;
import org.secuso.privacyfriendlyactivitytracker.persistence.Symptom;

import java.util.List;

/**
 * 신체적증상관련 viewModel
 */
public class SymptomViewModel extends AndroidViewModel {
    private final CycleRepository mRepository;
    MutableLiveData<Integer> date = new MutableLiveData<>(); // 선택한 날자
    public LiveData<Symptom> dayData; // 일별 신체적증상 자료

    MutableLiveData<List<Integer>> month = new MutableLiveData<>(); // 월 시작 및 마감날자
    public LiveData<List<Symptom>> monthData; // 월별 자료

    public SymptomViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CycleRepository(application);
        dayData = Transformations.switchMap(date, new Function<Integer, LiveData<Symptom>>() {
            @Override
            public LiveData<Symptom> apply(Integer date) {
                return mRepository.getSymptom(date);
            }
        });
        monthData = Transformations.switchMap(month, new Function<List<Integer>, LiveData<List<Symptom>>>() {
            @Override
            public LiveData<List<Symptom>> apply(List<Integer> month) {
                return mRepository.getMonthSymptom(month.get(0), month.get(1));
            }
        });
    }

    /**
     * 신체적증상자료 추가 및 갱신하는 함수
     * @param symptom 새로 추가 및 갱신될 신체적증상자료
     */
    public void insertOrUpdate(Symptom symptom) {
        mRepository.insertOrUpdateSymptom(symptom);
    }

    /**
     * 신체적증상자료 삭제 함수
     * @param id 삭제될 신체적증상자료의 id
     */
    public void delete(int id) {
        mRepository.deleteSymptom(id);
    }

    /**
     * 날자설정함수
     * @param date 설정될 날자
     */
    public void setDate(int date) {
        this.date.setValue(date);
    }

    /**
     * 월설정함수
     * @param month 설정될 월
     */
    public void setMonth(List<Integer> month) {
        this.month.setValue(month);
    }
}
