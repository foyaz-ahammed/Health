package org.secuso.privacyfriendlyactivitytracker.persistence;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * 건강항목순서변경과 관련된 repository
 */
public class HealthOrderRepository {
    public HealthOrderDao mHealthOrderDao;

    public HealthOrderRepository(Application application) {
        MeasureDatabase db = MeasureDatabase.getInstance(application);
        mHealthOrderDao = db.healthOrderDao();
    }

    /**
     * 건강항목순서목록를 얻는 함수 (LiveData)
     * @return 순서목록
     */
    public LiveData<List<HealthOrder>> getHealthOrder() {
        return mHealthOrderDao.getHealthOrder();
    }

    /**
     * 건강항목순서목록을 얻는 함수
     * @return 순서목록
     */
    public List<HealthOrder> getOrders() {
        return mHealthOrderDao.getOrders();
    }

    /**
     * 개별적인 건강항목자료를 자료기지에 보관하는 함수
     * @param order 개별적인 건강항목자료
     */
    public void insertOrUpdateOrder(HealthOrder order) {
        AsyncTask.execute(() -> mHealthOrderDao.insertOrUpdate(order));
    }

    /**
     * 모든 건강항목순서자료 삭제
     */
    public void deleteAllOrder() {
        AsyncTask.execute(() -> mHealthOrderDao.deleteAll());
    }
}
