package org.secuso.privacyfriendlyactivitytracker.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.activities.OrderEditActivity;
import org.secuso.privacyfriendlyactivitytracker.activities.StepActivity;
import org.secuso.privacyfriendlyactivitytracker.blood.BloodPressureActivity;
import org.secuso.privacyfriendlyactivitytracker.cycle.CycleActivity;
import org.secuso.privacyfriendlyactivitytracker.cycle.CycleInitActivity;
import org.secuso.privacyfriendlyactivitytracker.exercise.ExerciseActivity;
import org.secuso.privacyfriendlyactivitytracker.heart.HeartRateActivity;
import org.secuso.privacyfriendlyactivitytracker.layout.HistoryItemContainer;
import org.secuso.privacyfriendlyactivitytracker.mainItems.MainBloodPressureContainer;
import org.secuso.privacyfriendlyactivitytracker.mainItems.MainCycleContainer;
import org.secuso.privacyfriendlyactivitytracker.mainItems.MainExerciseContainer;
import org.secuso.privacyfriendlyactivitytracker.mainItems.MainHeartRateContainer;
import org.secuso.privacyfriendlyactivitytracker.mainItems.MainWaterContainer;
import org.secuso.privacyfriendlyactivitytracker.mainItems.MainWeightContainer;
import org.secuso.privacyfriendlyactivitytracker.models.BloodPressureInfo;
import org.secuso.privacyfriendlyactivitytracker.models.StepCount;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.CycleLength;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;
import org.secuso.privacyfriendlyactivitytracker.persistence.Step;
import org.secuso.privacyfriendlyactivitytracker.persistence.WalkingModes;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomCirclePercentBar;
import org.secuso.privacyfriendlyactivitytracker.utils.UnitHelper;
import org.secuso.privacyfriendlyactivitytracker.viewModel.BloodViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleLengthViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.ExerciseViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HealthOrderViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.HeartRateViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.StepCountViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WaterViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WeightViewModel;
import org.secuso.privacyfriendlyactivitytracker.water.WaterActivity;
import org.secuso.privacyfriendlyactivitytracker.weight.WeightActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * 건강항목들을 보여주는 화면으로써 개별적인 항목들의 상태를 보여주는 화면
 */
