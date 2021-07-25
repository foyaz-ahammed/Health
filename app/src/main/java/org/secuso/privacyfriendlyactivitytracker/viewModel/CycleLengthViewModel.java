package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.secuso.privacyfriendlyactivitytracker.persistence.CycleLength;
import org.secuso.privacyfriendlyactivitytracker.persistence.CycleRepository;

/**
 * 생리길이관련 viewModel
 */
public class CycleLengthViewModel extends AndroidViewModel {
    private final CycleRepository mRepository;

    public LiveData<CycleLength> cycleLengthLiveData; // 생리기간자료

    public CycleLengthViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CycleRepository(application);

        cycleLengthLiveData = mRepository.getCycleLengthLiveData();
    }

    /**
     * 생리길이 갱신하는 함수
     * @param cycleLength 생리길이
     */
    public void insertOrUpdate(CycleLength cycleLength) {
        mRepository.insertOrUpdateLength(cycleLength);
    }

    /**
     * 생리길이 얻는 함수
     * @return 생리길이
     */
    public CycleLength getCycleLengthData() {
        return mRepository.getCycleLengthData();
    }
}
