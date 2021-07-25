package org.secuso.privacyfriendlyactivitytracker.fragments;

import android.app.DatePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.adapters.ReportAdapter;
import org.secuso.privacyfriendlyactivitytracker.models.ActivityChartDataSet;
import org.secuso.privacyfriendlyactivitytracker.models.ActivityDayChart;
import org.secuso.privacyfriendlyactivitytracker.models.ActivitySummary;
import org.secuso.privacyfriendlyactivitytracker.models.StepCount;
import org.secuso.privacyfriendlyactivitytracker.persistence.Step;
import org.secuso.privacyfriendlyactivitytracker.persistence.WalkingModes;
import org.secuso.privacyfriendlyactivitytracker.services.MovementSpeedService;
import org.secuso.privacyfriendlyactivitytracker.viewModel.StepCountViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

/**
 * 일별 걸음수현시화면
 */
public class DailyReportFragment extends Fragment implements ReportAdapter.OnItemClickListener {
    public static String LOG_TAG = DailyReportFragment.class.getName();
    private ReportAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private OnFragmentInteractionListener mListener;
    private ActivitySummary activitySummary;
    private ActivityDayChart activityChart;
    private List<Object> reports = new ArrayList<>();
    private Calendar day;
    private boolean generatingReports;
    private Map<Integer, WalkingModes> menuWalkingModes; // 방식묶음 달리기, 걷기
    private int menuCorrectStepId;

    StepCountViewModel mViewModel;

