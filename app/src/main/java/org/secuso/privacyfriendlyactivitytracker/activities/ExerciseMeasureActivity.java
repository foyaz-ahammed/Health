package org.secuso.privacyfriendlyactivitytracker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jem.easyreveal.clippathproviders.CircularClipPathProvider;
import com.jem.easyreveal.layouts.EasyRevealFrameLayout;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.exercise.WorkoutShowActivity;
import org.secuso.privacyfriendlyactivitytracker.models.Stopwatch;
import org.secuso.privacyfriendlyactivitytracker.models.StopwatchListener;
import org.secuso.privacyfriendlyactivitytracker.models.StopwatchModel;
import org.secuso.privacyfriendlyactivitytracker.models.WorkoutInfo;
import org.secuso.privacyfriendlyactivitytracker.persistence.Exercise;
import org.secuso.privacyfriendlyactivitytracker.persistence.ExerciseRepository;
import org.secuso.privacyfriendlyactivitytracker.persistence.MeasureDatabase;
import org.secuso.privacyfriendlyactivitytracker.services.HardwareStepCounterService;
import org.secuso.privacyfriendlyactivitytracker.utils.ExerciseStopView;
import org.secuso.privacyfriendlyactivitytracker.utils.StopwatchTextController;
import org.secuso.privacyfriendlyactivitytracker.utils.TrainingWarningDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.PAUSED;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RESET;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RUNNING;

/**
 * 운동측정화면
 */
