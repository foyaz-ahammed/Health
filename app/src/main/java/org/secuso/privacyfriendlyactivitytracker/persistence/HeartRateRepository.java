package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.secuso.privacyfriendlyactivitytracker.models.HeartRateInfo;

import java.util.List;

/**
 * 심박수관련 repository
 */
public class HeartRateRepository {
    public HeartRateDao mHeartRateDao;

    public HeartRateRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mHeartRateDao = db.heartRateDao();
    }

    /**
     * 심박수 자료 추가 및 갱신
     * @param info 새로 추가할 심박수 자료
     */
    public void insertOrUpdateHeartRate(HeartRateInfo info) {
        AsyncTask.execute(() -> mHeartRateDao.insertOrUpdate(new HeartRate(info)));
    }

    public LiveData<HeartRate> getLatestData() {
        return mHeartRateDao.getLatestData();
    }

    /**
     * 주어진 날자의 심박수자료목록을 얻는 함수
     * @return 얻어진 일자료목록
     */
    public LiveData<List<HeartRate>> getDayData(String day) {
        return mHeartRateDao.getDayData(day);
    }

    /**
     * 주어진 시작시간과 마감시간사이의 자료 얻기
     * @param start 시작시간
     * @param end 마감시간
     * @return 일별 최대 및 최소 심박수
     */
    public LiveData<List<HeartRateDao.MaxMin>> getPeriodData(long start, long end) {
        return mHeartRateDao.getPeriodData(start, end);
    }

    /**
     * 주어진 년에 해당한 자료 얻기
     * @return 얻어진 월자료목록
     */
    public LiveData<List<HeartRateDao.MaxMin>> getYearData(int year) {
        return mHeartRateDao.getYearData(year);
    }
}
