package org.secuso.privacyfriendlyactivitytracker.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.secuso.privacyfriendlyactivitytracker.models.ActivityChart;
import org.secuso.privacyfriendlyactivitytracker.models.ActivityDayChart;
import org.secuso.privacyfriendlyactivitytracker.models.ActivitySummary;
import org.secuso.privacyfriendlyactivitytracker.models.StepCount;
import org.secuso.privacyfriendlyactivitytracker.persistence.Step;
import org.secuso.privacyfriendlyactivitytracker.persistence.WalkingModes;
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
import java.util.TimeZone;

/**
 * 월별 걸음수현시화면
 */
public class MonthlyReportFragment extends Fragment implements ReportAdapter.OnItemClickListener {
    public static String LOG_TAG = WeeklyReportFragment.class.getName();

    private ReportAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private OnFragmentInteractionListener mListener;

    private Calendar day;
    private ActivitySummary activitySummary;
    private ActivityChart activityChart;
    private List<Object> reports = new ArrayList<>();
    private boolean generatingReports;
    private Map<Integer, WalkingModes> menuWalkingModes; // 방식묶음 달리기, 걷기

    StepCountViewModel mViewModel;

    public MonthlyReportFragment() {
        // Required empty public constructor
    }

    /**
     * 새 instance 생성함수
     *
     * @return MonthlyReportFragment 새 instance
     */
    public static MonthlyReportFragment newInstance() {
        MonthlyReportFragment fragment = new MonthlyReportFragment();
        Bundle args = new Bundle();
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

        day.set(Calendar.DAY_OF_MONTH, 1);
        mViewModel = new ViewModelProvider(requireActivity()).get(StepCountViewModel.class);
        mViewModel.instanceForMonth();
        updateMonthPeriod();
        mViewModel.monthStepData.observe(getViewLifecycleOwner(), steps -> generateData(false, steps));

        mAdapter = new ReportAdapter(reports);
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

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
    }

