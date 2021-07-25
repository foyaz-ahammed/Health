package org.secuso.privacyfriendlyactivitytracker.cycle;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.haibin.calendarview.CalendarView;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.persistence.CycleLength;
import org.secuso.privacyfriendlyactivitytracker.persistence.Ovulation;
import org.secuso.privacyfriendlyactivitytracker.persistence.Symptom;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleLengthViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.CycleViewModel;
import org.secuso.privacyfriendlyactivitytracker.viewModel.SymptomViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 생리기록화면
 */
public class RecordFragment extends Fragment implements View.OnClickListener,
        CalendarView.OnMonthChangeListener, CalendarView.OnCalendarSelectListener {
    LinearLayout mSetArea;
    LinearLayout mWaitDescArea;
    LinearLayout mPeriodSetArea;
    LinearLayout mFlowArea;
    LinearLayout mPainArea;
    LinearLayout mConditionArea;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch mPeriodToggle;
    TextView mPeriodTitle;
    TextView mFlowIntensity;
    TextView mPainIntensity;
    TextView mConditions;
    TextView mDayStatus;
    TextView mTotalDay;
    ImageView mStats;
    FrameLayout mCurrentCalendar;
    TextView mCurrentDate;
    CalendarView mCalendarView;
    TextView mCalendarTitle;

    CycleViewModel cycleViewModel;
    SymptomViewModel symptomViewModel;
    CycleLengthViewModel cycleLengthViewModel;

    Calendar selectedCalendar; // 선택한 날자
    SimpleDateFormat dateFormat, monthYearFormat;
    boolean isDateSelected = true; //날자선택판별
    boolean isIntensityEnabled = false;
    List<Ovulation> cycleData = new ArrayList<>();
    int mPeriodLength, mCycleLength; //생리기간 및 생리주기기간

    public RecordFragment() {
        // Required empty public constructor
    }

    public static RecordFragment newInstance() {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        selectedCalendar = Calendar.getInstance();
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);

        cycleViewModel = new ViewModelProvider(requireActivity()).get(CycleViewModel.class);
        symptomViewModel = new ViewModelProvider(requireActivity()).get(SymptomViewModel.class);
        cycleLengthViewModel = new ViewModelProvider(requireActivity()).get(CycleLengthViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        mCalendarView = view.findViewById(R.id.calendar_view);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnMonthChangeListener(this);
        mCalendarTitle = view.findViewById(R.id.calendar_title);
        mStats = view.findViewById(R.id.stats);
        mStats.setOnClickListener(this);
        mCurrentCalendar = view.findViewById(R.id.current_calendar);
        mCurrentCalendar.setOnClickListener(this);
        mCurrentDate = view.findViewById(R.id.current_date);
        mSetArea = view.findViewById(R.id.set_area);
        mWaitDescArea = view.findViewById(R.id.wait_desc_area);
        mPeriodSetArea = view.findViewById(R.id.period_set_area);
        mFlowArea = view.findViewById(R.id.flow_area);
        mFlowArea.setOnClickListener(this);
        mPainArea = view.findViewById(R.id.pain_area);
        mPainArea.setOnClickListener(this);
        mConditionArea = view.findViewById(R.id.condition_area);
        mConditionArea.setOnClickListener(this);
        mFlowIntensity = view.findViewById(R.id.flow_intensity);
        mPainIntensity = view.findViewById(R.id.pain_intensity);
        mConditions = view.findViewById(R.id.conditions);
        mPeriodTitle = view.findViewById(R.id.period_title);
        mPeriodToggle = view.findViewById(R.id.period_toggle);
        mDayStatus = view.findViewById(R.id.day_status);
        mTotalDay = view.findViewById(R.id.total_day);

        mPeriodToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //생리기간 switch상태를 변경하면 자료기지 갱신
                if (!isDateSelected) {
                    int selectedDate = Utils.getIntDate(selectedCalendar);
                    List<Ovulation> obtainedData = new ArrayList<>(cycleData);
                    //생리 자료기지가 1개이상의 자료를 가지고 있는 경우
                    if (obtainedData.size() > 0) {
                        // 선택된 날자가 첫자료의 생리첫날자보다 이전인 경우
                        if (selectedDate < obtainedData.get(0).getPeriodStart()) {
                            //선택된 날자와 첫자료의 생리시작날자와의 차이가 10일이상인 경우, 새 자료추가 및 첫자료의 가임기 갱신
                            if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart())) >= 10) {
                                Calendar periodStart = (Calendar) selectedCalendar.clone();
                                Calendar periodEnd = (Calendar) selectedCalendar.clone();
                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                Calendar fertileStart = (Calendar) selectedCalendar.clone();
                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                Calendar fertileEnd = (Calendar) selectedCalendar.clone();
                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                        Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 0));

                                if (Utils.getDiffDays(periodEnd, Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart())) < 20) {
                                    if (Utils.getDiffDays(periodEnd, Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart())) > 10) {
                                        Calendar nextFertileStart = (Calendar) periodEnd.clone();
                                        nextFertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                obtainedData.get(0).getPeriodEnd(), Utils.getIntDate(nextFertileStart),
                                                obtainedData.get(0).getFertileEnd(), obtainedData.get(0).getIsPredict()));
                                    } else {
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                obtainedData.get(0).getPeriodEnd(), -1, -1, obtainedData.get(0).getIsPredict()));
                                    }
                                }
                                //선택된 날자와 첫자료의 생리시작날자와의 차이가 10일이하인 경우
                            } else {
                                //선택된 날자와 첫자료의 생리마감날자와의 차이가 생리주기보다 긴 경우, 경고창 표시
                                if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd())) >= mCycleLength) {
                                    Toast.makeText(getContext(), R.string.period_length_limit_desc, Toast.LENGTH_SHORT).show();
                                    mPeriodToggle.setChecked(false);
                                //선택된 날자와 첫자료의 생리마감날자와의 차이가 생리주기보다 짦은 경우, 첫자료 갱신
                                } else {
                                    Calendar periodStart = (Calendar) selectedCalendar.clone();
                                    Calendar fertileStart = (Calendar) selectedCalendar.clone();
                                    Calendar fertileEnd = (Calendar) selectedCalendar.clone();
                                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), Utils.getIntDate(periodStart),
                                            obtainedData.get(0).getPeriodEnd(), Utils.getIntDate(fertileStart),
                                            Utils.getIntDate(fertileEnd), obtainedData.get(0).getIsPredict()));
                                    Ovulation nextOvulation = cycleViewModel.getNextOvulation(obtainedData.get(0).getPeriodEnd());
                                    //첫자료를 얻은후에 실지 자료가 존재하지 않으면 예상자료 갱신
                                    if (nextOvulation == null) {
                                        cycleViewModel.deletePredictOvulation();
                                        if (Utils.getDiffDays(periodStart, Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd())) == mCycleLength - 1) {
                                            Calendar beforePeriodStart = Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd());
                                            beforePeriodStart.add(Calendar.DAY_OF_MONTH, 1);
                                            addPredictOvulation(beforePeriodStart);
                                        } else {
                                            periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                            Calendar periodEnd = (Calendar) periodStart.clone();
                                            periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                            fertileEnd = (Calendar) periodStart.clone();
                                            if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), periodStart) > 10) {
                                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), periodStart) >= 20) {
                                                    fertileStart = (Calendar) periodStart.clone();
                                                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                } else {
                                                    fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd());
                                                    fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                }
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                        Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                            } else {
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                        -1, -1, 1));
                                            }
                                            periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                            periodEnd = (Calendar) periodStart.clone();
                                            periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                            fertileStart = (Calendar) periodStart.clone();
                                            fertileEnd = (Calendar) periodStart.clone();
                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                    Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                            periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                            periodEnd = (Calendar) periodStart.clone();
                                            periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                            fertileStart = (Calendar) periodStart.clone();
                                            fertileEnd = (Calendar) periodStart.clone();
                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                    Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                        }
                                    }
                                }
                            }
                        //선택된 날자와 첫자료의 생리시작날자가 같은 경우
                        }
                        else if (selectedDate == obtainedData.get(0).getPeriodStart()) {
                            //첫자료가 예상자료이면 새 자료 추가 및 예상자료 갱신
                            if (obtainedData.get(0).getIsPredict() == 1) {
                                Calendar periodStart = (Calendar) selectedCalendar.clone();
                                Calendar periodEnd = (Calendar) periodStart.clone();
                                Calendar fertileStart = (Calendar) periodStart.clone();
                                Calendar fertileEnd = (Calendar) periodStart.clone();
                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                Ovulation ovulation = cycleViewModel.getPrevOvulation(selectedDate);
                                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) > 10) {
                                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) >= 20) {
                                        fertileStart = (Calendar) periodStart.clone();
                                        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                    } else {
                                        fertileStart = Utils.convertIntDateToCalendar(ovulation.getPeriodEnd());
                                        fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                    }
                                    cycleViewModel.deletePredictOvulation();
                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 0));
                                    addPredictOvulation(periodStart);
                                } else {
                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) > 5) {
                                        cycleViewModel.deletePredictOvulation();
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                -1, -1, 0));
                                        addPredictOvulation(periodStart);
                                    } else {
                                        if (Math.abs(Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodStart()),
                                                selectedCalendar)) >= mCycleLength) {
                                            Toast.makeText(getContext(), R.string.period_length_limit_desc, Toast.LENGTH_SHORT).show();
                                            mPeriodToggle.setChecked(false);
                                        }
                                    }
                                }
                             //첫자료가 예상자료가 아닌 실지자료인 경우
                            }
                            else {
                                cycleViewModel.deleteOvulation(obtainedData.get(0).getId());
                                Ovulation ovulation = cycleViewModel.getPrevOvulation(selectedDate);
                                //둘째자료가 예상자료이면 예상자료 갱신
                                if (obtainedData.size() > 1) {
                                    if (obtainedData.get(1).getIsPredict() == 1) {
                                        cycleViewModel.deletePredictOvulation();
                                        if (ovulation != null) {
                                            if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodStart()),
                                                    Utils.convertIntDateToCalendar(ovulation.getPeriodEnd())) == mCycleLength - 1) {
                                                Calendar calendar = Utils.convertIntDateToCalendar(ovulation.getPeriodEnd());
                                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                                                addPredictOvulation(calendar);
                                            } else {
                                                Calendar periodStart = Utils.convertIntDateToCalendar(ovulation.getPeriodStart());
                                                periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                                Calendar periodEnd = (Calendar) periodStart.clone();
                                                Calendar fertileStart = (Calendar) periodStart.clone();
                                                Calendar fertileEnd = (Calendar) periodStart.clone();
                                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), periodStart) > 10) {
                                                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), periodStart) >= 20) {
                                                        fertileStart = (Calendar) periodStart.clone();
                                                        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                    } else {
                                                        fertileStart = Utils.convertIntDateToCalendar(ovulation.getPeriodEnd());
                                                        fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                    }
                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                                } else {
                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                            -1, -1, 1));
                                                }
                                                periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                                periodEnd = (Calendar) periodStart.clone();
                                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                fertileStart = (Calendar) periodStart.clone();
                                                fertileEnd = (Calendar) periodStart.clone();
                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                        Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                                periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                                periodEnd = (Calendar) periodStart.clone();
                                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                fertileStart = (Calendar) periodStart.clone();
                                                fertileEnd = (Calendar) periodStart.clone();
                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                        Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                            }
                                        }
                                        //둘째자료가 실지자료이면 둘째자료의 가임기 갱신
                                    } else {
                                        Calendar fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart());
                                        Calendar fertileEnd = (Calendar) fertileStart.clone();
                                        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                        if (ovulation != null) {
                                            if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()),
                                                    Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) <= 20) {
                                                fertileStart = Utils.convertIntDateToCalendar(ovulation.getPeriodEnd());
                                                fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                            }
                                        }
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(),
                                                obtainedData.get(1).getPeriodStart(), obtainedData.get(1).getPeriodEnd(),
                                                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), obtainedData.get(1).getIsPredict()));
                                    }
                                }
                            }
                        }
                        else {
                            //첫자료가 예상자료인 경우
                            if (obtainedData.get(0).getIsPredict() == 1) {
                                //첫자료의 이전자료
                                Ovulation ovulation = cycleViewModel.getPrevOvulation(obtainedData.get(0).getPeriodStart());
                                //첫자료의 이전자료가 예상자료인 경우
                                if (ovulation.getIsPredict() == 1) {
                                    cycleViewModel.deletePredictOvulation();
                                    Calendar periodStart = (Calendar) selectedCalendar.clone();
                                    Calendar periodEnd = (Calendar) periodStart.clone();
                                    Calendar fertileStart = (Calendar) periodStart.clone();
                                    Calendar fertileEnd = (Calendar) periodStart.clone();
                                    periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 0));
                                    addPredictOvulation(periodStart);
                                } else {
                                    //첫자료의 이전자료의 생리마감날자와 선택된 날자와의 차이가 5일이상인 경우
                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) > 5) {
                                        cycleViewModel.deletePredictOvulation();
                                        Calendar periodStart = (Calendar) selectedCalendar.clone();
                                        Calendar periodEnd = (Calendar) periodStart.clone();
                                        Calendar fertileStart = (Calendar) periodStart.clone();
                                        Calendar fertileEnd = (Calendar) periodStart.clone();
                                        periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                        if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) > 10) {
                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                            if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) >= 20) {
                                                fertileStart = (Calendar) periodStart.clone();
                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                            } else {
                                                fertileStart = Utils.convertIntDateToCalendar(ovulation.getPeriodEnd());
                                                fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                            }
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                    Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 0));
                                        } else {
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                    -1, -1, 0));
                                        }
                                        addPredictOvulation(periodStart);
                                    } else {
                                        if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodStart()), selectedCalendar) > mCycleLength) {
                                            Toast.makeText(getContext(), R.string.period_length_limit_desc, Toast.LENGTH_SHORT).show();
                                            mPeriodToggle.setChecked(false);
                                        }
                                    }
                                }
                            }
                            else {
                                //선택된 날자가 첫자료의 생리마감날자보다 이전인 경우
                                if (selectedDate < obtainedData.get(0).getPeriodEnd()) {
                                    if (obtainedData.get(1).getIsPredict() == 1) {
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                selectedDate, obtainedData.get(0).getFertileStart(), obtainedData.get(0).getFertileEnd(), obtainedData.get(0).getIsPredict()));
                                        //첫자료의 생리시작날자와 생리마감날자와의 차이가 27일인 경우
                                        if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart()),
                                                Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd())) == mCycleLength - 1) {
                                            cycleViewModel.deletePredictOvulation();
                                            //새 예상 자료 생성
                                            Calendar periodStart = Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart());
                                            periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                            Calendar periodEnd = (Calendar) periodStart.clone();
                                            Calendar fertileStart = (Calendar) periodStart.clone();
                                            Calendar fertileEnd = (Calendar) periodStart.clone();
                                            periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                            if (Utils.getDiffDays(selectedCalendar, periodStart) > 10) {
                                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                if (Utils.getDiffDays(selectedCalendar, periodStart) >= 20) {
                                                    fertileStart = (Calendar) periodStart.clone();
                                                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                } else {
                                                    fertileStart = (Calendar) selectedCalendar.clone();
                                                    fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                }
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                        Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                            } else {
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                        -1, -1, 1));
                                            }
                                            periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                            periodEnd = (Calendar) periodStart.clone();
                                            periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                            fertileStart = (Calendar) periodStart.clone();
                                            fertileEnd = (Calendar) periodStart.clone();
                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                    Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                            periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                            periodEnd = (Calendar) periodStart.clone();
                                            periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                            fertileStart = (Calendar) periodStart.clone();
                                            fertileEnd = (Calendar) periodStart.clone();
                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                    Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                        } else {
                                            //첫 자료의 생리마감날자 갱신
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                    selectedDate, obtainedData.get(0).getFertileStart(), obtainedData.get(0).getFertileEnd(), obtainedData.get(0).getIsPredict()));
                                            //둘째 자료의 가임기시작날자와 마감날자 갱신
                                            Calendar fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart());
                                            Calendar fertileEnd = (Calendar) fertileStart.clone();
                                            if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) > 10) {
                                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) >= 20) {
                                                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                } else {
                                                    fertileStart = (Calendar) selectedCalendar.clone();
                                                    fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                }
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                        obtainedData.get(1).getPeriodEnd(), Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), obtainedData.get(1).getIsPredict()));
                                            } else {
                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                        obtainedData.get(1).getPeriodEnd(), -1, -1, obtainedData.get(1).getIsPredict()));
                                            }
                                        }
                                    } else {
                                        //첫자료의 생리마감날자 갱신
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                selectedDate, obtainedData.get(0).getFertileStart(), obtainedData.get(0).getFertileEnd(), obtainedData.get(0).getIsPredict()));
                                        //둘째 자료의 가임기시작날자와 마감날자 갱신
                                        Calendar fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart());
                                        Calendar fertileEnd = (Calendar) fertileStart.clone();
                                        if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) > 10) {
                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                            if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) >= 20) {
                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                            } else {
                                                fertileStart = (Calendar) selectedCalendar.clone();
                                                fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                            }
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                    obtainedData.get(1).getPeriodEnd(), Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), obtainedData.get(1).getIsPredict()));
                                        } else {
                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                    obtainedData.get(1).getPeriodEnd(), -1, -1, obtainedData.get(1).getIsPredict()));
                                        }
                                    }
                                } else if (selectedDate == obtainedData.get(0).getPeriodEnd()) {
                                    mPeriodToggle.setChecked(true);
                                } else {
                                    //선택된 날자와 첫자료의 생리마감날자와의 차이가 6일 이하인 경우
                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), selectedCalendar) <= 5) {
                                        if (obtainedData.size() > 1) {
                                            //둘째 자료가 예상자료인 경우
                                            if (obtainedData.get(1).getIsPredict() == 1) {
                                                //선택된 날자와 첫자료의 생리시작날자와의 차이가 27일 이하인 경우, 첫자료 갱신 및 둘째자료의 가임기 갱신
                                                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart()), selectedCalendar) < mCycleLength - 1) {
                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                            selectedDate, obtainedData.get(0).getFertileStart(), obtainedData.get(0).getFertileEnd(), obtainedData.get(0).getIsPredict()));
                                                    Calendar fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart());
                                                    Calendar fertileEnd = (Calendar) fertileStart.clone();
                                                    if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) > 10) {
                                                        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                        if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) >= 20) {
                                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                        } else {
                                                            fertileStart = (Calendar) selectedCalendar.clone();
                                                            fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                        }
                                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                                obtainedData.get(1).getPeriodEnd(), Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), obtainedData.get(1).getIsPredict()));
                                                    } else {
                                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                                obtainedData.get(1).getPeriodEnd(), -1, -1, obtainedData.get(1).getIsPredict()));
                                                    }
                                                    //선택된 날자와 첫자료의 생리시작날자와의 차이가 27일인 경우, 첫자료 갱신 및 예상자료 재생성
                                                } else if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart()), selectedCalendar) == mCycleLength - 1) {
                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                            selectedDate, obtainedData.get(0).getFertileStart(), obtainedData.get(0).getFertileEnd(), obtainedData.get(0).getIsPredict()));
                                                    cycleViewModel.deletePredictOvulation();
                                                    Calendar calendar = (Calendar) selectedCalendar.clone();
                                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                                    addPredictOvulation(calendar);
                                                } else {
                                                    Toast.makeText(getContext(), R.string.period_length_limit_desc, Toast.LENGTH_SHORT).show();
                                                    mPeriodToggle.setChecked(false);
                                                }
                                            } else {
                                                //둘째자료의 생리시작날자와 선택된 날자와의 차이가 6일 이하인 경우, 생리주기가 너무 짦다는 경고창 표시
                                                if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) <= 5) {
                                                    Toast.makeText(getContext(), R.string.period_cycle_short_desc, Toast.LENGTH_SHORT).show();
                                                    mPeriodToggle.setChecked(false);
                                                } else {
                                                    //첫자료의 생리시작날자와 선택된 날자와의 차이가 생리주기보다 긴 경우, 경고창 표시
                                                    //아닌 경우 첫자료의 생리마감날자 갱신 및 둘째자료의 가임기 갱신
                                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodStart()), selectedCalendar) >= mCycleLength) {
                                                        Toast.makeText(getContext(), R.string.period_length_limit_desc, Toast.LENGTH_SHORT).show();
                                                        mPeriodToggle.setChecked(false);
                                                    } else {
                                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(0).getId(), obtainedData.get(0).getPeriodStart(),
                                                                selectedDate, obtainedData.get(0).getFertileStart(), obtainedData.get(0).getFertileEnd(), obtainedData.get(0).getIsPredict()));
                                                        Calendar fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart());
                                                        Calendar fertileEnd = (Calendar) fertileStart.clone();
                                                        if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) > 10) {
                                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                            if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) >= 20) {
                                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                            } else {
                                                                fertileStart = (Calendar) selectedCalendar.clone();
                                                                fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                            }
                                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                                    obtainedData.get(1).getPeriodEnd(), Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), obtainedData.get(1).getIsPredict()));
                                                        } else {
                                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                                    obtainedData.get(1).getPeriodEnd(), -1, -1, obtainedData.get(1).getIsPredict()));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        if (obtainedData.size() > 1) {
                                            //둘째 자료가 예상 자료이면 새 자료 생성 및 예상자료 재 생성
                                            if (obtainedData.get(1).getIsPredict() == 1) {
                                                cycleViewModel.deletePredictOvulation();
                                                Calendar periodStart = (Calendar) selectedCalendar.clone();
                                                Calendar periodEnd = (Calendar) periodStart.clone();
                                                Calendar fertileStart = (Calendar) periodStart.clone();
                                                Calendar fertileEnd = (Calendar) periodStart.clone();
                                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), selectedCalendar) > 10) {
                                                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), selectedCalendar) >= 20) {
                                                        fertileStart = (Calendar) periodStart.clone();
                                                        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                    } else {
                                                        fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd());
                                                        fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                    }
                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 0));
                                                } else {
                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                            -1, -1, 0));
                                                }
                                                addPredictOvulation(periodStart);
                                            } else {
                                                //둘째 자료의 생리시작날자와 선택된 날자와의 차이가 10일 이하인 경우, 둘째자료 갱신
                                                if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) < 10) {
                                                    if (Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodEnd())) > mCycleLength - 1) {
                                                        Toast.makeText(getContext(), R.string.period_length_limit_desc, Toast.LENGTH_SHORT).show();
                                                        mPeriodToggle.setChecked(false);
                                                    } else {
                                                        Calendar periodStart = (Calendar) selectedCalendar.clone();
                                                        Calendar fertileStart = (Calendar) periodStart.clone();
                                                        Calendar fertileEnd = (Calendar) periodStart.clone();
                                                        if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), selectedCalendar) > 10) {
                                                            fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                            if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), selectedCalendar) >= 20) {
                                                                fertileStart = (Calendar) periodStart.clone();
                                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                            } else {
                                                                fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd());
                                                                fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                            }
                                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), Utils.getIntDate(periodStart), obtainedData.get(1).getPeriodEnd(),
                                                                    Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 0));
                                                        } else {
                                                            cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), Utils.getIntDate(periodStart), obtainedData.get(1).getPeriodEnd(),
                                                                    -1, -1, 0));
                                                        }
                                                        Ovulation nextOvulation = cycleViewModel.getNextOvulation(obtainedData.get(1).getPeriodEnd());
                                                        //첫자료를 얻은 후에 실지 자료가 존재하지 않으면 예상 자료 갱신
                                                        if (nextOvulation == null) {
                                                            cycleViewModel.deletePredictOvulation();
                                                            if (Utils.getDiffDays(periodStart, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodEnd())) == mCycleLength - 1) {
                                                                Calendar beforePeriodStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodEnd());
                                                                beforePeriodStart.add(Calendar.DAY_OF_MONTH, 1);
                                                                addPredictOvulation(beforePeriodStart);
                                                            } else {
                                                                periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                                                Calendar periodEnd = (Calendar) periodStart.clone();
                                                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                                fertileEnd = (Calendar) periodStart.clone();
                                                                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodEnd()), periodStart) > 10) {
                                                                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodEnd()), periodStart) >= 20) {
                                                                        fertileStart = (Calendar) periodStart.clone();
                                                                        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                                    } else {
                                                                        fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodEnd());
                                                                        fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                                    }
                                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                                                } else {
                                                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                                            -1, -1, 1));
                                                                }
                                                                periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                                                periodEnd = (Calendar) periodStart.clone();
                                                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                                fertileStart = (Calendar) periodStart.clone();
                                                                fertileEnd = (Calendar) periodStart.clone();
                                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                                        Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                                                periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                                                periodEnd = (Calendar) periodStart.clone();
                                                                periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                                fertileStart = (Calendar) periodStart.clone();
                                                                fertileEnd = (Calendar) periodStart.clone();
                                                                fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                                fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                                        Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    Calendar periodStart = (Calendar) selectedCalendar.clone();
                                                    Calendar periodEnd = (Calendar) periodStart.clone();
                                                    Calendar fertileStart = (Calendar) periodStart.clone();
                                                    Calendar fertileEnd = (Calendar) periodStart.clone();
                                                    periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                                    //새 자료 생성
                                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), selectedCalendar) > 10) {
                                                        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                        if (Utils.getDiffDays(Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd()), selectedCalendar) >= 20) {
                                                            fertileStart = (Calendar) periodStart.clone();
                                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                        } else {
                                                            fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(0).getPeriodEnd());
                                                            fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                        }
                                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 0));
                                                    } else {
                                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                                -1, -1, 0));
                                                    }
                                                    //둘째 자료의 가임기 갱신
                                                    fertileStart = Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart());
                                                    fertileEnd = (Calendar) fertileStart.clone();
                                                    if (Utils.getDiffDays(periodEnd, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) > 10) {
                                                        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                                        if (Utils.getDiffDays(periodEnd, Utils.convertIntDateToCalendar(obtainedData.get(1).getPeriodStart())) >= 20) {
                                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                                        } else {
                                                            fertileStart = (Calendar) periodEnd.clone();
                                                            fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                                        }
                                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                                obtainedData.get(1).getPeriodEnd(), Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), obtainedData.get(1).getIsPredict()));
                                                    } else {
                                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(obtainedData.get(1).getId(), obtainedData.get(1).getPeriodStart(),
                                                                obtainedData.get(1).getPeriodEnd(), -1, -1, obtainedData.get(1).getIsPredict()));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        //생리자료가 자료기지에 존재하지 않으면 새 자료 생성
                        Calendar periodStart = (Calendar) selectedCalendar.clone();
                        Calendar periodEnd = (Calendar) selectedCalendar.clone();
                        periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                        Calendar fertileStart = (Calendar) periodStart.clone();
                        Calendar fertileEnd = (Calendar) periodStart.clone();
                        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart),
                                Utils.getIntDate(periodEnd), Utils.getIntDate(fertileStart),
                                Utils.getIntDate(fertileEnd), 0));
                        addPredictOvulation(periodStart);
                    }
                    isDateSelected = true;
                }
            }
        });

        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 2);
        Calendar start = Calendar.getInstance();
        start.add(Calendar.YEAR, -10);

        updateMonthPeriod(Calendar.getInstance());

        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        monthYearFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        cycleViewModel.setDate(Integer.parseInt(dateFormat.format(new Date(selectedCalendar.getTimeInMillis())).replace("-", "")));
        cycleViewModel.ovulationLiveData.observe(getViewLifecycleOwner(), new Observer<List<Ovulation>>() {
            @Override
            public void onChanged(List<Ovulation> data) {
                cycleData = data;
                int selectedDate = Utils.getIntDate(selectedCalendar);
                //선택된 날자가 현재 날자보다 이후이면 주기설정부분을 숨기고 기다림상태를 보여주는 부분 현시
                //선택된 날자가 현재 날자보다 이전이면 주기설정부분을 현시하고 기다림상태를 보여주는 부분 숨기기
                if (Utils.getIntDate(Calendar.getInstance()) < selectedDate) {
                    mSetArea.setVisibility(View.GONE);
                    mWaitDescArea.setVisibility(View.VISIBLE);
                } else {
                    mSetArea.setVisibility(View.VISIBLE);
                    mWaitDescArea.setVisibility(View.GONE);

                    Calendar currentDate = Calendar.getInstance();
                    if (Utils.getDiffDays(selectedCalendar, currentDate) >= 180) {
                        mPeriodSetArea.setVisibility(View.GONE);
                        mConditionArea.setVisibility(View.GONE);
                        enableIntensity(false);
                    } else {
                        mPeriodSetArea.setVisibility(View.VISIBLE);
                        mConditionArea.setVisibility(View.VISIBLE);

                        //얻은 자료의 개수가 1개 이상인 경우
                        if (data.size() > 0) {
                            //선택된 날자가 첫자료의 생리시작날자보다 이전이면 표제를 시작으로 보여주고 intensity 부분 숨기기
                            if (selectedDate < data.get(0).getPeriodStart()) {
                                mPeriodTitle.setText(getResources().getString(R.string.period_started));
                                mPeriodToggle.setChecked(false);
                                enableIntensity(false);
                                //선택된 날자가 첫자료의 생리시작날자인 경우
                            } else if (selectedDate == data.get(0).getPeriodStart()) {
                                // 첫자료가 예상자료이면 선택된 날자와 첫자료의 이전자료의 생리시작날자와의 차이에 따라 intensity 부분 숨기기 및 표제 보여주기
                                //아니면 intensity 부분 보여주기 및 표제를 시작으로 보여주기
                                if (data.get(0).getIsPredict() == 1) {
                                    Ovulation ovulation = cycleViewModel.getPrevOvulation(selectedDate);
                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) > 5) {
                                        mPeriodTitle.setText(getResources().getString(R.string.period_started));
                                    } else {
                                        mPeriodTitle.setText(getResources().getString(R.string.period_ended));
                                    }
                                    mPeriodToggle.setChecked(false);
                                    enableIntensity(false);
                                } else {
                                    mPeriodTitle.setText(getResources().getString(R.string.period_started));
                                    mPeriodToggle.setChecked(true);
                                    enableIntensity(true);
                                }
                                //선택된 날자가 첫자료의 생리시작날자보다 이후인 경우
                            } else {
                                //첫자료가 예상자료이면 첫자료의 이전자료의 생리마감날자와 선택된 날자의 차이에 따라 intensity 부분 숨기기 및 표제 보여주기
                                if (data.get(0).getIsPredict() == 1) {
                                    Ovulation ovulation = cycleViewModel.getPrevOvulation(data.get(0).getPeriodStart());
                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), selectedCalendar) > 5) {
                                        mPeriodTitle.setText(getResources().getString(R.string.period_started));
                                    } else {
                                        mPeriodTitle.setText(getResources().getString(R.string.period_ended));
                                    }
                                    mPeriodToggle.setChecked(false);
                                    enableIntensity(false);
                                    //첫자료가 예상 자료가 아닌 경우
                                } else {
                                    //선택된 날자가 첫자료의 생리마감날자보다 이전이면 intensity 부분 현시 및 표제를 마감으로 현시
                                    if (selectedDate < data.get(0).getPeriodEnd()) {
                                        mPeriodTitle.setText(getResources().getString(R.string.period_ended));
                                        mPeriodToggle.setChecked(false);
                                        enableIntensity(true);
                                        //선택된 날자가 첫자료의 생리마감날자이면 intensity 부분 숨기기 및 표제를 마감으로 현시
                                    } else if (selectedDate == data.get(0).getPeriodEnd()) {
                                        mPeriodTitle.setText(getResources().getString(R.string.period_ended));
                                        mPeriodToggle.setChecked(true);
                                        enableIntensity(true);
                                        //선택된 날자가 첫자료의 생리마감날자보다 이후이면 선택된 날자와 첫자료의 생리마감날자와의 차이에 따라 intensity부분 숨기기 및 표제 현시
                                    } else {
                                        if (Math.abs(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodEnd()), selectedCalendar)) <= 5) {
                                            mPeriodTitle.setText(getResources().getString(R.string.period_ended));
                                        } else {
                                            mPeriodTitle.setText(getResources().getString(R.string.period_started));
                                        }
                                        mPeriodToggle.setChecked(false);
                                        enableIntensity(false);
                                    }
                                }
                            }
                        } else {
                            mPeriodTitle.setText(getResources().getString(R.string.period_started));
                            mPeriodToggle.setChecked(false);
                            enableIntensity(false);
                        }
                    }
                }
                isDateSelected = false;
                mPeriodToggle.setClickable(true);
            }
        });

        //선택된 날자의 생리상태 현시
        cycleViewModel.ovulationDataForStatus.observe(getViewLifecycleOwner(), new Observer<List<Ovulation>>() {
            @Override
            public void onChanged(List<Ovulation> data) {
                Calendar currentDate = Calendar.getInstance();
                if (Utils.getDiffDays(selectedCalendar, currentDate) >= 180) {
                    mDayStatus.setText(getResources().getString(R.string.impossible_modify_cycle_desc));
                    mTotalDay.setText("");
                } else {
                    if (data.size() > 0) {
                        int selectedDate = Utils.getIntDate(selectedCalendar);
                        if (data.size() > 1) {
                            if (data.get(1).getPeriodStart() == selectedDate) {
                                mDayStatus.setText(getResources().getString(data.get(1).getIsPredict() == 1 ?
                                        R.string.predicted_period_day_status : R.string.period_day_status, 1));
                            } else {
                                if (data.get(0).getPeriodEnd() >= selectedDate) {
                                    mDayStatus.setText(getResources().getString(data.get(0).getIsPredict() == 1 ?
                                                    R.string.predicted_period_day_status : R.string.period_day_status,
                                            Math.abs(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodStart()), selectedCalendar)) + 1));
                                } else {
                                    showCloserStatus(selectedDate, data.get(1));
                                }
                            }
                        } else {
                            if (selectedDate >= data.get(0).getPeriodStart()) {
                                if (selectedDate <= data.get(0).getPeriodEnd()) {
                                    mDayStatus.setText(getResources().getString(data.get(0).getIsPredict() == 1 ?
                                                    R.string.predicted_period_day_status : R.string.period_day_status,
                                            Math.abs(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodStart()), selectedCalendar)) + 1));
                                } else {
                                    mDayStatus.setText(getResources().getString(R.string.status_empty_desc));
                                }
                            } else {
                                showCloserStatus(selectedDate, data.get(0));
                            }
                        }
                    } else {
                        mDayStatus.setText(getResources().getString(R.string.status_empty_desc));
                    }
                }
            }
        });

        //총날자 현시
        cycleViewModel.ovulationDataForTotal.observe(getViewLifecycleOwner(), new Observer<List<Ovulation>>() {
            @Override
            public void onChanged(List<Ovulation> data) {
                if (data.size() > 0) {
                    int selectedDate = Utils.getIntDate(selectedCalendar);
                    if (data.get(0).getPeriodStart() > selectedDate) {
                        mTotalDay.setText("");
                    } else {
                        if (data.size() > 1) {
                            int totalDays = 0;
                            if (data.get(1).getPeriodStart() == selectedDate) {
                                if (data.size() > 2) {
                                    totalDays = Math.abs(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(1).getPeriodStart()),
                                            Utils.convertIntDateToCalendar(data.get(2).getPeriodStart())));
                                    mTotalDay.setText(getResources().getString(R.string.total_days, totalDays));
                                } else {
                                    mTotalDay.setText("");
                                }
                            } else {
                                totalDays = Math.abs(Utils.getDiffDays(Utils.convertIntDateToCalendar(data.get(0).getPeriodStart()),
                                        Utils.convertIntDateToCalendar(data.get(1).getPeriodStart())));
                                mTotalDay.setText(getResources().getString(R.string.total_days, totalDays));
                            }
                        } else {
                            mTotalDay.setText("");
                        }
                    }
                } else {
                    mTotalDay.setText("");
                }
            }
        });

        //현재 선택된 월에 기초하여 3개월 자료를 얻고 생리기간, 가임기간, 예상부분으로 분할
        cycleViewModel.ovulationMonthData.observe(getViewLifecycleOwner(), new Observer<List<Ovulation>>() {
            @Override
            public void onChanged(List<Ovulation> data) {
                List<LinkedHashMap<String, Calendar>> periodList = new ArrayList<>();
                List<LinkedHashMap<String, Calendar>> fertileList = new ArrayList<>();
                List<LinkedHashMap<String, Calendar>> predictList = new ArrayList<>();
                for (int i = 0; i< data.size(); i ++) {
                    Calendar startPeriod = Utils.convertIntDateToCalendar(data.get(i).getPeriodStart());
                    Calendar endPeriod = Utils.convertIntDateToCalendar(data.get(i).getPeriodEnd());
                    LinkedHashMap<String, Calendar> period = new LinkedHashMap<>();
                    int diffDays = Utils.getDiffDays(startPeriod, endPeriod);
                    for (int j = 0; j < diffDays + 1; j ++) {
                        period.put(dateFormat.format(new Date(startPeriod.getTimeInMillis())), startPeriod);
                        startPeriod.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    if (period.size() > 0) {
                        if (data.get(i).getIsPredict() == 0)
                            periodList.add(period);
                        else predictList.add(period);
                    }

                    if (data.get(i).getFertileStart() > 0) {
                        Calendar startFertile = Utils.convertIntDateToCalendar(data.get(i).getFertileStart());
                        Calendar endFertile = Utils.convertIntDateToCalendar(data.get(i).getFertileEnd());
                        LinkedHashMap<String, Calendar> fertilePeriod = new LinkedHashMap<>();
                        diffDays = Utils.getDiffDays(startFertile, endFertile);
                        for (int j = 0; j < diffDays + 1; j ++) {
                            fertilePeriod.put(dateFormat.format(new Date(startFertile.getTimeInMillis())), startFertile);
                            startFertile.add(Calendar.DAY_OF_MONTH, 1);
                        }
                        if (fertilePeriod.size() > 0) {
                            fertileList.add(fertilePeriod);
                        }
                    }
                }

                mCalendarView.updateRangeList(periodList, fertileList, predictList);
            }
        });

        symptomViewModel.setDate(Utils.getIntDate(selectedCalendar));
        //현재 선택된 월에 따라 신체적증상 얻기 및 그 자료를 생리달력에 표시
        symptomViewModel.monthData.observe(getViewLifecycleOwner(), new Observer<List<Symptom>>() {
            @Override
            public void onChanged(List<Symptom> symptoms) {
                Map<String, com.haibin.calendarview.Calendar> events= new HashMap<>();
                for (int i = 0; i < symptoms.size(); i ++) {
                    Calendar calendar = Utils.convertIntDateToCalendar(symptoms.get(i).getDate());
                    com.haibin.calendarview.Calendar customCalendar = new com.haibin.calendarview.Calendar();
                    customCalendar.setYear(calendar.get(Calendar.YEAR));
                    customCalendar.setMonth(calendar.get(Calendar.MONTH) + 1);
                    customCalendar.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                    customCalendar.setSchemeColor(0xFF40db25);
                    customCalendar.setScheme("");
                    events.put(customCalendar.toString(), customCalendar);
                }
                mCalendarView.setSchemeDate(events);
            }
        });
        //선택된 날자의 생리적증상자료 얻고 자료가 있으면 신체적증상상태를 intensity 부분에 현시
        symptomViewModel.dayData.observe(getViewLifecycleOwner(), new Observer<Symptom>() {
            @Override
            public void onChanged(Symptom symptom) {
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
            }
        });

        cycleLengthViewModel.cycleLengthLiveData.observe(getViewLifecycleOwner(), new Observer<CycleLength>() {
            @Override
            public void onChanged(CycleLength cycleLength) {
                if (cycleLength != null) {
                    //생리기간이나 주기기간이 변경되면 예상자료 갱신
                    if (mPeriodLength > 0 && (mPeriodLength != cycleLength.getPeriodLength() || mCycleLength != cycleLength.getCycleLength())) {
                        mPeriodLength = cycleLength.getPeriodLength();
                        mCycleLength = cycleLength.getCycleLength();
                        Ovulation ovulation = cycleViewModel.getLastOvulation();
                        if (ovulation != null) {
                            //마지막생리자료의 생리시작날자와 마감날자와의 차이가 갱신된 생리주기기간보다 길면 마지막 생리자료의 생리마감날자 변경 및 예상자료갱신
                            if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodStart()),
                                    Utils.convertIntDateToCalendar(ovulation.getPeriodEnd())) >= mCycleLength) {
                                Calendar periodEnd = Utils.convertIntDateToCalendar(ovulation.getPeriodStart());
                                periodEnd.add(Calendar.DAY_OF_MONTH, mCycleLength - 1);
                                cycleViewModel.insertOrUpdateOvulation(new Ovulation(ovulation.getId(), ovulation.getPeriodStart(), Utils.getIntDate(periodEnd),
                                        ovulation.getFertileStart(), ovulation.getFertileEnd(), ovulation.getIsPredict()));
                                cycleViewModel.deletePredictOvulation();
                                Calendar calendar = (Calendar) periodEnd.clone();
                                calendar.add(Calendar.DAY_OF_MONTH, 1);
                                addPredictOvulation(calendar);
                            } else {
                                //마지막 자료의 생리기간이 갱신된 생리주기기간과 같으면 예상자료 갱신
                                if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodStart()),
                                        Utils.convertIntDateToCalendar(ovulation.getPeriodEnd())) == mCycleLength - 1) {
                                    cycleViewModel.deletePredictOvulation();
                                    Calendar calendar = Utils.convertIntDateToCalendar(ovulation.getPeriodEnd());
                                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                                    addPredictOvulation(calendar);
                                } else {
                                    cycleViewModel.deletePredictOvulation();
                                    Calendar periodStart = Utils.convertIntDateToCalendar(ovulation.getPeriodStart());
                                    periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                    Calendar periodEnd = (Calendar) periodStart.clone();
                                    periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
                                    Calendar fertileStart = (Calendar) periodStart.clone();
                                    Calendar fertileEnd = (Calendar) periodStart.clone();
                                    if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), periodStart) > 10) {
                                        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                        if (Utils.getDiffDays(Utils.convertIntDateToCalendar(ovulation.getPeriodEnd()), periodStart) >= 20) {
                                            fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                        } else {
                                            fertileStart = Utils.convertIntDateToCalendar(ovulation.getPeriodEnd());
                                            fertileStart.add(Calendar.DAY_OF_MONTH, 1);
                                        }
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                    } else {
                                        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                                -1, -1, 1));
                                    }
                                    periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                    periodEnd.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                    fertileStart = (Calendar) periodStart.clone();
                                    fertileEnd = (Calendar) periodStart.clone();
                                    fertileStart.add(Calendar.DAY_OF_MONTH, -19);
                                    fertileEnd.add(Calendar.DAY_OF_MONTH, -10);
                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                    periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                    periodEnd.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                    fertileStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                    fertileEnd.add(Calendar.DAY_OF_MONTH, mCycleLength);
                                    cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                                            Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
                                }
                            }
                        }
                    } else {
                        mPeriodLength = cycleLength.getPeriodLength();
                        mCycleLength = cycleLength.getCycleLength();
                    }
                } else {
                    mPeriodLength = 5;
                    mCycleLength = 28;
                }
            }
        });

        Calendar currentMonth = Calendar.getInstance();
        currentMonth.set(Calendar.YEAR, mCalendarView.getCurYear());
        currentMonth.set(Calendar.MONTH, mCalendarView.getCurMonth() - 1);
        mCalendarTitle.setText(monthYearFormat.format(new Date(currentMonth.getTimeInMillis())));

        return view;
    }

    //예상자료 생성
    private void addPredictOvulation(Calendar beforePeriodStart) {
        Calendar periodStart = (Calendar) beforePeriodStart.clone();
        periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
        Calendar periodEnd = (Calendar) periodStart.clone();
        Calendar fertileStart = (Calendar) periodStart.clone();
        Calendar fertileEnd = (Calendar) periodStart.clone();
        periodEnd.add(Calendar.DAY_OF_MONTH, mPeriodLength - 1);
        fertileStart.add(Calendar.DAY_OF_MONTH, -19);
        fertileEnd.add(Calendar.DAY_OF_MONTH, -10);

        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
        fertileStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
        fertileEnd.add(Calendar.DAY_OF_MONTH, mCycleLength);
        periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
        periodEnd.add(Calendar.DAY_OF_MONTH, mCycleLength);
        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
        fertileStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
        fertileEnd.add(Calendar.DAY_OF_MONTH, mCycleLength);
        periodStart.add(Calendar.DAY_OF_MONTH, mCycleLength);
        periodEnd.add(Calendar.DAY_OF_MONTH, mCycleLength);
        cycleViewModel.insertOrUpdateOvulation(new Ovulation(0, Utils.getIntDate(periodStart), Utils.getIntDate(periodEnd),
                Utils.getIntDate(fertileStart), Utils.getIntDate(fertileEnd), 1));
    }

    /**
     * 상태표시함수
     * @param selectedDate 선택된 날자
     * @param nextCycle 다음 생리자료
     */
    public void showCloserStatus(int selectedDate, Ovulation nextCycle) {
        int periodDiffDays = Math.abs(Utils.getDiffDays(selectedCalendar,
                Utils.convertIntDateToCalendar(nextCycle.getPeriodStart())));
        if (nextCycle.getFertileStart() > 0) {
            int diffDays = Math.abs(Utils.getDiffDays(selectedCalendar, Utils.convertIntDateToCalendar(nextCycle.getFertileStart())));
            if (nextCycle.getFertileStart() <= selectedDate && nextCycle.getFertileEnd() >= selectedDate) {
                mDayStatus.setText(getResources().getString(R.string.fertile_day_status,
                        diffDays + 1));
            } else if (nextCycle.getFertileStart() > selectedDate) {
                if (diffDays == 1) {
                    mDayStatus.setText(getResources().getString(R.string.fertile_closer_status_one));
                } else {
                    mDayStatus.setText(getResources().getString(R.string.fertile_closer_status, diffDays));
                }
            } else {
                if (periodDiffDays == 1) {
                    mDayStatus.setText(nextCycle.getIsPredict() == 1 ? getResources().getString(R.string.predicted_period_closer_status_one)
                            : getResources().getString(R.string.period_closer_status_one));
                } else {
                    mDayStatus.setText(nextCycle.getIsPredict() == 1 ? getResources().getString(R.string.predicted_period_closer_status, periodDiffDays)
                            : getResources().getString(R.string.period_closer_status, periodDiffDays));
                }
            }
        } else {
            if (periodDiffDays == 1) {
                mDayStatus.setText(nextCycle.getIsPredict() == 1 ? getResources().getString(R.string.predicted_period_closer_status_one)
                        : getResources().getString(R.string.period_closer_status_one));
            } else {
                mDayStatus.setText(nextCycle.getIsPredict() == 1 ? getResources().getString(R.string.predicted_period_closer_status, periodDiffDays)
                        : getResources().getString(R.string.period_closer_status, periodDiffDays));
            }
        }
    }

    /**
     * intensity부분 현시여부 설정함수
     * @param isEnabled true이면 현시, false이면 숨기기
     */
    public void enableIntensity(boolean isEnabled) {
        isIntensityEnabled = isEnabled;
        mFlowArea.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        mPainArea.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
    }

    /**
     * 월기간 갱신하는 함수
     * @param calendar 갱신될 월기간에 속한 날자
     */
    public void updateMonthPeriod(Calendar calendar) {
        List<Integer> month = new ArrayList<>();
        Calendar start = (Calendar) calendar.clone();
        start.add(Calendar.MONTH, -1);
        start.set(Calendar.DAY_OF_MONTH, 1);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 2);
        end.add(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - 1);
        month.add(Utils.getIntDate(start));
        month.add(Utils.getIntDate(end));
        cycleViewModel.setMonth(month);
        symptomViewModel.setMonth(month);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.flow_area:
            case R.id.pain_area:
            case R.id.condition_area:
                Intent intent = new Intent(getContext(), SymptomsActivity.class);
                intent.putExtra("enabledIntensity", isIntensityEnabled);
                intent.putExtra("selectedDate", Utils.getIntDate(selectedCalendar));
                startActivity(intent);
                break;
            case R.id.stats:
                Intent statsIntent = new Intent(getContext(), CycleStatsActivity.class);
                startActivity(statsIntent);
                break;
            case R.id.current_calendar:
                mCalendarView.scrollToCurrent();
                break;
        }
    }

    @Override
    public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {

    }

    /**
     * 달력에서 날자를 선택하였을때 호출되는 callback
     * @param calendar calendar 선택된 날자
     * @param b
     */
    @Override
    public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean b) {
        Calendar today = Calendar.getInstance();
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(calendar.getTimeInMillis());
        if (Utils.getIntDate(selectedDate) != Utils.getIntDate(today)) {
            mCurrentCalendar.setVisibility(View.VISIBLE);
            mCurrentDate.setText(String.valueOf(today.get(Calendar.DAY_OF_MONTH)));
        } else {
            mCurrentCalendar.setVisibility(View.GONE);
        }
        isDateSelected = true;
        selectedCalendar = selectedDate;
        selectedCalendar.set(Calendar.HOUR_OF_DAY, 0);
        selectedCalendar.set(Calendar.MINUTE, 0);
        selectedCalendar.set(Calendar.SECOND, 0);
        selectedCalendar.set(Calendar.MILLISECOND, 0);
        cycleViewModel.setDate(Utils.getIntDate(selectedCalendar));
        symptomViewModel.setDate(Utils.getIntDate(selectedCalendar));
    }


    /**
     * 달력에서 월전환을 진행하였을때 호출되는 callback
     * @param year 년
     * @param month 월
     */
    @Override
    public void onMonthChange(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        updateMonthPeriod(calendar);
        mCalendarTitle.setText(monthYearFormat.format(new Date(calendar.getTimeInMillis())));
    }
}