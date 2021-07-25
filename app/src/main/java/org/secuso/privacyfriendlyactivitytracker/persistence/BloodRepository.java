package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;

import java.util.List;

/**
 * 혈압관련 repository
 */
public class BloodRepository {
    public BloodDao mBloodDao;

    public BloodRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mBloodDao = db.BloodDao();
    }

    /**
     * 혈압자료를 추가 및 갱신하는 함수
     * @param bloodPressureInfo 추가 및 갱신하려는 자료
     */
    public void insertOrUpdateBlood(BloodPressureInfo bloodPressureInfo) {
        new InsertAsyncTask(mBloodDao).execute(bloodPressureInfo);
    }

    /**
     * 혈압자료를 삭제하는 함수
     * @param selectedList 선택된 자료들의 id 목록
     */
    public void deleteBlood(List<Integer> selectedList) {
        mBloodDao.delete(selectedList);
    }

    /**
     * 최신자료를 얻는 함수
     * @return 얻은 자료
     */
    public LiveData<List<Blood>> getLatestData() {
        return mBloodDao.getLatestData();
    }

    /**
     * 모든 자료를 얻는 함수
     * @return 얻은 자료
     */
    public LiveData<List<Blood>> getAllData() {
        return mBloodDao.getAllData();
    }

    /**
     * 자료추가를 진행하는 class
     */
    private static class InsertAsyncTask extends AsyncTask<BloodPressureInfo, Void, Void> {

        private final BloodDao mAsyncTaskDao;

        InsertAsyncTask(BloodDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final BloodPressureInfo... params) {
            Blood blood = new Blood(params[0]);
            mAsyncTaskDao.insertOrUpdate(blood);
            return null;
        }
    }
}