    @Override
    public void onResume() {
        super.onResume();
        if(day == null){
            day = Calendar.getInstance();
        }
        if(!day.getTimeZone().equals(TimeZone.getDefault())) {
            day = Calendar.getInstance();
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 현재 보여주는 날자가 오늘인지 아닌지 판단하는 함수
     * @return 오늘이면 true, 아니면 false
     */
    private boolean isTodayShown() {
        if(day == null){
            return false;
        }
        Calendar start = getStartDay();
        Calendar end = getEndDay();
        return (start.before(day) || start.equals(day)) && end.after(day);
    }

    /**
     * 달의 첫날자 얻는 함수
     * @return 달의 첫날자(Calendar)
     */
    private Calendar getStartDay(){
        if(day == null){
            return Calendar.getInstance();
        }
        Calendar start = (Calendar) day.clone();
        start.set(Calendar.DAY_OF_WEEK, day.getFirstDayOfWeek());
        start.set(Calendar.MILLISECOND, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.HOUR_OF_DAY, 0);
        return start;
    }

    /**
     * 달의 마지막 날자 얻는 함수
     * @return 달의 마감날자(Calendar)
     */
    private Calendar getEndDay(){
        Calendar end = getStartDay();
        end.add(Calendar.MONTH, 1);
        return end;
    }

    /**
     * 화면에 현시할 자료생성
     * @param updated 오늘자료가 갱신되였는지 판단
     * @param data 자료기지에서 받은 자료
     */
    private void generateData(boolean updated, List<Step> data) {
        Log.i(LOG_TAG, "Generating reports");
        if (!this.isTodayShown() && updated || isDetached() || getContext() == null || generatingReports) {
            // 현재 보여주는 날자가 오늘이 아니고 오늘자료가 갱신되였으면 탈퇴
            return;
        }
        generatingReports = true;
        final Context context = getActivity().getApplicationContext();
        final Locale locale = context.getResources().getConfiguration().locale;
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        final Calendar now = Calendar.getInstance();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Get all step counts for this day.
                day.set(Calendar.DAY_OF_MONTH, 1);
                Calendar start = (Calendar) day.clone();
                SimpleDateFormat formatDate = new SimpleDateFormat("MM.dd", locale);
                SimpleDateFormat fakeFormatDate = new SimpleDateFormat("MM-dd", locale);
                Map<String, Double> stepData = new LinkedHashMap<>();
                Map<String, Double> distanceData = new LinkedHashMap<>();
                Map<String, Double> caloriesData = new LinkedHashMap<>();
                stepData.put("", null);
                distanceData.put("", null);
                caloriesData.put("", null);
                int totalSteps = 0;
                double totalDistance = 0;
                double totalCalories = 0;
                List<WalkingModes> allWalkingModes = mViewModel.getAllWalkingModes();
                for (int i = 0; i <= day.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                    start.set(Calendar.HOUR_OF_DAY, 0);
                    start.set(Calendar.MINUTE, 0);
                    start.set(Calendar.SECOND, 0);
                    start.set(Calendar.MILLISECOND, 0);
                    List<StepCount> stepCounts = new ArrayList<>();
                    for (int j = 0; j < data.size(); j ++) {
                        if (data.get(j).getDate() == Utils.getIntDate(start)) {
                            StepCount stepCount = new StepCount();
                            stepCount.setStartTime(start.getTimeInMillis());
                            stepCount.setEndTime(data.get(j).getTimeStamp());
                            stepCount.setStepCount(data.get(j).getStepCount());
                            stepCount.setWalkingMode(data.get(j).getWalkingMode() == 1 ? allWalkingModes.get(0) : allWalkingModes.get(1));
                            stepCounts.add(stepCount);
                        } else break;
                    }
                    if (stepCounts.size() > 0) {
                        data.subList(0, stepCounts.size()).clear();
                    }
                    int steps = 0;
                    double distance = 0;
                    double calories = 0;

                    for (StepCount stepCount : stepCounts) {
                        steps += stepCount.getStepCount();
                        distance += stepCount.getDistance();
                        calories += stepCount.getCalories();
                    }
                    //기간이 월단위인 경우 월요일이면 MM.dd형식의 날자를 key로 한 자료 추가, 아니면 MM-dd형식의 날자를 key로 한 자료 추가
                    //기간이 주단위인 경우에는 MM.dd형식의 날자를 Key로 한 자료 추가
                    if (start.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) {
                        stepData.put(formatDate.format(start.getTime()), (double) steps);
                        distanceData.put(formatDate.format(start.getTime()), distance);
                        caloriesData.put(formatDate.format(start.getTime()), (double) calories);
                    } else {
                        stepData.put(fakeFormatDate.format(start.getTime()), (double) steps);
                        distanceData.put(fakeFormatDate.format(start.getTime()), distance);
                        caloriesData.put(fakeFormatDate.format(start.getTime()), (double) calories);
                    }
                    totalSteps += steps;
                    totalDistance += distance;
                    totalCalories += calories;
                    if (i != day.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                        start.add(Calendar.DAY_OF_MONTH, 1);
                    }
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM", locale);
                String title = getString(R.string.date_format5, day.get(Calendar.YEAR), simpleDateFormat.format(day.getTime()));

                // create view models
                if (activitySummary == null) {
                    activitySummary = new ActivitySummary(totalSteps, totalDistance, totalCalories, title);
                    Step firstStep = mViewModel.getFirstStepData();
                    Calendar firstDate = Calendar.getInstance();
                    if (firstStep != null) {
                        firstDate.setTimeInMillis(firstStep.getTimeStamp());
                    }
                    activitySummary.setHasPredecessor(Utils.getIntDate(firstDate) < Utils.getIntDate(day));
                    reports.add(activitySummary);
                } else {
                    activitySummary.setSteps(totalSteps);
                    activitySummary.setDistance(totalDistance);
                    activitySummary.setCalories(totalCalories);
                    activitySummary.setTitle(title);
                    activitySummary.setHasSuccessor(new Date().after(getEndDay().getTime()));
                    Step firstStep = mViewModel.getFirstStepData();
                    Calendar firstDate = Calendar.getInstance();
                    if (firstStep != null) {
                        firstDate.setTimeInMillis(firstStep.getTimeStamp());
                    }
                    activitySummary.setHasPredecessor(Utils.getIntDate(firstDate) < Utils.getIntDate(day));
                }


                if (activityChart == null) {
                    activityChart = new ActivityChart(stepData, distanceData, caloriesData, title);
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
        });

    }

    /**
     * viewmodel에 월기간 갱신하는 함수
     */
    public void updateMonthPeriod() {
        Calendar lastMonthDay = (Calendar) day.clone();
        lastMonthDay.add(Calendar.DAY_OF_MONTH, day.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
        List<Integer> monthPeriod = new ArrayList<>();
        monthPeriod.add(Utils.getIntDate(day));
        monthPeriod.add(Utils.getIntDate(lastMonthDay));
        mViewModel.setMonth(monthPeriod);
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
     * chart type의 check 상태변경하는 함수
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
        this.day.add(Calendar.MONTH, -1);
        updateMonthPeriod();
    }

    /**
     * 다음단추를 눌렀을때 호출되는 callback
     */
    @Override
    public void onNextClicked() {
        this.day.add(Calendar.MONTH, 1);
        updateMonthPeriod();
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
        DatePickerDialog dialog = new DatePickerDialog(getContext(), R.style.AppTheme_DatePickerDialog,
                (view, year1, monthOfYear, dayOfMonth) -> {
            MonthlyReportFragment.this.day.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            MonthlyReportFragment.this.day.set(Calendar.MONTH, monthOfYear);
            MonthlyReportFragment.this.day.set(Calendar.YEAR, year1);
            updateMonthPeriod();
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
        if (!menuWalkingModes.containsKey(id)) {
            return;
        }

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
