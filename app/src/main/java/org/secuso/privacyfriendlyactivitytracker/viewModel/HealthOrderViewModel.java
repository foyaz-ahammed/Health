package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import org.secuso.privacyfriendlyactivitytracker.persistence.HealthOrder;
import org.secuso.privacyfriendlyactivitytracker.persistence.HealthOrderRepository;

import java.util.List;

/**
 * 건강항목순서변경과 관련된 viewModel
 */
public class HealthOrderViewModel extends AndroidViewModel {
    private final HealthOrderRepository mRepository;

    public HealthOrderViewModel(@NonNull Application application) {
        super(application);
        mRepository = new HealthOrderRepository(application);
    }

    /**
     * 건강항목순서목록를 얻는 함수 (LiveData)
     * @return 순서목록
     */
    public LiveData<List<HealthOrder>> getHealthOrder() {
        return mRepository.getHealthOrder();
    }

    /**
     * 건강항목순서목록을 얻는 함수
     * @return 순서목록
     */
    public List<HealthOrder> getOrders() {
        return mRepository.getOrders();
    }

    /**
     * 개별적인 건강항목자료를 자료기지에 보관하는 함수
     * @param order 개별적인 건강항목자료
     */
    public void insertOrUpdate(HealthOrder order) {
        mRepository.insertOrUpdateOrder(order);
    }

    /**
     * 모든 건강항목순서자료 삭제
     */
    public void deleteAllOrder() {
        mRepository.deleteAllOrder();
    }
}
