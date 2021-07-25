package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;

import java.util.List;

/**
 * 물관련 repository
 */
public class WaterRepository {
    public WaterDao mWaterDao;

    public WaterRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mWaterDao = db.WaterDao();
    }

    /**
     * 최신자료를 얻는 함수
     * @return
     */
    public LiveData<List<Water>> getLatestData() {
        return mWaterDao.getLatestData();
    }

    /**
     * 날자별 자료를 얻는 함수
     * @param date 날자
     * @return 얻은 자료
     */
    public LiveData<Water> getDayData(DateTime date) {
        return mWaterDao.getDayData(date.getMillis());
    }

    /**
     * 주별 자료를 얻는 함수
     * @param start 주의 시작시간
     * @param end 주의 마감시간
     * @return 얻은 자료
     */
    public LiveData<List<Water>> getWeekData(long start, long end) {
        return mWaterDao.getWeekData(start, end);
    }

    /**
     * 월별 자료를 얻는 함수
     * @param start 월의 시작시간
     * @param end 월의 마감시간
     * @return 얻은 자료
     */
    public LiveData<List<Water>> getMonthData(long start, long end) {
        return mWaterDao.getMonthData(start, end);
    }

    /**
     * 추가 및 갱신을 진행하는 함수
     * @param waterInfo 추가 및 갱신할 자료
     */
    public void insertOrUpdateWater(WaterInfo waterInfo) {
        AsyncTask.execute(() -> {
            Water water = new Water(waterInfo);
            mWaterDao.insertOrUpdate(water);
        });
    }
}
