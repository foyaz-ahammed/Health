package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;
import android.os.AsyncTask;

import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.Weight;
import org.secuso.privacyfriendlyactivitytracker.persistence.WeightDao;
import org.secuso.privacyfriendlyactivitytracker.persistence.WeightRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 몸무게 viewModel
 */
public class WeightViewModel extends AndroidViewModel {

    private final WeightRepository mRepository;
    public LiveData<List<Weight>> weightAllData; // 모든 자료
    public MutableLiveData<Integer> position = new MutableLiveData<>(); // 확장 및 축소할려는 위치
    Map<Long, Boolean> expandList = new HashMap<>(); // 일별 확장 및 축소상태목록
    List<WeightInfo> history = new ArrayList<>(); // 기록자료
    List<WeightInfo> originData = new ArrayList<>(); // 본래자료

    public MutableLiveData<String> currentDate = new MutableLiveData<String>(); // 현재 선택한 날자
    public MediatorLiveData<List<WeightInfo>> historyData = new MediatorLiveData<>(); // 기록자료
    public MediatorLiveData<List<WeightInfo>> expandData = new MediatorLiveData<>(); // 확장 및 축소를 반영한 자료

    public LiveData<List<Weight>> allDayData; // 모든 날자자료
    public MediatorLiveData<List<Weight>> dayData; // 날자별 자료

    public LiveData<List<WeightDao.AvgType>> weekPeriodData; // 주별 자료
    public LiveData<List<WeightDao.AvgType>> monthPeriodData; // 월별 자료
    public LiveData<List<WeightDao.AvgType>> yearData; // 년별 자료

    public LiveData<List<Weight>> mainData; // 기본화면에 표시할 자료

    int dayCurrentIndex = 0; // 현재 선택한 날자에 기초한 총 날자목록에서의 위치

    public MutableLiveData<List<Long>> weekPeriod = new MutableLiveData<List<Long>>(); // 주의 시작 및 마감날자
    public MutableLiveData<List<Long>> monthPeriod = new MutableLiveData<List<Long>>(); // 월의 시작 및 마감날자
    public MutableLiveData<Integer> year = new MutableLiveData<>(); // 선택한 년

    public WeightViewModel(Application application) {
        super(application);
        mRepository = new WeightRepository(application);

    }

    /**
     * 건강관리화면을 위한 초기화함수
     */
    public void instanceForMain() {
        mainData = mRepository.getThreeData();
    }

    /**
     * 기록보기화면을 위한 초기화함수
     */
    public void instanceForHistory() {
        weightAllData = mRepository.getAllData();
        historyData.addSource(weightAllData, weights -> AsyncTask.execute(() -> {
            history = new ArrayList<>();
            List<WeightInfo> dayList = new ArrayList<>();
            List<WeightInfo> allData = new ArrayList<>();
            List<WeightInfo> eachDayData = new ArrayList<>();
            List<WeightDao.AvgType> weightDayList = new ArrayList<>();
            weightDayList = mRepository.getDayList();
            for (int i = 0; i < weightDayList.size(); i ++) {
                WeightInfo info = new WeightInfo();
                WeightDao.AvgType dayAvg = weightDayList.get(i);
                info.setWeightValue(dayAvg.getWeightAvg());
                info.setDate(dayAvg.getMeasureDate());
                DateTime dateTime = new DateTime(dayAvg.getMeasureMilliTime());
                info.setMeasureDateTime(dateTime);
                info.setType("day");
                info.setExpand(expandList.get(dayAvg.getMeasureMilliTime()) != null ?
                        expandList.get(dayAvg.getMeasureMilliTime()) : true);
                dayList.add(info);
            }
            allData = convertWeightType(weights);
            for (int i = 0; i < dayList.size(); i ++) {
                eachDayData.add(dayList.get(i));
                history.add(dayList.get(i));
                int count = 0;
                for (int j = 0; j < allData.size(); j ++) {
                    if (allData.get(j).getDate().equals(dayList.get(i).getDate())) {
                        if (count > 0)
                            eachDayData.add(new WeightInfo("weight_divider", allData.get(j).getWeightValue(), allData.get(j).getMeasureDateTime()));
                        eachDayData.add(allData.get(j));
                        if (dayList.get(i).getExpand()) {
                            if (count > 0)
                                history.add(new WeightInfo("weight_divider", allData.get(j).getWeightValue(), allData.get(j).getMeasureDateTime()));
                            history.add(allData.get(j));
                        }
                        count++;
                    } else break;
                }
                eachDayData.add(new WeightInfo("day_divider", dayList.get(i).getWeightValue(), dayList.get(i).getMeasureDateTime()));
                history.add(new WeightInfo("day_divider", dayList.get(i).getWeightValue(), dayList.get(i).getMeasureDateTime()));
                allData.subList(0, count).clear();
            }
            if (eachDayData.size() > 0) {
                eachDayData = eachDayData.subList(0, eachDayData.size() - 1);
                history = history.subList(0, history.size() - 1);
            }
            originData = new ArrayList<>();
            originData.addAll(eachDayData);
            historyData.postValue(eachDayData);
        }));

        expandData.addSource(position, clickedPos -> {
            int pos = clickedPos + 1;
            boolean isExpand = history.get(clickedPos).getExpand();
            history.get(clickedPos).setExpand(!isExpand);
            expandList.put(history.get(clickedPos).getMeasureDateTime().getMillis(), !isExpand);

            List<WeightInfo> data = new ArrayList<>();
            data.addAll(history);
            int count = 0;
            if (isExpand) {
                for (int i = pos; i < data.size(); i ++) {
                    if (!(data.get(i).getType().equals("day_divider"))) {
                        count++;
                    } else break;
                }
                if (count > 0) {
                    data.subList(pos, pos + count).clear();
                    history.subList(pos, pos + count).clear();
                }
            } else {
                for (int i = 0; i < originData.size(); i ++) {
                    if (originData.get(i).getType().equals("day") &&
                            data.get(clickedPos).getMeasureDateTime().getMillis() == originData.get(i).getMeasureDateTime().getMillis()) {
                        for (int j = i + 1; j < originData.size(); j ++) {
                            if (!originData.get(j).getType().equals("day_divider")) {
                                data.add(pos, originData.get(j));
                                history.add(pos, originData.get(j));
                                pos++;
                            } else break;
                        }
                        break;
                    }
                }
            }
            expandData.postValue(data);
        });
    }

