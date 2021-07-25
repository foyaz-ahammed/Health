package org.secuso.privacyfriendlyactivitytracker.viewModel;

import android.app.Application;
import android.os.AsyncTask;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.models.ExerciseInfo;
import org.secuso.privacyfriendlyactivitytracker.models.ExerciseInfo.TotalInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.Exercise;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseDao.DayTotal;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 운동 viewModel
 */
public class ExerciseViewModel extends AndroidViewModel {
    private final ExerciseRepository mRepository;

    public MediatorLiveData<List<ExerciseInfo>> historyData = new MediatorLiveData<List<ExerciseInfo>>(); // 기록자료
    public MediatorLiveData<List<ExerciseInfo>> expandData = new MediatorLiveData<>();
    public LiveData<List<Exercise>> typeData; // 운동형태별 자료
    public MutableLiveData<Integer> type = new MutableLiveData<>(); // 운동형태
    public MutableLiveData<Integer> position = new MutableLiveData<>(); // 확장 및 축소한 위치
    public Map<String, Integer> expandList = new HashMap<>(); // 월별 확장 및 축소상태목록
    List<ExerciseInfo> history = new ArrayList<>(); // 기록자료
    List<ExerciseInfo> originData = new ArrayList<>(); // 본래자료

    public MutableLiveData<Integer> weekStatsType = new MutableLiveData<>(); // 주별 운동형태
    public MutableLiveData<List<Long>> weekPeriod = new MutableLiveData<>(); // 주별 통계현시기간
    public LiveData<List<DayTotal>> weekPeriodData; // 운동형태에 따르는 주별 현시기간자료
    public LiveData<List<DayTotal>> weekTypeData; // 운동형태에 따르는 주별 운동자료

    public MutableLiveData<Integer> monthStatsType = new MutableLiveData<>(); // 월별 운동형태
    public MutableLiveData<List<Long>> monthPeriod = new MutableLiveData<>(); // 월별 통계현시기간
    public LiveData<List<DayTotal>> monthPeriodData; // 운동형태에 따르는 월별 현시기간자료
    public LiveData<List<DayTotal>> monthTypeData; // 운동형태에 따르는 주별 운동자료

    public MutableLiveData<Integer> yearStatsType = new MutableLiveData<>(); // 년별 운동형태
    public MutableLiveData<Integer> year = new MutableLiveData<>(); // 년별 통계현시기간
    public LiveData<List<DayTotal>> yearData; // 운동형태에 따르는 년별 현시기간자료
    public LiveData<List<DayTotal>> yearTypeData; // 운동형태에 따르는 년별 운동자료

    public MutableLiveData<Integer> totalStatsType = new MutableLiveData<>(); // 총보기에 해당하는 운동형태
    public MutableLiveData<List<Long>> totalPeriod = new MutableLiveData<>(); // 총보기에 해당하는 통계현시기간
    public LiveData<List<DayTotal>> totalPeriodData; // 총보기에 해당하는 운동형태에 따르는 현시기간자료
    public LiveData<List<DayTotal>> totalTypeData; // 총보기에 해당하는 운동형태에 따르는 운동자료

    public LiveData<List<Exercise>> latestData; // 최신 운동자료

