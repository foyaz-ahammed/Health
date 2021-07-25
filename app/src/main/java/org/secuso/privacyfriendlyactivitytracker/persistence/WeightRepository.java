package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;

import java.util.List;

/**
 * 몸무게관련 repository
 */
public class WeightRepository {
    public WeightDao mWeightDao;

    public WeightRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mWeightDao = db.weightDao();
    }

    /**
     * 모든자료 얻는 함수
     * @return 모든 자료
     */
    public LiveData<List<Weight>> getAllData() {
        return mWeightDao.getAll();
    }

    /**
     * 날자목록 얻는 함수
     * @return 날자목록
     */
    public List<WeightDao.AvgType> getDayList() {
        return mWeightDao.getDayList();
    }

    /**
     * 날자별 모든 자료 얻는 함수
     * @return 날자별 모든 자료
     */
    public LiveData<List<Weight>> getAllDayData() {
        return mWeightDao.getAllDayData();
    }

    /**
     * 자료 추가 함수
     * @param weightInfo 새로 추가할 자료
     */
    public void insertWeight(WeightInfo weightInfo) {
        new InsertAsyncTask(mWeightDao).execute(weightInfo);
    }

    /**
     * 선택된 자료 삭제함수
     * @param selectedList 선택된 자료의 id목록
     */
    public void deleteWeight(List<Integer> selectedList) {
        mWeightDao.delete(selectedList);
    }

    /**
     * 날자별 자료 얻는 함수
     * @param date 날자
     * @return 얻은 자료
     */
    public List<Weight> getDayData(String date) {
        return mWeightDao.getDayData(date);
    }

    /**
     * 주별 자료 얻는 함수
     * @param start 주의 시작시간
     * @param end 주의 마감시간
     * @return 얻은 자료
     */
    public LiveData<List<WeightDao.AvgType>> getWeekData(long start, long end) {
        return mWeightDao.getWeekData(start, end);
    }

    /**
     * 월별 자료 얻는 함수
     * @param start 월의 시작시간
     * @param end 월의 마감시간
     * @return 얻은 자료
     */
    public LiveData<List<WeightDao.AvgType>> getMonthData(long start, long end) {
        return mWeightDao.getMonthData(start, end);
    }

    /**
     * 년별 자료 얻는 함수
     * @param year 년도
     * @return 얻은 자료
     */
    public LiveData<List<WeightDao.AvgType>> getYearData(int year) {
        return mWeightDao.getYearData(String.valueOf(year));
    }

    /**
     * 최신 3개의 자료 얻는 함수
     * @return 얻은 자료
     */
    public LiveData<List<Weight>> getThreeData() {
        return mWeightDao.getThreeData();
    }

    /**
     * 최신 자료 얻는 함수
     * @return 얻은 자료
     */
    public LiveData<Weight> getLatestData() {
        return mWeightDao.getLatestData();
    }

    /**
     * 최신 자료 얻는 함수
     * @return 얻은 자료
     */
    public Weight getLatestWeight() {
        return mWeightDao.getLatestWeight();
    }

    /**
     * 자료 추가를 위한 class
     */
    private static class InsertAsyncTask extends AsyncTask<WeightInfo, Void, Void> {

        private final WeightDao mAsyncTaskDao;

        InsertAsyncTask(WeightDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final WeightInfo... params) {
            Weight weight = new Weight(params[0]);
            mAsyncTaskDao.insertOrUpdate(weight);
            return null;
        }
    }
}
