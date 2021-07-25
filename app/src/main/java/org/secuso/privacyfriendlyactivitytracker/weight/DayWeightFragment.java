package org.secuso.privacyfriendlyactivitytracker.weight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.WeightInfo;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WeightViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 일별 몸무게자료 현시화면
 */
public class DayWeightFragment extends Fragment implements DailyMeasureLayout.OnViewClickListener,
        View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    DailyMeasureLayout dailyMeasureLayout;
    RecyclerView mDailyHistory;
    LinearLayout mHistoryTitle;
    DailyWeightMeasureHistoryAdapter mDailyWeightMeasureHistoryAdapter;
    LinearLayout mainContent;
    RelativeLayout emptyContent;
    LinearLayout mHistoryMore;

    WeightViewModel viewModel;

    int currentIndex = 0; // 자료가 존재하는 날자목록에서 현재 선택한 날자에 해당하는 위치
    int maxIndex; // 자료가 존재하는 날자목록의 개수
    List<WeightInfo> dayWeightInfoList = new ArrayList<>();
    List<WeightInfo> weightDayList = new ArrayList<>();

    WeightFragment mWeightFragment;

    public DayWeightFragment(WeightFragment weightFragment) {
        // Required empty public constructor
        mWeightFragment = weightFragment;
    }

    /**
     * DayWeightFragment instance를 생성하는 함수
     * @param weightFragment weightFragment instance
     * @return
     */
    public static DayWeightFragment newInstance(WeightFragment weightFragment) {
        DayWeightFragment fragment = new DayWeightFragment(weightFragment);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(WeightViewModel.class);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_day_weight, container, false);

        dailyMeasureLayout = view.findViewById(R.id.daily_measure_show);
        dailyMeasureLayout.setOnViewClickListener(this);
        mHistoryTitle = view.findViewById(R.id.history_title);
        mDailyHistory = view.findViewById(R.id.daily_history);
        mHistoryMore = view.findViewById(R.id.history_more);
        mHistoryMore.setOnClickListener(this);
        mainContent = view.findViewById(R.id.main_content);
        emptyContent = view.findViewById(R.id.empty_content);

        viewModel.instanceForDay();
        viewModel.allDayData.observe(getViewLifecycleOwner(), weights -> {
            weightDayList = viewModel.convertWeightType(weights);
            if (weightDayList.size() > 0) {
                showMainContent(true);
                maxIndex = weightDayList.size() - 1;
                dailyMeasureLayout.setValues(weightDayList.get(currentIndex), currentIndex, maxIndex);
            } else {
                showMainContent(false);
            }
            mWeightFragment.cancelLoading();
        });

        viewModel.dayData.observe(getViewLifecycleOwner(), weights -> {
            dayWeightInfoList = viewModel.convertWeightType(weights);
            mDailyWeightMeasureHistoryAdapter.updateData(dayWeightInfoList);
            canShowHistoryTitle();
            if (weightDayList.size() > 0)
                dailyMeasureLayout.setValues(weightDayList.get(currentIndex), currentIndex, maxIndex);
        });

        updateGoalShow();

        mDailyWeightMeasureHistoryAdapter = new DailyWeightMeasureHistoryAdapter(getContext(), dayWeightInfoList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mDailyHistory.setLayoutManager(layoutManager);
        mDailyHistory.setAdapter(mDailyWeightMeasureHistoryAdapter);

        return view;
    }

    /**
     * 일별 기록자료의 title을 현시할수 있는지 확인하는 함수
     */
    public void canShowHistoryTitle() {
        if (dayWeightInfoList.size() > 0) mHistoryTitle.setVisibility(View.VISIBLE);
        else mHistoryTitle.setVisibility(View.GONE);
    }

    /**
     * 자료가 없는 경우 자료없음 UI현시, 아니면 자료 현시
     * @param canShow 자료현시여부
     */
    private void showMainContent(boolean canShow) {
        mainContent.setVisibility(canShow ? View.VISIBLE : View.GONE);
        emptyContent.setVisibility(canShow ? View.GONE : View.VISIBLE);
    }

    /**
     * 이전을 눌렀을때 호출되는 callback
     */
    @Override
    public void onPrevClicked() {
        currentIndex = currentIndex + 1;
        canShowHistoryTitle();
        viewModel.setDayCurrentIndex(currentIndex, weightDayList.get(currentIndex).getDate());
    }

    /**
     * 다음을 눌렀을때 호출되는 callback
     */
    @Override
    public void onNextClicked() {
        currentIndex = currentIndex - 1;
        canShowHistoryTitle();
        viewModel.setDayCurrentIndex(currentIndex, weightDayList.get(currentIndex).getDate());
    }

    @Override
    public void onDestroy() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view == mHistoryMore) {
            Intent intent = new Intent(getContext(), HistoryActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_weight_goal)) || key.equals(getString(R.string.pref_weight_start))) {
            updateGoalShow();
        }
    }

    /**
     * 몸무게목표상태표시를 갱신하는 함수
     */
    private void updateGoalShow() {
        SharedPreferences sharePref = PreferenceManager.getDefaultSharedPreferences(getContext());
        float weightStart, weightGoal;
        weightStart = sharePref.getFloat(getString(R.string.pref_weight_start), 0);
        weightGoal = sharePref.getFloat(getString(R.string.pref_weight_goal), 0);
        if (weightStart != 0 && weightGoal != 0) {
            dailyMeasureLayout.setGoalProgress(String.valueOf(weightStart), String.valueOf(weightGoal));
        }
    }
}