    /**
     * 일별보기화면을 위한 초기화함수
     */
    public void instanceForDay() {
        allDayData = mRepository.getAllDayData();

        if (dayData == null) {
            dayData = new MediatorLiveData<>();
            dayData.addSource(allDayData, weights -> AsyncTask.execute(() -> {
                if (weights.size() > 0) {
                    List<Weight> data = mRepository.getDayData(weights.get(dayCurrentIndex).getDate());
                    dayData.postValue(data.subList(1, data.size()));
                }
            }));

            dayData.addSource(currentDate, s -> AsyncTask.execute(() -> {
                List<Weight> data = mRepository.getDayData(s);
                dayData.postValue(data.subList(1, data.size()));
            }));
        }
    }

    /**
     * 주별보기화면을 위한 초기화함수
     */
    public void instanceForWeekPeriod() {
        weekPeriodData = Transformations.switchMap(weekPeriod, input ->
                mRepository.getWeekData(input.get(0), input.get(1)));
    }

    /**
     * 월별보기화면을 위한 초기화함수
     */
    public void instanceForMonthPeriod() {
        monthPeriodData = Transformations.switchMap(monthPeriod, input ->
                mRepository.getMonthData(input.get(0), input.get(1)));
    }

    /**
     * 년별보기화면을 위한 초기화함수
     */
    public void instanceForYear() {
        yearData = Transformations.switchMap(year, mRepository::getYearData);
    }

    /**
     * 주기간 설정함수
     * @param start 주의 시작시간
     * @param end 주의 마감시간
     */
    public void setWeekPeriod(long start, long end) {
        List<Long> data = new ArrayList<>();
        data.add(start);
        data.add(end);
        weekPeriod.setValue(data);
    }

    /**
     * 월기간 설정함수
     * @param start 월의 시작시간
     * @param end 월의 마감시간
     */
    public void setMonthPeriod(long start, long end) {
        List<Long> data = new ArrayList<>();
        data.add(start);
        data.add(end);
        monthPeriod.setValue(data);
    }

    /**
     * 년도 설정함수
     * @param year 년도
     */
    public void setYear(int year) {
        this.year.setValue(year);
    }

    /**
     * 몸무게 자료 object 형태 변경함수
     * @param data 자료
     * @return 변경된 자료
     */
    public List<WeightInfo> convertWeightType(List<Weight> data) {
        List<WeightInfo> newData = new ArrayList<>();
        for (int i = 0; i < data.size(); i ++) {
            WeightInfo info = new WeightInfo(data.get(i));
            newData.add(info);
        }
        return newData;
    }

    /**
     * 선택된 몸무게자료들 삭제하는 함수
     * @param selectedItemList 선택된 몸무게자료 id목록
     */
    public void deleteWeight(List<Integer> selectedItemList) {
        mRepository.deleteWeight(selectedItemList);
    }

    /**
     * 현재 보여주는 날자의 index 설정함수
     * @param index 설정할 index
     * @param newDate 새 날자
     */
    public void setDayCurrentIndex(int index, String newDate) {
        dayCurrentIndex = index;
        currentDate.postValue(newDate);
    }

    /**
     * 기록확장 위치 설정하는 함수
     * @param position 위치
     */
    public void setExpandPosition(int position) {
        this.position.postValue(position);
    }

    /**
     * 최근 몸무게자료 얻는 함수
     * @return 최근 몸무게자료
     */
    public LiveData<Weight> getLatestWeightData() {
        return mRepository.getLatestData();
    }
}
