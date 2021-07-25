package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.persistence.CycleLength;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;
import org.secuso.privacyfriendlyactivitytracker.persistence.Symptom;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleLengthViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.SymptomViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 생리현시화면
 */
public class PeriodFragment extends Fragment implements View.OnClickListener, CircleCalendarView.OnDateSelectedListener {
    LinearLayout mSetArea;
    LinearLayout mFlowArea;
    LinearLayout mPainArea;
    LinearLayout mConditionArea;
    LinearLayout mPeriodSetArea;
    LinearLayout mWaitDescArea;
    LinearLayout mSymptomEmptyContent;
    CircleCalendarView mCircleCalendar;
    TextView mSelectedDate;
    TextView mDayStatus;
    TextView mTotalDays;
    TextView mNoDataDesc;
    TextView mAddCycleDesc;
    TextView mRecordPeriod;
    TextView mFlowIntensity;
    TextView mPainIntensity;
    TextView mConditions;
    ImageView mStats;
    TextView mCurrentDate;
    FrameLayout mCurrentCalendar;

    CycleViewModel cycleViewModel;
    CycleLengthViewModel cycleLengthViewModel;
    SymptomViewModel symptomViewModel;

    Calendar currentCalendar = Calendar.getInstance(); //오늘날자
    Calendar selectedCalendar = Calendar.getInstance(); //선택한 날자
    SimpleDateFormat selectedDayFormat;
    Ovulation cycleData; // 마지막생리자료
    int cycleLength, overflowCycleLength; //생리주기기간 및 넘쳐난 생리주기기간
    boolean isCycleLengthOverflow = false; // 생리주기기간이 넘쳐났는지 판별

    private OnFragmentInteractionListener mListener;

    CycleFragment mCycleFragment;

    public PeriodFragment(CycleFragment cycleFragment) {
        mCycleFragment = cycleFragment;
    }