    public ExerciseViewModel(@NonNull Application application) {
        super(application);
        mRepository = new ExerciseRepository(application);
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
    public void instanceForHistory() {
        typeData = Transformations.switchMap(type, type -> {
            if (type == 0) {
                return mRepository.getAllData();
            } else if (type == 1) {
                return mRepository.getRunningData();
            } else if (type == 2) {
                return mRepository.getWalkingData();
            } else if (type == 3) {
                return mRepository.getCyclingData();
            } else {
                return mRepository.getSwimmingData();
            }
        });
        historyData.addSource(typeData, exercises -> AsyncTask.execute(() -> {
            history.clear();
            List<ExerciseInfo> exerciseData = new ArrayList<>();
            List<ExerciseInfo> exerciseInfos = new ArrayList<>();
            List<Exercise> monthList = new ArrayList<>();
            if (type.getValue() == 0)
                monthList = mRepository.getMonthList();
            else monthList = mRepository.getMonthListByType(type.getValue());
            for (int i = 0; i < monthList.size(); i ++) {
                List<ExerciseDao.DistanceTotal> totalData = new ArrayList<>();
                ExerciseInfo exerciseInfo = new ExerciseInfo(type.getValue(), "month", new WorkoutInfo(monthList.get(i)),
                        expandList.get(monthList.get(i).getMonth()) != null ? expandList.get(monthList.get(i).getMonth()) : View.VISIBLE);
                exerciseData.add(exerciseInfo);
                exerciseInfos.add(exerciseInfo);
                totalData = mRepository.getTotalData(monthList.get(i).getMonth());

                ExerciseInfo totalInfo = new ExerciseInfo(type.getValue(), "total", new WorkoutInfo(monthList.get(i)), View.VISIBLE);
                for (int j = 0; j < totalData.size(); j ++) {
                    ExerciseDao.DistanceTotal total = totalData.get(j);
                    switch (total.getActivity()) {
                        case 1:
                            totalInfo.setTotalRunning(new TotalInfo(total.getSum(), total.getCount()));
                            break;
                        case 2:
                            totalInfo.setTotalWalking(new TotalInfo(total.getSum(), total.getCount()));
                            break;
                        case 3:
                            totalInfo.setTotalCycling(new TotalInfo(total.getSum(), total.getCount()));
                            break;
                        case 5:
                            totalInfo.setTotalSwimming(new TotalInfo(total.getSum(), total.getCount()));
                            break;
                    }
                }
                exerciseData.add(totalInfo);
                if (expandList.get(monthList.get(i).getMonth()) == null ||
                        expandList.get(monthList.get(i).getMonth()) == View.VISIBLE) {
                    exerciseInfos.add(totalInfo);
                }

                List<WorkoutInfo> monthData = new ArrayList<>();
                for (int j = 0; j < exercises.size(); j ++) {
                    if (monthList.get(i).getMonth().equals(exercises.get(j).getMonth())) {
                        monthData.add(new WorkoutInfo(exercises.get(j)));
                        if (monthData.size() > 1)
                            exerciseData.add(new ExerciseInfo(type.getValue(), "workout_divider", new WorkoutInfo(), View.VISIBLE));
                        exerciseData.add(new ExerciseInfo(type.getValue(), "workout", new WorkoutInfo(exercises.get(j)), View.VISIBLE));
                        if (expandList.get(monthList.get(i).getMonth()) == null || expandList.get(monthList.get(i).getMonth()) == View.VISIBLE) {
                            if (monthData.size() > 1)
                                exerciseInfos.add(new ExerciseInfo(type.getValue(), "workout_divider", new WorkoutInfo(), View.VISIBLE));
                            exerciseInfos.add(new ExerciseInfo(type.getValue(), "workout", new WorkoutInfo(exercises.get(j)), View.VISIBLE));
                        }
                    } else break;
                }
                exercises.subList(0, monthData.size()).clear();
                exerciseData.add(new ExerciseInfo(type.getValue(), "month_divider", new WorkoutInfo(), View.VISIBLE));
                exerciseInfos.add(new ExerciseInfo(type.getValue(), "month_divider", new WorkoutInfo(), View.VISIBLE));
            }
            if (exerciseData.size() > 0) {
                exerciseData = exerciseData.subList(0, exerciseData.size() - 1);
            }
            if (exerciseInfos.size() > 0) {
                exerciseInfos = exerciseInfos.subList(0, exerciseInfos.size() - 1);
            }
            originData.clear();
            originData.addAll(exerciseData);
            history.clear();
            history.addAll(exerciseInfos);
            historyData.postValue(exerciseInfos);
        }));

        expandData.addSource(position, clickedPos -> {
            int pos = clickedPos + 1;
            int visibility = history.get(clickedPos).getVisibility();
            history.get(clickedPos).setVisibility(visibility == View.GONE ? View.VISIBLE : View.GONE);
            expandList.put(history.get(clickedPos).getInfo().getMonth(), visibility == View.GONE ? View.VISIBLE : View.GONE);

            List<ExerciseInfo> data = new ArrayList<>();
            data.addAll(history);
            int count = 0;
            if (visibility == View.VISIBLE) {
                for (int i = pos; i < data.size(); i ++) {
                    if (!(data.get(i).getViewType().equals("month_divider"))) {
                        count++;
                    } else break;
                }
                if (pos + count > pos) {
                    data.subList(pos, pos + count).clear();
                    history.subList(pos, pos + count).clear();
                }
            } else {
                for (int i = 0; i < originData.size(); i ++) {
                    if (data.get(clickedPos).getInfo().getMonth().equals(originData.get(i).getInfo().getMonth())) {
                        for (int j = i + 1; j < originData.size(); j ++) {
                            if (!originData.get(j).getViewType().equals("month_divider")) {
                                data.add(pos, originData.get(j));
                                history.add(pos, originData.get(j));
                                pos ++;
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
     * 주별 운동상태화면을 위한 초기화함수
     */
    public void instanceForWeekStats() {
        weekPeriodData = Transformations.switchMap(weekPeriod, input -> {
            if (weekStatsType.getValue() != null)
                return mRepository.getWeekData(input.get(0), input.get(1), weekStatsType.getValue());
            return null;
        });

        weekTypeData = Transformations.switchMap(weekStatsType, type ->
                mRepository.getWeekData(weekPeriod.getValue().get(0), weekPeriod.getValue().get(1), type));
    }

    /**
     * 월별 운동상태화면을 위한 초기화함수
     */
    public void instanceForMonthStats() {
        monthPeriodData = Transformations.switchMap(monthPeriod, input -> {
            if (monthStatsType.getValue() != null)
                return mRepository.getMonthData(input.get(0), input.get(1), monthStatsType.getValue());
            return null;
        });

        monthTypeData = Transformations.switchMap(monthStatsType, type ->
                mRepository.getMonthData(monthPeriod.getValue().get(0), monthPeriod.getValue().get(1), type));
    }

    /**
     * 년별 운동상태화면을 위한 초기화함수
     */
    public void instanceForYearStats() {
        yearData = Transformations.switchMap(year, year -> {
            if (yearStatsType.getValue() != null)
                return mRepository.getYearData(year, yearStatsType.getValue());
            return null;
        });

        yearTypeData = Transformations.switchMap(yearStatsType, type ->
                mRepository.getYearData(year.getValue(), type));
    }

    /**
     * 전체 운동상태화면을 위한 초기화함수
     */
    public void instanceForTotalStats() {
        totalPeriodData = Transformations.switchMap(totalPeriod, input -> {
            if (totalStatsType.getValue() != null)
                return mRepository.getTotalData(input.get(0), input.get(1), totalStatsType.getValue());
            return null;
        });

        totalTypeData = Transformations.switchMap(totalStatsType, type ->
                mRepository.getTotalData(totalPeriod.getValue().get(0), totalPeriod.getValue().get(1), type));
    }

    /**
     * 전체 실외달리기 거리를 얻는 함수
     * @return 거리
     */
    public LiveData<Float> getTotalOutdoorRunDistance() {
        return mRepository.getTotalOutdoorRunDistance();
    }

    /**
     * 전체 실내달리기 거리를 얻는 함수
     * @return 거리
     */
    public LiveData<Float> getTotalIndoorRunDistance() {
        return mRepository.getTotalIndoorRunDistance();
    }

    /**
     * 전체 걸은 거리를 얻는 함수
     * @return 거리
     */
    public LiveData<Float> getTotalWalkDistance() {
        return mRepository.getTotalWalkDistance();
    }

    /**
     * 주 기간 설정하는 함수
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
     * 월 기간 설정하는 함수
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
     * 년도 설정하는 함수
     * @param year 설정할 년도
     */
    public void setYear(int year) {
        this.year.setValue(year);
    }

    /**
     * 전체보기 기간 설정하는 함수
     * @param start 시작년도
     * @param end 마감년도
     */
    public void setTotalPeriod(long start, long end) {
        List<Long> data = new ArrayList<>();
        data.add(start);
        data.add(end);
        totalPeriod.setValue(data);
    }

    /**
     * 주상태보기 운동종목 설정하는 함수
     * @param statsType 운동종목
     */
    public void setWeekStatsType(int statsType) {
        this.weekStatsType.setValue(statsType);
    }

    /**
     * 월상태보기 운동종목 설정하는 함수
     * @param statsType 운동종목
     */
    public void setMonthStatsType(int statsType) {
        this.monthStatsType.setValue(statsType);
    }


    /**
     * 년상태보기 운동종목 설정하는 함수
     * @param statsType 운동종목
     */
    public void setYearStatsType(int statsType) {
        this.yearStatsType.setValue(statsType);
    }

    /**
     * 전체보기 운동종목 설정하는 함수
     * @param statsType 운동종목
     */
    public void setTotalStatsType(int statsType) {
        this.totalStatsType.setValue(statsType);
    }

    /**
     * 기록확장 위치 설정하는 함수
     * @param position 위치
     */
    public void setExpandPosition(int position) {
        this.position.postValue(position);
    }

    /**
     * 기록보기 종목설정하는 함수
     * @param type 종목
     */
    public void setType(int type) {
        this.type.postValue(type);
    }

    /**
     * 자료 추가 및 갱신하는 함수
     * @param info 새로 추가 및 갱신될 자료
     */
    public void insertOrUpdate(WorkoutInfo info) {
        mRepository.insertOrUpdate(info);
    }

    /**
     * 자료를 삭제하는 함수
     * @param id 삭제할 자료의 id
     */
    public void deleteData(int id) {
        mRepository.deleteData(id);
    }
}
