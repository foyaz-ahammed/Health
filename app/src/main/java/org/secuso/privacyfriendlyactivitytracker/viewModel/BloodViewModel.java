package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.Blood;
import org.secuso.privacyfriendlyactivitytracker.persistence.BloodRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * 혈압관련 viewModel
 */
public class BloodViewModel extends AndroidViewModel {
    private final BloodRepository mRepository;
    public LiveData<List<Blood>> allData; // 모든 혈압기록자료
    public MediatorLiveData<List<BloodPressureInfo>> bloodAllData = new MediatorLiveData<>();
    public LiveData<List<Blood>> latestData; // 마지막으로 기록한 혈압자료

    public BloodViewModel(@NonNull Application application) {
        super(application);
        mRepository = new BloodRepository(application);
    }

    /**
     * 건강관리화면을 위한 초기화함수
     */
    public void instanceForMain() {
        latestData = mRepository.getLatestData();
    }

    /**
     * 기록보기화면을 위한 초기화함수
     */
    public void instanceForDetail() {
        allData = mRepository.getAllData();
        bloodAllData.addSource(allData, bloodData -> {
            List<BloodPressureInfo> infoList = new ArrayList<>();
            for (int i = 0; i < bloodData.size(); i ++) {
                infoList.add(new BloodPressureInfo(0, bloodData.get(i)));
                infoList.add(new BloodPressureInfo(1, bloodData.get(i).getModifiedTime()));
            }
            if (infoList.size() > 0) {
                infoList = infoList.subList(0, infoList.size() - 1);
            }
            bloodAllData.postValue(infoList);
        });
    }

    /**
     * 선택된 자료들을 삭제하는 함수
     * @param selectedItemList 선택된 자료들의 id 목록
     */
    public void deleteBlood(List<Integer> selectedItemList) {
        mRepository.deleteBlood(selectedItemList);
    }
}
