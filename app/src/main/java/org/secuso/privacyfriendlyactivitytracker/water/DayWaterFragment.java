package org.secuso.privacyfriendlyactivitytracker.water;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;

import org.joda.time.DateTime;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.models.WaterInfo;
import org.secuso.privacyfriendlyactivitytracker.utils.CustomDatePickerDialog;
import org.secuso.privacyfriendlyactivitytracker.utils.WaveView;
import org.secuso.privacyfriendlyactivitytracker.viewModel.WaterViewModel;

import java.util.Calendar;
import java.util.Objects;

/**
 * 일별물자료현시화면
 */
public class DayWaterFragment extends Fragment implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    ImageView mPrev;
    ImageView mNext;
    TextView mDate;
    WaveView mWaterChange;
    ImageView mWaterDecrease;
    ImageView mWaterIncrease;
    TextView mGlasses;
    TextView mTotalAmount;
    RoundedHorizontalProgressBar mWaterProgress;
    TextView mSetTarget;
    TextView mTarget;
    TextView mSetTargetDesc;
    LinearLayout mProgressArea;

    WaterViewModel viewModel;

    DateTime date;
    int year, month, day;
    int glasses; // 고뿌수
    int target = 0; // 하루 목표량
    int id = 0;
    boolean isDateChanged = true; // 날자를 변경시켰는지 판별
    boolean isIncreased; // 증가하였는지 판별

    WaterFragment mWaterFragment;

    public DayWaterFragment(WaterFragment waterFragment) {
        // Required empty public constructor
        mWaterFragment = waterFragment;
    }

    public static DayWaterFragment newInstance(WaterFragment waterFragment) {
        DayWaterFragment fragment = new DayWaterFragment(waterFragment);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        year = Calendar.getInstance().get(Calendar.YEAR);
        month = Calendar.getInstance().get(Calendar.MONTH);
        day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        date = new DateTime(year, month + 1, day, 0, 0, 0);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPref.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_water, container, false);

        mPrev = view.findViewById(R.id.prev);
        mPrev.setOnClickListener(this);
        mNext = view.findViewById(R.id.next);
        mNext.setOnClickListener(this);
        mDate = view.findViewById(R.id.date);
        mDate.setOnClickListener(this);
        mDate.setText(Utils.getDateString(date, Objects.requireNonNull(getContext())));
        mWaterChange = view.findViewById(R.id.water_change);
        mWaterDecrease = view.findViewById(R.id.water_decrease);
        mWaterDecrease.setOnClickListener(this);
        mWaterIncrease = view.findViewById(R.id.water_increase);
        mWaterIncrease.setOnClickListener(this);
        mGlasses = view.findViewById(R.id.glasses);
        mTotalAmount = view.findViewById(R.id.total_amount);
        mWaterProgress = view.findViewById(R.id.water_progress);
        mSetTarget = view.findViewById(R.id.set_target);
        mSetTarget.setOnClickListener(this);
        mTarget = view.findViewById(R.id.target);
        mProgressArea = view.findViewById(R.id.progress_area);
        mSetTargetDesc = view.findViewById(R.id.set_target_desc);

        viewModel = new ViewModelProvider(requireActivity()).get(WaterViewModel.class);
        viewModel.instanceForDay();
        viewModel.setDate(date);
        viewModel.dayData.observe(getViewLifecycleOwner(), water -> {
            if (water != null) {
                id = water.getId();
                glasses = Math.max(water.getGlasses(), 0);
            } else {
                id = 0;
                glasses = 0;
            }
            mWaterChange.setProgress(glasses > 0 ? 800 : 0);
            mGlasses.setText(String.valueOf(glasses));
            mTotalAmount.setText(getResources().getString(R.string.water_format, glasses * 250));
            decreaseButtonClickable(glasses > 0);
            mWaterProgress.animateProgress(500, isDateChanged ? 0 : isIncreased ? (glasses - 1) * 250 : (glasses + 1) * 250, glasses * 250);

            mWaterFragment.cancelLoading();
        });

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
        target = sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0);
        targetEnabled(target > 0);
        mWaterProgress.setMax(target * 250);

        mNext.setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.date) {
            CustomDatePickerDialog datePickerDialog = new CustomDatePickerDialog(year, month, day, getContext());
            datePickerDialog.setOnDateSelectedListener((_year, _monthOfYear, _dayOfMonth) -> {
                date = new DateTime(_year, _monthOfYear + 1, _dayOfMonth, 0, 0, 0);
                Calendar currentDay = Calendar.getInstance();
                if (date.getMillis() > currentDay.getTimeInMillis()) {
                    year = currentDay.get(Calendar.YEAR);
                    month = currentDay.get(Calendar.MONTH);
                    day = currentDay.get(Calendar.DAY_OF_MONTH);
                    date = new DateTime(year, month + 1, day, 0, 0, 0);
                    Toast.makeText(getContext(), getString(R.string.error_msg), Toast.LENGTH_SHORT).show();
                } else {
                    year = _year;
                    month = _monthOfYear;
                    day = _dayOfMonth;
                }
                mDate.setText(Utils.getDateString(date, Objects.requireNonNull(getContext())));

                updateGlasses(true, true);

                if (currentDay.get(Calendar.YEAR) == year && currentDay.get(Calendar.MONTH) == month
                        && currentDay.get(Calendar.DAY_OF_MONTH) == day) {
                    mNext.setVisibility(View.GONE);
                } else {
                    mNext.setVisibility(View.VISIBLE);
                }
            });
            datePickerDialog.show();
        } else if (view.getId() == R.id.next) {
            updateDate(1);
            updateGlasses(true, true);

            Calendar currentDay = Calendar.getInstance();
            if (currentDay.get(Calendar.YEAR) == year && currentDay.get(Calendar.MONTH) == month
                    && currentDay.get(Calendar.DAY_OF_MONTH) == day) {
                mNext.setVisibility(View.GONE);
            }
        } else if (view.getId() == R.id.prev) {
            mNext.setVisibility(View.VISIBLE);
            updateDate(-1);
            updateGlasses(true, true);
        } else if (view.getId() == R.id.water_decrease) {
            glasses--;
            updateGlasses(false, false);

            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator animPlay = ObjectAnimator.ofInt(mWaterChange, "progress", 800, 0);
            animPlay.setDuration(500);
            animPlay.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (glasses > 0)
                        mWaterChange.setProgress(800);
                }
            });
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.playTogether(animPlay);
            animatorSet.start();
        } else if (view.getId() == R.id.water_increase) {
            AnimatorSet animatorSet = new AnimatorSet();
            ObjectAnimator animPlay = ObjectAnimator.ofInt(mWaterChange, "progress", 0, 800);
            animPlay.setDuration(500);
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.playTogether(animPlay);
            animatorSet.start();

            glasses++;
            updateGlasses(false, true);
        } else if (view.getId() == R.id.set_target) {
            Intent intent = new Intent(getContext(), SetTargetActivity.class);
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
     * 물감소단추의 click 가능상태를 변경하는 함수
     * @param clickable true이면 click 가능, 아니면 click 불가능
     */
    private void decreaseButtonClickable(boolean clickable) {
        mWaterDecrease.setClickable(clickable);
        mWaterDecrease.setAlpha(clickable ? 1.0f : 0.5f);
    }

    /**
     * 날자상태를 변경하는 함수
     * @param count 증가 또는 감소하려는 날자수 (1혹은 -1)
     */
    private void updateDate(int count) {
        Calendar nextDay = Calendar.getInstance();
        nextDay.set(Calendar.YEAR, date.getYear());
        nextDay.set(Calendar.MONTH, date.getMonthOfYear() - 1);
        nextDay.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        nextDay.set(Calendar.HOUR_OF_DAY, 0);
        nextDay.set(Calendar.MINUTE, 0);
        nextDay.set(Calendar.SECOND, 0);
        nextDay.add(Calendar.DAY_OF_MONTH, count);
        year = nextDay.get(Calendar.YEAR);
        month = nextDay.get(Calendar.MONTH);
        day = nextDay.get(Calendar.DAY_OF_MONTH);
        date = new DateTime(year, month + 1, day, 0, 0, 0);
        mDate.setText(Utils.getDateString(date, Objects.requireNonNull(getContext())));
    }

    /**
     * 자료갱신하는 함수
     * @param isDateChanged 날자변경상태
     * @param isIncreased 증가 및 감소상태
     */
    private void updateGlasses(boolean isDateChanged, boolean isIncreased) {
        this.isDateChanged = isDateChanged;
        this.isIncreased = isIncreased;
        if (isDateChanged) {
            viewModel.setDate(date);
        } else {
            WaterInfo info = new WaterInfo();
            if (id > 0)
                info.setId(id);
            info.setGlasses(Math.max(glasses, 0));
            info.setMeasureDateTime(date);
            viewModel.insertOrUpdate(info);
        }
    }

    /**
     * 물목표에 대한 UI를 보여주기 위한 상태를 변경하기 위한 함수
     * @param enabled true이면 물목표 UI상태 현시, false 이면 현시 안함
     */
    private void targetEnabled(boolean enabled) {
        mTarget.setVisibility(enabled ? View.VISIBLE : View.GONE);
        mSetTargetDesc.setVisibility(enabled ? View.GONE : View.VISIBLE);
        mProgressArea.setVisibility(enabled ? View.VISIBLE : View.GONE);
        if (enabled)
            mTarget.setText(getResources().getString(R.string.target_format, target));
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPref, String key) {
        if (key.equals(getString(R.string.pref_daily_water_goal))) {
            target = sharedPref.getInt(getString(R.string.pref_daily_water_goal), 0);
            targetEnabled(target > 0);
            mWaterProgress.setMax(target * 250);
            mWaterProgress.animateProgress(500, 0, glasses * 250);
        }
    }
}