public class ExerciseMeasureActivity extends AppCompatActivity implements View.OnClickListener,
        ExerciseStopView.OnFinishListener, SharedPreferences.OnSharedPreferenceChangeListener {
    /** Stopwatch 의 상태를 보관하는 Key */
    private static final String STATE = "sw_state";

    /** 측정중일때 재그리기사이간격(milliseconds) */
    private static final int REDRAW_PERIOD_RUNNING = 25;

    /** 중지상태일때 재그리기사이간격(milliseconds) */
    private static final int REDRAW_PERIOD_PAUSED = 500;

    TextView mDuration;
    TextView mDistance;
    TextView mCalorie;
    TextView mSpeed;
    TextView mSteps;
    ViewGroup mStateContainer;
    ImageView mStart;
    ImageView mPause;
    ExerciseStopView mStop;
    TextView mStopHint;
    TextView mSensorDesc;
    TextView mCountDownText;
    EasyRevealFrameLayout mEasyRevealLinearLayout;

    StopwatchTextController mStopwatchTextController;

    MeasureDatabase mDatabase;

    private final int EXERCISE_TYPE_INDOOR_RUNNING = 0;
    private final int EXERCISE_TYPE_OUTDOOR_RUNNING = 1;
    private final int EXERCISE_TYPE_WALKING = 2;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    /** Stopwatch가 실행되는동안 stopWatch시간 및 현재 lap 시간을 갱신하도록 예약 */
    private final Runnable mTimeUpdateRunnable = new TimeUpdateRunnable();

    private final StopwatchListener mStopwatchWatcher = new StopwatchWatcher();

    Toast mToast = null;
    int exerciseType; // 운동형태 0: 실내달리기, 1: 실외달리기, 2: 걷기
    int mCurrentCount; // 시간계수기 상태
    float distance, speedDistance, stepLength;

    Handler mCountDownHandler = new Handler(); // 시간계수기를 위한 Handler
    Handler mSpeedHandler = new Handler(); // 속도측정을 위한 Handler

    private final Runnable mSpeedCalcRunnable = new Runnable() {
        @SuppressLint("DefaultLocale")
        @Override
        public void run() {
            //7초간격으로 속도상태 갱신
            int trainingSpeedSteps = sharedPref.getInt(getString(R.string.pref_training_speed_steps), 0);
            if (trainingSpeedSteps > 0) {
                speedDistance = (float) (Math.round(trainingSpeedSteps * stepLength)) / 1000;
                mSpeed.setText(String.format("%.1f", speedDistance / 7 * 3600));
            } else {
                mSpeed.setText(getString(R.string.empty_value));
            }

            //속도상태를 반영하는데 필요한 걸음수자료 재설정
            if (editor == null)
                editor = sharedPref.edit();
            editor.putInt(getString(R.string.pref_training_speed_steps), 0);
            editor.apply();

            mSpeedHandler.postDelayed(this, 7000);
        }
    };

    private final Runnable mCountDown = new Runnable() {
        public void run() {
            if (mCurrentCount > 0) {
                mCountDownText.setText(String.valueOf(mCurrentCount));

                //animation of count down
                ObjectAnimator scaleFirstXAnimator = ObjectAnimator.ofFloat(mCountDownText, "scaleX", 0f, 1f);
                scaleFirstXAnimator.setDuration(400);
                ObjectAnimator scaleFirstYAnimator = ObjectAnimator.ofFloat(mCountDownText, "scaleY", 0f, 1f);
                scaleFirstYAnimator.setDuration(400);
                ObjectAnimator alphaFirstAnimator = ObjectAnimator.ofFloat(mCountDownText, "alpha", 0f, 1f);
                alphaFirstAnimator.setDuration(400);
                AnimatorSet firstAnimatorSet = new AnimatorSet();
                firstAnimatorSet.playTogether(scaleFirstXAnimator, scaleFirstYAnimator, alphaFirstAnimator);

                ObjectAnimator secondAnimator = ObjectAnimator.ofFloat(mCountDownText, "alpha", 1f, 1f);
                secondAnimator.setDuration(200);

                ObjectAnimator scaleThirdXAnimator = ObjectAnimator.ofFloat(mCountDownText, "scaleX", 1f, 2f);
                scaleThirdXAnimator.setDuration(400);
                ObjectAnimator scaleThirdYAnimator = ObjectAnimator.ofFloat(mCountDownText, "scaleY", 1f, 2f);
                scaleThirdYAnimator.setDuration(400);
                ObjectAnimator alphaThirdAnimator = ObjectAnimator.ofFloat(mCountDownText, "alpha", 1f, 0f);
                alphaThirdAnimator.setDuration(400);
                AnimatorSet thirdAnimatorSet = new AnimatorSet();
                thirdAnimatorSet.playTogether(scaleThirdXAnimator, scaleThirdYAnimator, alphaThirdAnimator);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playSequentially(firstAnimatorSet, secondAnimator, thirdAnimatorSet);
                animatorSet.start();

                mCurrentCount--;
            } else {
                /* Countdown animation 이 끝났음 */
                boolean supportStepDetectorSensor = getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
                // 운동측정이 시작되면 자동중지를 위한 handler 를 실행
                if (supportStepDetectorSensor) {
                    if (editor == null)
                        editor = sharedPref.edit();
                    editor.putLong(getString(R.string.pref_training_last_updated_time), System.currentTimeMillis());
                    editor.putBoolean(getString(R.string.pref_training_auto_pause), false);
                    editor.putBoolean(getString(R.string.pref_training_user_pause), false);
                    editor.apply();

                    checkTrainingStats(true);
                }

                mCountDownText.setVisibility(View.GONE);
                CircularClipPathProvider circularClipPathProvider = (CircularClipPathProvider) mEasyRevealLinearLayout.getClipPathProvider();
                circularClipPathProvider.setCircleCenter((float) mEasyRevealLinearLayout.getWidth() / 2,
                        mEasyRevealLinearLayout.getHeight() - mSensorDesc.getHeight() - Utils.dip2px(60));
                mEasyRevealLinearLayout.hide();

                //animation 이 끝난후 statusbar 와 navigationbar의 색상 복원
                getWindow().setStatusBarColor(getResources().getColor(R.color.transparent));
                getWindow().setNavigationBarColor(getResources().getColor(R.color.transparent));

                //운동측정 재개
                if (!getStopwatch().isRunning()) {
                    doStart();
                }
                showPauseButton(true);
            }
        }
    };

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_measure);

        mDuration = findViewById(R.id.duration);
        mDistance = findViewById(R.id.distance);
        mCalorie = findViewById(R.id.calorie);
        mSpeed = findViewById(R.id.speed);
        mSteps = findViewById(R.id.steps);
        mStateContainer = (ViewGroup) findViewById(R.id.state_container);
        mStart = mStateContainer.findViewById(R.id.start);
        mStart.setOnClickListener(this);
        mPause = mStateContainer.findViewById(R.id.pause);
        mPause.setOnClickListener(this);
        mStop = mStateContainer.findViewById(R.id.stop);
        mStop.setOnFinishListener(this);
        mSensorDesc = findViewById(R.id.sensor_desc);
        mStopHint = findViewById(R.id.stop_hint);
        mCountDownText = findViewById(R.id.countdown);
        mEasyRevealLinearLayout = findViewById(R.id.hide_area);

        CircularClipPathProvider circularClipPathProvider = new CircularClipPathProvider();
        mEasyRevealLinearLayout.setClipPathProvider(circularClipPathProvider);

        mStopwatchTextController = new StopwatchTextController(mDuration);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.registerOnSharedPreferenceChangeListener(this);
        exerciseType = sharedPref.getInt(getString(R.string.pref_training_type), 0);

        //운동측정이 진행중이면 중지, 시작단추 숨기기 및 일시중지 현시
        final int stateIndex = sharedPref.getInt(STATE, RESET.ordinal());
        final Stopwatch.State state = Stopwatch.State.values()[stateIndex];
        if (state == RUNNING) {
            mEasyRevealLinearLayout.setVisibility(View.GONE);
            mStart.setVisibility(View.GONE);
            mStop.setVisibility(View.GONE);
            mPause.setVisibility(View.VISIBLE);
            showPauseButton(true);
        } else if (state == PAUSED) {
            mEasyRevealLinearLayout.setVisibility(View.GONE);
        } else if (state == RESET) {
            //status bar 와 navigation bar 의 색상을 배경색상과 동일하게 설정
            getWindow().setStatusBarColor(getResources().getColor(R.color.tab_active_color));
            getWindow().setNavigationBarColor(getResources().getColor(R.color.tab_active_color));

            startCountDownAnimation();
        }

        mDatabase = MeasureDatabase.getInstance(this);
        if (exerciseType == EXERCISE_TYPE_INDOOR_RUNNING || exerciseType == EXERCISE_TYPE_OUTDOOR_RUNNING) {
            stepLength = mDatabase.walkingModesDao().getRunningStepLength();
        } else {
            stepLength = mDatabase.walkingModesDao().getWalkingStepLength();
        }

        updateData();

        StopwatchModel.getStopwatchModel().addStopwatchListener(mStopwatchWatcher);

        mSpeedHandler.postDelayed(mSpeedCalcRunnable, 7000);
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateUI();

        //application 상태를 foreground 로 설정
        if (!StopwatchModel.getStopwatchModel().getNotificationModel().isApplicationInForeground()) {
            StopwatchModel.getStopwatchModel().getNotificationModel().setApplicationInForeground(true);
            StopwatchModel.getStopwatchModel().updateNotification();
        }
    }

    @Override
    protected void onStop() {
        //application 상태를 background 로 설정
        if (!isChangingConfigurations()) {
            if (StopwatchModel.getStopwatchModel().getNotificationModel().isApplicationInForeground()) {
                StopwatchModel.getStopwatchModel().getNotificationModel().setApplicationInForeground(false);
                StopwatchModel.getStopwatchModel().updateNotification();
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        StopwatchModel.getStopwatchModel().removeStopwatchListener(mStopwatchWatcher);
        mSpeedHandler.removeCallbacks(mSpeedCalcRunnable);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (editor == null)
            editor = sharedPref.edit();
        boolean supportStepDetectorSensor = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        if (view.getId() == R.id.start) {
            if (supportStepDetectorSensor) {
                // 사용자에 의해 운동측정이 재개되였을때 preference 에 자동중지관련 상태보관
                editor.putBoolean(getString(R.string.pref_training_user_pause), false);
                editor.putBoolean(getString(R.string.pref_training_auto_pause), false);
                editor.putLong(getString(R.string.pref_training_last_updated_time), System.currentTimeMillis());
            }
            showPauseButton(true);
        } else if (view.getId() == R.id.pause) {
            if (supportStepDetectorSensor) {
                // 사용자에 의해 운동측정이 중지되였을때 preference 에 자동중지관련 상태보관
                editor.putBoolean(getString(R.string.pref_training_user_pause), true);
                editor.putBoolean(getString(R.string.pref_training_auto_pause), false);
            }
            showPauseButton(false);
        }
        editor.apply();
    }

    /**
     * 운동측정이 끝났을때 호출되는 callback
     */
    @Override
    public void onFinished() {
        if (!mSteps.getText().toString().equals(getString(R.string.empty_value))) {
            //측정한 거리가 0.1km이하이면 자료기지에 자료 보관안함, 이상이면 운동자료를 자료기지에 보관
            if (Float.parseFloat(mDistance.getText().toString()) >= 0.1) {
                Calendar now = Calendar.getInstance();
                int activity;
                if (exerciseType == EXERCISE_TYPE_INDOOR_RUNNING) {
                    activity = 4;
                } else if (exerciseType == EXERCISE_TYPE_OUTDOOR_RUNNING) {
                    activity = 1;
                } else {
                    activity = 2;
                }

                SimpleDateFormat simpleMonthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
                SimpleDateFormat simpleDayFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat simpleYearFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

                Exercise exercise = new Exercise(0, activity, mDuration.getText().toString(), getStopwatch().getTotalTime(),
                        (float) Math.round(Float.parseFloat(mDistance.getText().toString()) * 100) / 100, simpleYearFormat.format(now.getTime()),
                        simpleMonthFormat.format(now.getTime()), simpleDayFormat.format(now.getTime()),
                        now.getTimeInMillis() - getStopwatch().getTotalTime(), now.getTimeInMillis());
                ExerciseRepository exerciseRepository = new ExerciseRepository(getApplication());
                WorkoutInfo workoutInfo = new WorkoutInfo(exercise);
                exerciseRepository.insertOrUpdate(workoutInfo);

                //Stopwatch 재설정
                StopwatchModel.getStopwatchModel().resetStopwatch();
                //걸음수와 관련한 SharedPreference 재설정
                resetStepPreferences();
                finish();
                // 자동중지를 위한 handler 취소
                checkTrainingStats(false);
                Intent intent = new Intent(this, WorkoutShowActivity.class);
                intent.putExtra("workoutInfo", workoutInfo);
                startActivity(intent);

            } else {
                TrainingWarningDialog trainingWarningDialog = new TrainingWarningDialog(this);
                trainingWarningDialog.setOnEndClickListener(isEndClicked -> {
                    if (isEndClicked) {
                        finish();
                        // 자동중지를 위한 handler 취소
                        checkTrainingStats(false);
                        //Stopwatch 재설정
                        StopwatchModel.getStopwatchModel().resetStopwatch();
                        //걸음수와 관련한 SharedPreference 재설정
                        resetStepPreferences();

                    } else {
                        //운동측정 재개
                        if (!getStopwatch().isRunning()) {
                            doStart();
                        }
                        showPauseButton(true);
                    }
                });
                trainingWarningDialog.setCanceledOnTouchOutside(false);
                trainingWarningDialog.show();
            }
        } else {
            TrainingWarningDialog trainingWarningDialog = new TrainingWarningDialog(this);
            trainingWarningDialog.setOnEndClickListener(isEndClicked -> {
                if (isEndClicked) {
                    finish();
                    // 자동중지를 위한 handler 취소
                    checkTrainingStats(false);
                    //Stopwatch 재설정
                    StopwatchModel.getStopwatchModel().resetStopwatch();
                    //걸음수와 관련한 SharedPreference 재설정
                    resetStepPreferences();
                } else {
                    //운동측정 재개
                    if (!getStopwatch().isRunning()) {
                        doStart();
                    }
                    showPauseButton(true);
                }
            });
            trainingWarningDialog.setCanceledOnTouchOutside(false);
            trainingWarningDialog.show();
        }
    }

    @Override
    public void onCanceled() {
        mStopHint.setAlpha(1.0f);
        mStopHint.animate().setStartDelay(1000).alpha(0.0f).setDuration(500);
    }

    @Override
    public void onBackPressed() {
        if (getStopwatch().isRunning() || getStopwatch().isPaused()) {
            if (mToast != null) mToast.cancel();
            mToast = Toast.makeText(this, R.string.training_finish_warning, Toast.LENGTH_SHORT);
            mToast.show();
            return;
        }
        super.onBackPressed();
    }

    /**
     * 거리, 걸음수, 카로리갱신을 위한 함수
     */
    @SuppressLint("DefaultLocale")
    private void updateData() {
        int totalSteps = sharedPref.getInt(getString(R.string.pref_training_total_steps), 0);
        if (totalSteps > 0) {
            mSteps.setText(String.valueOf(totalSteps));
            distance = (float) (Math.round(totalSteps * stepLength)) / 1000;
            mDistance.setText(String.format("%.2f", distance));
            if (exerciseType == EXERCISE_TYPE_INDOOR_RUNNING || exerciseType == EXERCISE_TYPE_OUTDOOR_RUNNING) {
                mCalorie.setText(String.valueOf(Math.round(distance * 60)));
            } else {
                mCalorie.setText(String.valueOf(Math.round(distance * 30)));
            }
        } else {
            String emptyValue = getString(R.string.empty_value);
            mDistance.setText(emptyValue);
            mCalorie.setText(emptyValue);
            mSteps.setText(emptyValue);
        }
    }

    /**
     * 운동측정자동중지를 위한 handler 를 실행하기 위해 service 실행
     * @param isStarted 운동측정시작상태여부
     */
    private void checkTrainingStats(boolean isStarted) {
        boolean supportStepDetectorSensor = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        if (supportStepDetectorSensor) {
            Intent serviceIntent = new Intent(getApplicationContext(), HardwareStepCounterService.class);
            serviceIntent.putExtra("training_started", isStarted);
            startService(serviceIntent);
        }
    }

    /**
     * countdown animation을 현시하기 위한 함수
     */
    private void startCountDownAnimation() {
        mCountDownHandler.removeCallbacks(mCountDown);

        mCountDownText.setText(String.valueOf(3));
        mCountDownText.setVisibility(View.VISIBLE);

        mCurrentCount = 3;

        mCountDownHandler.post(mCountDown);
        for (int i = 1; i <= 3; i ++) {
            mCountDownHandler.postDelayed(mCountDown, i * 1000);
        }
    }

    /**
     * 일시중지단추 현시상태 갱신함수
     * @param isShown true 이면 현시, false 이면 숨기기
     */
    private void showPauseButton(boolean isShown) {
        if (isShown) {
            //시작, 중지단추 숨기기 및 일시중지단추 현시
            ObjectAnimator moveStart = ObjectAnimator.ofFloat(mStart, "translationX", Utils.dip2px(-60));
            moveStart.setDuration(200);
            moveStart.start();
            moveStart.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    mStart.setVisibility(View.GONE);
                }
            });
            ObjectAnimator moveStop = ObjectAnimator.ofFloat(mStop, "translationX", Utils.dip2px(60));
            moveStop.setDuration(200);
            moveStop.start();
            moveStop.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation, boolean isReverse) {
                    mStop.setVisibility(View.GONE);
                    mPause.setVisibility(View.VISIBLE);
                }
            });

            if (!getStopwatch().isRunning()) {
                doStart();
            }
        } else {
            //일시중지단추 숨기기 및 시작 및 중지단추 현시
            mPause.setVisibility(View.GONE);
            mStart.setVisibility(View.VISIBLE);
            mStop.setVisibility(View.VISIBLE);
            ObjectAnimator moveStart = ObjectAnimator.ofFloat(mStart, "translationX", Utils.dip2px(0));
            moveStart.setDuration(200);
            moveStart.start();
            ObjectAnimator moveStop = ObjectAnimator.ofFloat(mStop, "translationX", Utils.dip2px(0));
            moveStop.setDuration(200);
            moveStop.start();

            if (getStopwatch().isRunning()) {
                doPause();
            }
        }
    }

    /**
     * UI 갱신 함수
     */
    private void updateUI() {
        adjustWakeLock();
        updateTime();

        startUpdatingTime();
    }

    /**
     * 첫번째 runnable 을 실행하여 UI 내에서 시간갱신, 필요에따라 자기자체를 재예약
     */
    private void startUpdatingTime() {
        // Ensure only one copy of the runnable is ever scheduled by first stopping updates.
        stopUpdatingTime();
        mDuration.post(mTimeUpdateRunnable);
    }

    /**
     * UI 에서 시간을 갱신하는 runnable 중지
     */
    private void stopUpdatingTime() {
        mDuration.removeCallbacks(mTimeUpdateRunnable);
    }

    /**
     * 시간갱신 함수
     */
    @SuppressLint("DefaultLocale")
    private void updateTime() {
        final Stopwatch stopwatch = getStopwatch();
        final long totalTime = stopwatch.getTotalTime();
        mStopwatchTextController.setTimeString(totalTime);
    }

    /**
     * Stopwatch 얻는 함수
     * @return Stopwatch
     */
    private Stopwatch getStopwatch() {
        return StopwatchModel.getStopwatchModel().getStopwatch();
    }

    /**
     * 화면켜기 설정함수
     */
    private void adjustWakeLock() {
        final boolean appInForeground = StopwatchModel.getStopwatchModel().getNotificationModel().isApplicationInForeground();
        if (getStopwatch().isRunning() && appInForeground) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            releaseWakeLock();
        }
    }

    /**
     * 화면켜기 취소함수
     */
    private void releaseWakeLock() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Stopwatch 시작함수
     */
    private void doStart() {
        StopwatchModel.getStopwatchModel().startStopwatch();
    }

    /**
     * Stopwatch 중지함수
     */
    private void doPause() {
        StopwatchModel.getStopwatchModel().pauseStopwatch();
    }

    /**
     * 걸음수관련 SharedPreference 설정들을 초기화하는 함수
     */
    private void resetStepPreferences() {
        if (editor == null)
            editor = sharedPref.edit();
        editor.putInt(getString(R.string.pref_training_total_steps), 0);
        editor.putInt(getString(R.string.pref_training_speed_steps), 0);
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_training_total_steps))) {
            updateData();
        } else if (key.equals(getString(R.string.pref_training_auto_pause))) {
            showPauseButton(!sharedPref.getBoolean(getString(R.string.pref_training_auto_pause), true));
        }
    }

    /**
     * Stopwatch가 변경되는데 맞게 UI갱신
     */
    private class StopwatchWatcher implements StopwatchListener {
        @Override
        public void stopwatchUpdated(Stopwatch before, Stopwatch after) {
            if (StopwatchModel.getStopwatchModel().getNotificationModel().isApplicationInForeground()) {
                updateUI();
            }
        }
    }

    /**
     * 주기적으로 시간을 갱신하는 runnable.
     * Stopwatch가 더 이상 실행되지 않으면 갱신중지
     */
    private final class TimeUpdateRunnable implements Runnable {
        @Override
        public void run() {
            final long startTime = Utils.now();

            updateTime();

            // Blink text iff the stopwatch is paused and not pressed.
            final Stopwatch stopwatch = getStopwatch();

            if (!stopwatch.isReset()) {
                final long period = stopwatch.isPaused()
                        ? REDRAW_PERIOD_PAUSED
                        : REDRAW_PERIOD_RUNNING;
                final long endTime = Utils.now();
                final long delay = Math.max(0, startTime + period - endTime);
                mDuration.postDelayed(this, delay);
            }
        }
    }
}