    private MovementSpeedService.MovementSpeedBinder movementSpeedBinder;
    private ServiceConnection mMovementSpeedServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            movementSpeedBinder = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            movementSpeedBinder = (MovementSpeedService.MovementSpeedBinder) service;
        }
    };

    public DailyReportFragment() {
        // Required empty public constructor
    }

    /**
     * 새 instance 생성함수
     * @return DailyReportFragment 새 instance
     */
    public static DailyReportFragment newInstance() {
        DailyReportFragment fragment = new DailyReportFragment();
        Bundle args = new Bundle();
        // args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        day = Calendar.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_report, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        mViewModel = new ViewModelProvider(requireActivity()).get(StepCountViewModel.class);
        mViewModel.setDate(Utils.getIntDate(day));
        mViewModel.instanceForDay();
        mViewModel.dayStepData.observe(getViewLifecycleOwner(), steps -> generateData(false, steps));

        mAdapter = new ReportAdapter(reports);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(layoutManager);

        //자료내용에서의 변경들이 RecyclerView 의 layout 크기를 변경하지 않으면 이 설정을 리용하여 성능 개선
        mRecyclerView.setHasFixedSize(true);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        if(day == null){
            day = Calendar.getInstance();
        }
        if(!day.getTimeZone().equals(TimeZone.getDefault())) {
            day = Calendar.getInstance();
        }
        bindMovementSpeedService();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(day == null){
            day = Calendar.getInstance();
        }
        if(!day.getTimeZone().equals(TimeZone.getDefault())) {
            day = Calendar.getInstance();
        }
        bindMovementSpeedService();
    }

    @Override
    public void onDetach() {
        unbindMovementSpeedService();
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onPause(){
        unbindMovementSpeedService();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unbindMovementSpeedService();
        super.onDestroy();
    }

    //MovementSpeedService를  bind하는 함수
    private void bindMovementSpeedService(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        boolean isVelocityEnabled = sharedPref.getBoolean(getString(R.string.pref_show_velocity), false);
        if(movementSpeedBinder == null && isVelocityEnabled){
            Intent serviceIntent = new Intent(getContext(), MovementSpeedService.class);
            getActivity().getApplicationContext().startService(serviceIntent);
            getActivity().getApplicationContext().bindService(serviceIntent, mMovementSpeedServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    //MovementSpeedService를  unbind하는 함수
    private void unbindMovementSpeedService(){
        if(movementSpeedBinder != null && mMovementSpeedServiceConnection != null && movementSpeedBinder.getService() != null){
            getActivity().getApplicationContext().unbindService(mMovementSpeedServiceConnection);
            movementSpeedBinder = null;
        }
        Intent serviceIntent = new Intent(getContext(), MovementSpeedService.class);
        getActivity().getApplicationContext().stopService(serviceIntent);
    }

    /**
     * 현재보여주는 날자가 오늘인지 아닌지 판단하는 함수
     * @return 오늘이면 true, 아니면 false
     */
    private boolean isTodayShown() {
        return (Calendar.getInstance().get(Calendar.YEAR) == day.get(Calendar.YEAR) &&
                Calendar.getInstance().get(Calendar.MONTH) == day.get(Calendar.MONTH) &&
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == day.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 화면에 현시할 자료생성
     * @param updated 오늘자료가 갱신되였는지 판단
     * @param data 자료기지에서 받은 자료
     */
    private void generateData(boolean updated, List<Step> data) {
        if (!this.isTodayShown() && updated || isDetached() || getContext() == null || generatingReports) {
            // 현재 보여주는 날자가 오늘이 아니고 오늘자료가 갱신되였으면 탈퇴
            return;
        }

        generatingReports = true;

        final Context context = getActivity().getApplicationContext();
        final Locale locale = context.getResources().getConfiguration().locale;
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        List<StepCount> stepCounts = new ArrayList<>();
        SimpleDateFormat formatHourMinute = new SimpleDateFormat("HH:mm", locale);
        Calendar m = (Calendar) day.clone();
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
        List<WalkingModes> allWalkingModes = mViewModel.getAllWalkingModes();
        for (int h = 0; h < 24; h ++) {
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
        // chart 자료생성
        int stepCount = 0;
        double distance = 0;
        double calories = 0;
        Map<String, ActivityChartDataSet> stepData = new LinkedHashMap<>();
        Map<String, ActivityChartDataSet> distanceData = new LinkedHashMap<>();
        Map<String, ActivityChartDataSet> caloriesData = new LinkedHashMap<>();
        for (StepCount s1: stepCounts) {
            stepCount += s1.getStepCount();
            distance += s1.getDistance();
            calories += s1.getCalories();
            if (!stepData.containsKey(formatHourMinute.format(s1.getEndTime())) ||
                    stepData.get(formatHourMinute.format(s1.getEndTime())).getStepCount().getStepCount() < stepCount) {
                    stepData.put(formatHourMinute.format(s1.getEndTime()), new ActivityChartDataSet(stepCount, s1));
                    distanceData.put(formatHourMinute.format(s1.getEndTime()), new ActivityChartDataSet(distance, s1));
                    caloriesData.put(formatHourMinute.format(s1.getEndTime()), new ActivityChartDataSet(calories, s1));
            }else{
                Log.i(LOG_TAG, "Skipping put operation");
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", locale);
        String title = getString(R.string.date_format7, simpleDateFormat.format(day.getTime()), day.get(Calendar.DAY_OF_MONTH));

        if (activitySummary == null) {
            activitySummary = new ActivitySummary(stepCount, distance, calories, title);
            activitySummary.setHasSuccessor(!this.isTodayShown());
            Step firstStep = mViewModel.getFirstStepData();
            Calendar firstDate = Calendar.getInstance();
            if (firstStep != null) {
                firstDate.setTimeInMillis(firstStep.getTimeStamp());
            }
            activitySummary.setHasPredecessor(Utils.getIntDate(firstDate) < Utils.getIntDate(day));
            reports.add(activitySummary);
        } else {
            activitySummary.setSteps(stepCount);
            activitySummary.setDistance(distance);
            activitySummary.setCalories(calories);
            activitySummary.setTitle(title);
            activitySummary.setHasSuccessor(!this.isTodayShown());
            Step firstStep = mViewModel.getFirstStepData();
            Calendar firstDate = Calendar.getInstance();
            if (firstStep != null) {
                firstDate.setTimeInMillis(firstStep.getTimeStamp());
            }
            activitySummary.setHasPredecessor(Utils.getIntDate(firstDate) < Utils.getIntDate(day));
            boolean isVelocityEnabled = sharedPref.getBoolean(getString(R.string.pref_show_velocity), false);
            if(movementSpeedBinder != null  && isVelocityEnabled){
                activitySummary.setCurrentSpeed(movementSpeedBinder.getSpeed());
            }else{
                activitySummary.setCurrentSpeed(null);
            }
        }

        if (activityChart == null) {
            activityChart = new ActivityDayChart(stepData, distanceData, caloriesData, title);
            activityChart.setDisplayedDataType(ActivityDayChart.DataType.STEPS);
            reports.add(activityChart);
        } else {
            activityChart.setSteps(stepData);
            activityChart.setDistance(distanceData);
            activityChart.setCalories(caloriesData);
            activityChart.setTitle(title);
        }
        String d = sharedPref.getString(context.getString(R.string.pref_daily_step_goal), "10000");
        activityChart.setGoal(Integer.valueOf(d));

        // UI 갱신
        if (mAdapter != null && mRecyclerView != null && getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!mRecyclerView.isComputingLayout()) {
                        mAdapter.notifyDataSetChanged();
                    }else{
                        Log.w(LOG_TAG, "Cannot inform adapter for changes - RecyclerView is computing layout.");
                    }
                }
            });
        } else {
            Log.w(LOG_TAG, "Cannot inform adapter for changes.");
        }
        generatingReports = false;
    }

    /**
     * 걸음, 거리, 카로리 chart type을 변경하였을때 호출되는 callback
     * @param newDataType 변경된 chart type
     */
    @Override
    public void onActivityChartDataTypeClicked(ActivityDayChart.DataType newDataType) {
        Log.i(LOG_TAG, "Changing  displayed data type to " + newDataType.toString());
        if (this.activityChart == null) {
            return;
        }
        if (this.activityChart.getDisplayedDataType() == newDataType) {
            return;
        }
        this.activityChart.setDisplayedDataType(newDataType);
        if (this.mAdapter != null) {
            this.mAdapter.notifyItemChanged(this.reports.indexOf(this.activityChart));
        }
    }

    /**
     * chart type의 check상태변경하는 함수
     * @param menu chart type menu
     */
    @Override
    public void setActivityChartDataTypeChecked(Menu menu) {
        if (this.activityChart == null) {
            return;
        }
        if (this.activityChart.getDisplayedDataType() == null) {
            menu.findItem(R.id.menu_steps).setChecked(true);
        }
        switch (this.activityChart.getDisplayedDataType()) {
            case DISTANCE:
                menu.findItem(R.id.menu_distance).setChecked(true);
                break;
            case CALORIES:
                menu.findItem(R.id.menu_calories).setChecked(true);
                break;
            case STEPS:
            default:
                menu.findItem(R.id.menu_steps).setChecked(true);
        }
    }

    /**
     * 이전단추를 눌렀을때 호출되는 callback
     */
    @Override
    public void onPrevClicked() {
        this.day.add(Calendar.DAY_OF_MONTH, -1);
        mViewModel.setDate(Utils.getIntDate(day));

//        Calendar calendar = (Calendar) day.clone();
//        calendar.add(Calendar.DAY_OF_MONTH, -1);
//        generateFakeData(calendar);
    }

    /**
     * 다음단추를 눌렀을때 호출되는 callback
     */
    @Override
    public void onNextClicked() {
        this.day.add(Calendar.DAY_OF_MONTH, 1);
        mViewModel.setDate(Utils.getIntDate(day));
    }

    /**
     * 날자를 눌렀을때 호출되는 callback
     */
    @Override
    public void onTitleClicked() {
        int year = this.day.get(Calendar.YEAR);
        int month = this.day.get(Calendar.MONTH);
        int day = this.day.get(Calendar.DAY_OF_MONTH);

        // 날자선택대화창 instance생성 및 보여주기
        DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.AppTheme_DatePickerDialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                DailyReportFragment.this.day.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                DailyReportFragment.this.day.set(Calendar.MONTH, monthOfYear);
                DailyReportFragment.this.day.set(Calendar.YEAR, year);
                mViewModel.setDate(Utils.getIntDate(DailyReportFragment.this.day));
            }
        }, year, month, day);
        dialog.getDatePicker().setMaxDate(new Date().getTime()); // Max date is today
        Step firstStep = mViewModel.getFirstStepData();
        Date firstDate = Calendar.getInstance().getTime();
        if (firstStep != null) {
            firstDate.setTime(firstStep.getTimeStamp());
        }
        dialog.getDatePicker().setMinDate(firstDate.getTime());
        dialog.show();
    }

    /**
     * 걷기방식 Menu생성하는 함수
     * @param menu 걷기방식 menu
     */
    @Override
    public void inflateWalkingModeMenu(Menu menu) {
        // menu에 걷기방식들 추가
        menu.clear();
        menuWalkingModes = new HashMap<>();
        List<WalkingModes> walkingModes = mViewModel.getAllWalkingModes();
        int i = 0;
        for (WalkingModes walkingMode : walkingModes) {
            int id = Menu.FIRST + (i++);
            menuWalkingModes.put(id, walkingMode);
            menu.add(0, id, Menu.NONE, walkingMode.getName().equals("Walking") ? getString(R.string.step_walking) : getString(R.string.step_running))
                    .setChecked(walkingMode.getIsActive() == 1);
        }
        menu.setGroupCheckable(0, true, true);
    }

    /**
     * 걷기방식 항목을 선택하였을때 호출되는 callback
     * @param id 항목 id
     */
    @Override
    public void onWalkingModeClicked(int id) {
        if (menuWalkingModes.containsKey(id)) {
            // 선택된 걷기방식을 자료기지에 갱신
            WalkingModes walkingMode = menuWalkingModes.get(id);
            if (walkingMode.getIsActive() != 1) {
                mViewModel.updateActiveMode(new WalkingModes(walkingMode.getId(), walkingMode.getName(), walkingMode.getStepSize() ,
                        walkingMode.getStepFrequency(), 1, walkingMode.getDeleted()));
                WalkingModes other = menuWalkingModes.get(walkingMode.getId() == 1 ? 2 : 1);
                mViewModel.updateActiveMode(new WalkingModes(other.getId(), other.getName(), other.getStepSize() ,
                        other.getStepFrequency(), 0, other.getDeleted()));
            }
            Toast.makeText(getContext(), walkingMode.getId() == 1 ? R.string.step_walking_mode_change_note :
                    R.string.step_running_mode_change_note, Toast.LENGTH_SHORT).show();
        }
    }

    //시험용 자료 생성
    private void generateFakeData(Calendar calendar) {
        for (int i = 0; i < 24; i ++) {
            calendar.set(Calendar.HOUR_OF_DAY, new Random().nextInt(24));
            Step step = new Step(0, new Random().nextInt(100) + 100, 1, Utils.getIntDate(calendar), calendar.getTimeInMillis());
            mViewModel.insertOrUpdateStepData(step);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnFragmentInteractionListener {
        // Currently doing nothing here.
    }
}