    public static PeriodFragment newInstance(CycleFragment cycleFragment) {
        PeriodFragment fragment = new PeriodFragment(cycleFragment);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cycleViewModel = new ViewModelProvider(this).get(CycleViewModel.class);
        cycleLengthViewModel = new ViewModelProvider(requireActivity()).get(CycleLengthViewModel.class);
        symptomViewModel = new ViewModelProvider(this).get(SymptomViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_period, container, false);
        mStats = view.findViewById(R.id.stats);
        mStats.setOnClickListener(this);
        mCurrentCalendar = view.findViewById(R.id.current_calendar);
        mCurrentCalendar.setOnClickListener(this);
        mCurrentDate = view.findViewById(R.id.current_date);
        mSetArea = view.findViewById(R.id.set_area);
        mFlowArea = view.findViewById(R.id.flow_area);
        mFlowArea.setOnClickListener(this);
        mPainArea = view.findViewById(R.id.pain_area);
        mPainArea.setOnClickListener(this);
        mConditionArea = view.findViewById(R.id.condition_area);
        mConditionArea.setOnClickListener(this);
        mPeriodSetArea = view.findViewById(R.id.period_set_area);
        mPeriodSetArea.setVisibility(View.GONE);
        mCircleCalendar = view.findViewById(R.id.circle_calendar);
        mCircleCalendar.setOnDateSelectedListener(this);
        mSelectedDate = view.findViewById(R.id.selected_date);
        mDayStatus = view.findViewById(R.id.day_status);
        mTotalDays = view.findViewById(R.id.total_days);
        mNoDataDesc = view.findViewById(R.id.no_data_desc);
        mAddCycleDesc = view.findViewById(R.id.add_cycle_desc);
        mRecordPeriod = view.findViewById(R.id.record_period);
        mRecordPeriod.setOnClickListener(this);
        mFlowIntensity = view.findViewById(R.id.flow_intensity);
        mPainIntensity = view.findViewById(R.id.pain_intensity);
        mConditions = view.findViewById(R.id.conditions);
        mWaitDescArea = view.findViewById(R.id.wait_desc_area);
        mSymptomEmptyContent = view.findViewById(R.id.empty_content);

        cycleViewModel.setDate(Utils.getIntDate(currentCalendar));
        cycleViewModel.ovulationLiveData.observe(getViewLifecycleOwner(), data -> {
            int currentDate = Utils.getIntDate(currentCalendar);
            isCycleLengthOverflow = false;

            if (cycleLengthViewModel.getCycleLengthData() != null) {
                cycleLength = cycleLengthViewModel.getCycleLengthData().getCycleLength();
            } else {
                cycleLength = 28;
            }

            // 현재날자의 이전자료와 이후자료가 있는 경우, 첫자료의 생리기간과 둘째자료의 가임기간을 합쳐 새 생리주기생성
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
                //첫자료의 생리시작이 현재날자와 같으면 첫자료의 생리기간과 새로 추가된 가임기를 합쳐 새 생리주기 생성
                if (data.get(0).getPeriodStart() == currentDate) {
                    Calendar fertileStart = Utils.convertIntDateToCalendar(data.get(0).getPeriodStart());
                    fertileStart.add(Calendar.DAY_OF_MONTH, cycleLength);
                    Calendar fertileEnd = (Calendar) fertileStart.clone();
                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                    cycleData = new Ovulation(0, data.get(0).getPeriodStart(), data.get(0).getPeriodEnd(),
                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), data.get(0).getIsPredict());
                } else {
                    //현재날자와 첫자료의 생리시작의 차이가 생리주기보다 작으면 첫자료의 생리기간과 새로 추가된 가임기를 합쳐 새 자료 생성
                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodStart()), currentCalendar) < cycleLength) {
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

            if (cycleData != null)
                selectedCalendar = Calendar.getInstance();
            mCircleCalendar.setCycleLength(isCycleLengthOverflow ? overflowCycleLength : cycleLength);
            mTotalDays.setText(getResources().getString(R.string.total_days, isCycleLengthOverflow ? overflowCycleLength : cycleLength));
            mCircleCalendar.setData(cycleData, selectedCalendar);
            showStatusArea(cycleData != null);
            onDateSelected(selectedCalendar);
            mCycleFragment.cancelLoading();
        });

        cycleLengthViewModel.cycleLengthLiveData.observe(getViewLifecycleOwner(), cycleLengthData -> {
            cycleLength = cycleLengthData.getCycleLength();
            mCircleCalendar.setCycleLength(isCycleLengthOverflow ? overflowCycleLength : cycleLength);
            mTotalDays.setText(getResources().getString(R.string.total_days, isCycleLengthOverflow ? overflowCycleLength : cycleLength));
        });

        symptomViewModel.setDate(Utils.getIntDate(currentCalendar));
        symptomViewModel.dayData.observe(getViewLifecycleOwner(), symptom -> {
            if (symptom != null) {
                if (symptom.getFlowIntensity() > -1) {
                    String[] flowArray = getResources().getStringArray(R.array.flow_intensity_array);
                    mFlowIntensity.setText(flowArray[symptom.getFlowIntensity()]);
                } else {
                    mFlowIntensity.setText("");
                }

                if (symptom.getPainIntensity() > -1) {
                    String[] painArray = getResources().getStringArray(R.array.pain_intensity_array);
                    mPainIntensity.setText(painArray[symptom.getPainIntensity()]);
                } else {
                    mPainIntensity.setText("");
                }

                if (!symptom.getSymptoms().equals("")) {
                    String[] conditionArray = getResources().getStringArray(R.array.symptom_conditions);
                    String[] conditions = symptom.getSymptoms().split(" ");
                    StringBuilder condition = new StringBuilder();
                    for (int i = 0; i < conditions.length; i++) {
                        if (i == 0) {
                            condition.append(conditionArray[Integer.parseInt(conditions[i])]);
                        } else {
                            condition.append(", ").append(conditionArray[Integer.parseInt(conditions[i])]);
                        }
                    }
                    mConditions.setText(condition.toString());
                } else {
                    mConditions.setText("");
                }
            } else {
                mFlowIntensity.setText("");
                mPainIntensity.setText("");
                mConditions.setText("");
            }
        });

        selectedDayFormat = new SimpleDateFormat("MMM dd, EE", Locale.getDefault());
        mSelectedDate.setText(selectedDayFormat.format(new Date()));
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (OnFragmentInteractionListener) getParentFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    //선택된 날자의 상태를 보여주는 함수
    private void showStatus() {
        if (cycleData != null) {
            mSymptomEmptyContent.setVisibility(View.GONE);
            if (Utils.getIntDate(selectedCalendar) > Utils.getIntDate(currentCalendar)) {
                mSetArea.setVisibility(View.GONE);
                mWaitDescArea.setVisibility(View.VISIBLE);
            } else {
                mSetArea.setVisibility(View.VISIBLE);
                mWaitDescArea.setVisibility(View.GONE);
            }

            int selectedDate = Utils.getIntDate(selectedCalendar);
            if (selectedDate >= cycleData.periodStart && selectedDate <= cycleData.periodEnd) {
                mDayStatus.setText(getResources().getString( cycleData.getIsPredict() == 1 ?
                                R.string.predicted_period_day_status : R.string.period_day_status,
                        Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.periodStart), selectedCalendar) + 1
                ));
                mFlowArea.setVisibility(View.VISIBLE);
                mPainArea.setVisibility(View.VISIBLE);
            } else {
                mFlowArea.setVisibility(View.GONE);
                mPainArea.setVisibility(View.GONE);
                if (cycleData.fertileStart > 0) {
                    if (selectedDate < cycleData.fertileStart) {
                        int daysToFertileStart = Utils.getDiffDays(Utils.convertIntDateToCalendar(selectedDate), Utils.convertIntDateToCalendar(cycleData.fertileStart));
                        if (daysToFertileStart == 1) {
                            mDayStatus.setText(getResources().getString(R.string.fertile_closer_status_one));
                        } else {
                            mDayStatus.setText(getResources().getString(R.string.fertile_closer_status, daysToFertileStart));
                        }
                    } else if (selectedDate <= cycleData.fertileEnd) {
                        mDayStatus.setText(getResources().getString(R.string.fertile_day_status,
                                Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.fertileStart), selectedCalendar) + 1));
                    } else {
                        int daysToNext = (isCycleLengthOverflow ? overflowCycleLength : cycleLengthViewModel.getCycleLengthData().getCycleLength())
                                - Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.periodStart), Utils.convertIntDateToCalendar(selectedDate));
                        mDayStatus.setText(daysToNext == 1 ? getResources().getString(R.string.predicted_period_closer_status_one) :
                                getResources().getString(R.string.predicted_period_closer_status, daysToNext));
                    }
                } else {
                    int daysToNext = cycleLengthViewModel.getCycleLengthData().getCycleLength() - Utils.getDiffDays(Utils.convertIntDateToCalendar(cycleData.periodStart),
                            Utils.convertIntDateToCalendar(selectedDate));
                    mDayStatus.setText(daysToNext == 1 ? getResources().getString(R.string.predicted_period_closer_status_one) :
                            getResources().getString(R.string.predicted_period_closer_status, daysToNext));
                }
            }
        } else {
            mSymptomEmptyContent.setVisibility(View.VISIBLE);
            mSetArea.setVisibility(View.GONE);
            mWaitDescArea.setVisibility(View.GONE);
        }
    }

    //상태표시부분을 현시 및 보여주지 않도록 하는 함수
    private void showStatusArea(boolean isEnabled) {
        mSelectedDate.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        mDayStatus.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        mTotalDays.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        mNoDataDesc.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
        mAddCycleDesc.setVisibility(isEnabled ? View.GONE : View.VISIBLE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.flow_area:
            case R.id.pain_area:
            case R.id.condition_area:
                Intent intent = new Intent(getContext(), SymptomsActivity.class);
                intent.putExtra("enabledIntensity", mFlowArea.getVisibility() == View.VISIBLE);
                intent.putExtra("selectedDate", Utils.getIntDate(selectedCalendar));
                startActivity(intent);
                break;
            case R.id.record_period:
                mListener.changeFragment();
                break;
            case R.id.current_calendar:
                onDateSelected(currentCalendar);
                mCircleCalendar.selectCurrentDay();
                break;
            case R.id.stats:
                Intent statsIntent = new Intent(getContext(), CycleStatsActivity.class);
                startActivity(statsIntent);
                break;
        }
    }

    /**
     * 날자를 선택했을때 호출되는 callback
     * @param selectedDate 선택된 날자
     */
    @Override
    public void onDateSelected(Calendar selectedDate) {
        selectedCalendar = selectedDate;
        mSelectedDate.setText(selectedDayFormat.format(new Date(selectedDate.getTimeInMillis())));
        symptomViewModel.setDate(Utils.getIntDate(selectedCalendar));
        showStatus();
        if (cycleData != null) {
            if (Utils.getIntDate(selectedCalendar) != Utils.getIntDate(currentCalendar)) {
                mCurrentCalendar.setVisibility(View.VISIBLE);
                mCurrentDate.setText(String.valueOf(currentCalendar.get(Calendar.DAY_OF_MONTH)));
            } else {
                mCurrentCalendar.setVisibility(View.GONE);
            }
        } else {
            mCurrentCalendar.setVisibility(View.GONE);
        }
    }

    public interface OnFragmentInteractionListener {
        public void changeFragment();
    }
}