public class HealthFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    CustomCirclePercentBar mStepBar;
    TextView mDistance;
    TextView mCalorie;
    LinearLayout mStepArea;
    MainWeightContainer mWeight;
    MainBloodPressureContainer mBloodPressure;
    MainHeartRateContainer mHeartRate;
    MainWaterContainer mWater;
    MainExerciseContainer mExercise;
    MainCycleContainer mCycle;
    FrameLayout mLoading;
    TextView mEditBtn;
    GridLayout mHealthItemContainer;

    int mSteps = 0; // 표시할 오늘의 걸음수
    private boolean generatingReports;
    // loading 화면을 끝내기 위해 개별적인 항목들의 자료를 읽은 여부를 판단
    boolean isStepLoaded, isExerciseLoaded, isWeightLoaded, isBloodLoaded, isHeartRateLoaded, isWaterLoaded;
    boolean isCycleLengthOverflow = false; // 생리주기가 제정된 기간보다 많은지 판별
    int cycleLength, overflowCycleLength; // 제정된 생리주기기간 및 넘쳐난 생리주기기간

    Calendar today; // 오늘

    // 현시할 개별적인 항목들의 자료
    List<HistoryItemContainer.HistoryItemInfo> data = new ArrayList<>();

    StepCountViewModel stepCountViewModel;
    WeightViewModel weightViewModel;
    BloodViewModel bloodViewModel;
    HeartRateViewModel heartRateViewModel;
    WaterViewModel waterViewModel;
    ExerciseViewModel exerciseViewModel;
    CycleLengthViewModel cycleLengthViewModel;
    CycleViewModel cycleViewModel;
    HealthOrderViewModel healthOrderViewModel;

    public HealthFragment() {
        // Required empty public constructor
    }

    /**
     * 건강화면 instance를 생성하는 함수
     * @return
     */
    public static HealthFragment newInstance() {
        HealthFragment fragment = new HealthFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        today = Calendar.getInstance();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_health, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        isStepLoaded = isExerciseLoaded = isWeightLoaded = isBloodLoaded = isHeartRateLoaded = isWaterLoaded = false;

        mLoading = view.findViewById(R.id.loading);
        mStepArea = view.findViewById(R.id.main_step);
        mStepArea.setOnClickListener(this);
        mStepBar = view.findViewById(R.id.circle_bar);
        mDistance = view.findViewById(R.id.distance);
        mCalorie = view.findViewById(R.id.calorie);

        mWeight = view.findViewById(R.id.main_weight);
        mWeight.setOnClickListener(this);
        mBloodPressure = view.findViewById(R.id.main_blood_pressure);
        mBloodPressure.setOnClickListener(this);
        mHeartRate = view.findViewById(R.id.main_heart_rate);
        mHeartRate.setOnClickListener(this);
        mWater = view.findViewById(R.id.main_water);
        mWater.setOnClickListener(this);
        mExercise = view.findViewById(R.id.main_exercise);
        mExercise.setOnClickListener(this);
        mCycle = view.findViewById(R.id.main_cycle);
        mCycle.setOnClickListener(this);
        mEditBtn = view.findViewById(R.id.order_edit);
        mEditBtn.setOnClickListener(this);
        mHealthItemContainer = view.findViewById(R.id.health_item_container);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (mStepBar != null) {
            mStepBar.setGoal(Integer.parseInt(Objects.requireNonNull(sharedPref.getString(
                    getString(R.string.pref_daily_step_goal), "10000"))));
        }

        //건강항목순서감지 및 대면부갱신
        healthOrderViewModel = new ViewModelProvider(this).get(HealthOrderViewModel.class);
        healthOrderViewModel.getHealthOrder().observe(getViewLifecycleOwner(), orders -> {
            if (orders.size() > 0) {
                mHealthItemContainer.removeAllViews();
                String[] healthItems = getResources().getStringArray(R.array.health_item_list);
                int shownCount = 0;
                for (int i = 0; i < orders.size(); i ++) {
                    if (orders.get(i).getIsShown() == 1) {
                        shownCount++;
                        if (orders.get(i).getName().equals(healthItems[0])) {
                            mHealthItemContainer.addView(mWeight);
                            mWeight.setVisibility(View.VISIBLE);
                        } else if (orders.get(i).getName().equals(healthItems[1])) {
                            mHealthItemContainer.addView(mExercise);
                            mExercise.setVisibility(View.VISIBLE);
                        } else if (orders.get(i).getName().equals(healthItems[2])) {
                            mHealthItemContainer.addView(mBloodPressure);
                            mBloodPressure.setVisibility(View.VISIBLE);
                        } else if (orders.get(i).getName().equals(healthItems[3])) {
                            mHealthItemContainer.addView(mHeartRate);
                            mHeartRate.setVisibility(View.VISIBLE);
                        } else if (orders.get(i).getName().equals(healthItems[4])) {
                            mHealthItemContainer.addView(mWater);
                            mWater.setVisibility(View.VISIBLE);
                        } else if (orders.get(i).getName().equals(healthItems[5])) {
                            mHealthItemContainer.addView(mCycle);
                            mCycle.setVisibility(View.VISIBLE);
                        }
                    }
                }
                if (shownCount == 1) {
                    if (mHealthItemContainer.getChildAt(0).equals(mWeight)) {
                        mHealthItemContainer.addView(mExercise);
                        mExercise.setVisibility(View.INVISIBLE);
                    } else {
                        mHealthItemContainer.addView(mWeight);
                        mWeight.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

        //자료기지에서 걸음수얻기
        stepCountViewModel = new ViewModelProvider(this).get(StepCountViewModel.class);
        stepCountViewModel.setDate(Utils.getIntDate(Calendar.getInstance()));
        stepCountViewModel.instanceForDay();
        stepCountViewModel.dayStepData.observe(getViewLifecycleOwner(), this::generateData);

        //자료기지에서 몸무게자료얻기
        weightViewModel = new ViewModelProvider(this).get(WeightViewModel.class);
        weightViewModel.instanceForMain();
        weightViewModel.mainData.observe(getViewLifecycleOwner(), weights -> {
            List<WeightInfo> mainData = weightViewModel.convertWeightType(weights);
            data.clear();
            data.addAll(mainData);
            mWeight.setData(data);

            if (!isWeightLoaded) {
                isWeightLoaded = true;
                cancelLoading();
            }
        });

        //자료기지에서 마지막한개 혈압자료얻기
        bloodViewModel = new ViewModelProvider(this).get(BloodViewModel.class);
        bloodViewModel.instanceForMain();
        bloodViewModel.latestData.observe(getViewLifecycleOwner(), blood -> {
            List<BloodPressureInfo> latestData = new ArrayList<>();
            for (int i = 0; i < blood.size(); i ++) {
                BloodPressureInfo info = new BloodPressureInfo();
                info.setSystolicValue(blood.get(i).getSystolicValue());
                info.setDiastolicValue(blood.get(i).getDiastolicValue());
                info.setPulseValue(blood.get(i).getPulseValue());
                info.setDate(blood.get(i).getDate());
                info.setTime(blood.get(i).getTime());
                DateTime dateTime = new DateTime(blood.get(i).getMeasureMilliTime());
                info.setMeasureDateTime(dateTime);
                latestData.add(info);
            }
            data.clear();
            data.addAll(latestData);
            mBloodPressure.setData(data);

            if (!isBloodLoaded) {
                isBloodLoaded = true;
                cancelLoading();
            }
        });

        // 자료기지에서 마지막날자의 심박수자료 얻기
        heartRateViewModel = new ViewModelProvider(this).get(HeartRateViewModel.class);
        heartRateViewModel.instanceForMain();
        heartRateViewModel.mainData.observe(getViewLifecycleOwner(), data -> {
            mHeartRate.updateData(data);

            if (!isHeartRateLoaded) {
                isHeartRateLoaded = true;
                cancelLoading();
            }
        });

        //자료기지에서 최신 한개의 물자료 얻기
        if (sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0) != 0)
            mWater.changeTarget(sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0));
        else mWater.changeTarget(8);
        waterViewModel = new ViewModelProvider(this).get(WaterViewModel.class);
        waterViewModel.instanceForMain();
        waterViewModel.latestData.observe(getViewLifecycleOwner(), water -> {
            List<WaterInfo> latestData = new ArrayList<>();
            for (int i = 0; i < water.size(); i ++) {
                WaterInfo info = new WaterInfo();
                info.setGlasses(water.get(i).getGlasses());
                DateTime dateTime = new DateTime(water.get(i).getDate());
                info.setMeasureDateTime(dateTime);
                latestData.add(info);
            }
            data.clear();
            data.addAll(latestData);
            mWater.setData(data);

            if (!isWaterLoaded) {
                isWaterLoaded = true;
                cancelLoading();
            }
        });

        //자료기지에서 최신 한개의 운동자료얻기
        exerciseViewModel = new ViewModelProvider(this).get(ExerciseViewModel.class);
        exerciseViewModel.instanceForMain();
        exerciseViewModel.latestData.observe(getViewLifecycleOwner(), exercises -> {
            List<WorkoutInfo> latestData = new ArrayList<>();
            for (int i = 0; i < exercises.size(); i ++) {
                latestData.add(new WorkoutInfo(exercises.get(i)));
            }
            data.clear();
            data.addAll(latestData);
            mExercise.setData(data);

            if (!isExerciseLoaded) {
                isExerciseLoaded = true;
                cancelLoading();
            }
        });

        //자료기지에서 최신 한개의 생리자료얻기
        cycleLengthViewModel = new ViewModelProvider(this).get(CycleLengthViewModel.class);
        cycleViewModel = new ViewModelProvider(this).get(CycleViewModel.class);
        cycleViewModel.setDate(Utils.getIntDate(today));
        cycleViewModel.ovulationLiveData.observe(getViewLifecycleOwner(), data -> {
            int currentDate = Utils.getIntDate(today);
            Ovulation cycleData;
            isCycleLengthOverflow = false;

            if (cycleLengthViewModel.getCycleLengthData() != null) {
                cycleLength = cycleLengthViewModel.getCycleLengthData().getCycleLength();
            } else {
                cycleLength = 28;
            }

            if (data.size() > 1) {
                //첫자료의 생리기간이 생리주기와 같은 경우, 오늘이 첫자료의 생리기간에 속하지 않으면 첫자료의 생리기간과 다음자료의 생리기간을 합쳐 하나의 생리주기 생성 및 생리주기 늘이기
                //오늘이 첫자료의 생리기간에 속하면 가임기가 없는 생리주기 생성
                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodStart()), Utils.convertIntDateToCalendar(data.get(0).getPeriodEnd())) == cycleLength - 1) {
                    isCycleLengthOverflow = true;
                    overflowCycleLength = Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodStart()),
                            Utils.convertIntDateToCalendar(data.get(1).getPeriodStart()));
                }
                cycleData = new Ovulation(0, data.get(0).getPeriodStart(), data.get(0).getPeriodEnd(),
                        data.get(1).getFertileStart(), data.get(1).getFertileEnd(), data.get(0).getIsPredict());
            } else if (data.size() > 0) {
                //첫자료의 생리시작이 현재날자와 같으면 첫자료의 생리기간과 창조한 가임기간으로 새 생리주기 생성
                if (data.get(0).getPeriodStart() == currentDate) {
                    Calendar fertileStart = Utils.convertIntDateToCalendar(data.get(0).getPeriodStart());
                    fertileStart.add(Calendar.DAY_OF_MONTH, cycleLength);
                    Calendar fertileEnd = (Calendar) fertileStart.clone();
                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                    cycleData = new Ovulation(0, data.get(0).getPeriodStart(), data.get(0).getPeriodEnd(),
                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), data.get(0).getIsPredict());
                } else {
                    //첫자료의 생리시작날자와 현재날자와의 차이가 생리기간보다 작은 경우 첫자료의 생리기간과 창조한 가임기간으로 새 생리주기 생성
                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodStart()), today) < cycleLength) {
                        Calendar fertileStart = Utils.convertIntDateToCalendar(data.get(0).getPeriodStart());
                        fertileStart.add(Calendar.DAY_OF_MONTH, cycleLength);
                        Calendar fertileEnd = (Calendar) fertileStart.clone();
                        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                        cycleData = new Ovulation(0, data.get(0).getPeriodStart(), data.get(0).getPeriodEnd(),
                                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), data.get(0).getIsPredict());
                    } else {
                        cycleData = null;
                    }
                }
            } else {
                cycleData = null;
            }
            mCycle.setData(cycleData);
            mCycle.setCycleLength(isCycleLengthOverflow ? overflowCycleLength : cycleLength);
            mCycle.showValue(cycleData, isCycleLengthOverflow ? overflowCycleLength: cycleLength);
        });

        cycleLengthViewModel.cycleLengthLiveData.observe(getViewLifecycleOwner(), new Observer<CycleLength>() {
            @Override
            public void onChanged(CycleLength cycleLengthData) {
                if (cycleLengthData != null)
                    cycleLength = cycleLengthData.getCycleLength();
                else cycleLength = 28;
                mCycle.setCycleLength(isCycleLengthOverflow ? overflowCycleLength : cycleLength);
            }
        });

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.main_step) {
            startActivity(new Intent(getContext(), StepActivity.class));
        } else if (view.getId() == R.id.main_weight) {
            Intent intent = new Intent(getContext(), WeightActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.main_blood_pressure) {
            Intent intent = new Intent(getContext(), BloodPressureActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.main_heart_rate) {
            Intent intent = new Intent(getContext(), HeartRateActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.main_water) {
            Intent intent = new Intent(getContext(), WaterActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.main_exercise) {
            Intent intent = new Intent(getContext(), ExerciseActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.main_cycle) {
            Intent intent;
            CycleLength cycleLength = cycleLengthViewModel.getCycleLengthData();
            int cycleCount = cycleViewModel.getOvulationCount();
            if (cycleLength != null || cycleCount > 0) {
                intent = new Intent(getContext(), CycleActivity.class);
            } else {
                intent = new Intent(getContext(), CycleInitActivity.class);
            }
            startActivity(intent);
        } else if (view.getId() == R.id.order_edit) {
            Intent intent = new Intent(getContext(), OrderEditActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    /**
     * 걸음수 자료 생성하는 함수
     * @param data 자료기지에서 받은 걸음수 자료
     */
    @SuppressLint("SetTextI18n")
    private void generateData(List<Step> data) {
        if (isDetached() || generatingReports) {
            // 현재날자가 아니면 탈퇴
            return;
        }
        generatingReports = true;

        List<StepCount> stepCounts = new ArrayList<>();
        Calendar m = Calendar.getInstance();
        m.set(Calendar.HOUR_OF_DAY, 0);
        m.set(Calendar.MINUTE, 0);
        m.set(Calendar.SECOND, 0);

        StepCount s = new StepCount();
        s.setStartTime(m.getTimeInMillis());
        s.setEndTime(m.getTimeInMillis()); // one hour more
        s.setStepCount(0);
        s.setWalkingMode(null);
        stepCounts.add(s);
        StepCount previousStepCount = s;
        List<WalkingModes> allWalkingModes = stepCountViewModel.getAllWalkingModes();
        for (int h = 0; h < 24; h++) {
            m.set(Calendar.HOUR_OF_DAY, h);
            s = new StepCount();
            s.setStartTime(m.getTimeInMillis() + 1000);
            if(h != 23) {
                s.setEndTime(m.getTimeInMillis() + 3600000); // one hour more
            }else{
                s.setEndTime(m.getTimeInMillis() + 3599000); // one hour more - 1sec
            }
            s.setWalkingMode(previousStepCount.getWalkingMode());
            previousStepCount = s;
            // 자료기지에서 주어진 시작시간과 마감시간에 기초하여 자료얻기
            List<StepCount> stepCountsFromStorage = new ArrayList<>();
            for (int i = 0; i < data.size(); i ++) {
                if (data.get(i).getTimeStamp() >= s.getStartTime() && data.get(i).getTimeStamp() <= s.getEndTime()) {
                    StepCount stepCount = new StepCount();
                    stepCount.setStartTime(s.getStartTime());
                    stepCount.setEndTime(data.get(i).getTimeStamp());
                    stepCount.setStepCount(data.get(i).getStepCount());
                    stepCount.setWalkingMode(data.get(i).getWalkingMode() == 1 ? allWalkingModes.get(0) : allWalkingModes.get(1));
                    stepCountsFromStorage.add(stepCount);
                } else {
                    break;
                }
            }
            if (stepCountsFromStorage.size() > 0) {
                data.subList(0, stepCountsFromStorage.size()).clear();
            }

            for(StepCount stepCountFromStorage : stepCountsFromStorage){
                if(previousStepCount.getWalkingMode() == null || !previousStepCount.getWalkingMode().equals(stepCountFromStorage.getWalkingMode())){

                    long oldEndTime = s.getEndTime();
                    s.setEndTime(stepCountFromStorage.getStartTime() - 1000);
                    stepCounts.add(s);
                    previousStepCount = s;
                    if(previousStepCount.getWalkingMode() == null){
                        for (StepCount s1: stepCounts) {
                            s1.setWalkingMode(stepCountFromStorage.getWalkingMode());
                        }
                        previousStepCount.setWalkingMode(stepCountFromStorage.getWalkingMode());
                    }

                    s = new StepCount();
                    s.setStartTime(stepCountFromStorage.getStartTime());
                    s.setEndTime(oldEndTime);
                    s.setStepCount(stepCountFromStorage.getStepCount());
                    s.setWalkingMode(stepCountFromStorage.getWalkingMode());
                }else{
                    s.setStepCount(s.getStepCount() + stepCountFromStorage.getStepCount());
                }
            }
            stepCounts.add(s);
        }
        // chart자료생성
        int stepCount = 0;
        double distance = 0;
        double calories = 0;
        for (StepCount s1: stepCounts) {
            stepCount += s1.getStepCount();
            distance += s1.getDistance();
            calories += s1.getCalories();
        }
        mSteps = stepCount;
        mStepBar.setPercentData(mSteps, new DecelerateInterpolator());
        UnitHelper.FormattedUnitPair dailyDistance = UnitHelper.formatKilometers(UnitHelper.metersToKilometers(distance), getContext());
        UnitHelper.FormattedUnitPair dailyCalories = UnitHelper.formatCalories(calories, getContext());
        mDistance.setText(dailyDistance.getValue() + " " + dailyDistance.getUnit());
        mCalorie.setText(dailyCalories.getValue() +  " " + dailyCalories.getUnit());
        generatingReports = false;

        if (!isStepLoaded) {
            isStepLoaded = true;
            cancelLoading();
        }
    }

    /**
     * 화면읽기부분 없애는 함수
     */
    public void cancelLoading() {
        if (mLoading.getVisibility() == View.VISIBLE && isStepLoaded && isWeightLoaded &&
                isExerciseLoaded && isBloodLoaded && isHeartRateLoaded && isWaterLoaded) {
            mLoading.setVisibility(View.GONE);
        }
    }

    /**
     * SharedPreference 감지하는 함수
     * @param sharedPref sharedPreference instance
     * @param key 감지된 항목
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        if (key.equals(getString(R.string.pref_daily_water_goal))) {
            if (sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0) != 0)
                mWater.changeTarget(sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0));
            else mWater.changeTarget(8);
        } else if (key.equals(getString(R.string.pref_daily_step_goal))) {
            if (mStepBar != null) {
                mStepBar.setGoal(Integer.parseInt(Objects.requireNonNull(sharedPref.getString(
                        getString(R.string.pref_daily_step_goal), "10000"))));
            }
        }
    }
}