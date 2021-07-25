package org.secuso.privacyfriendlyactivitytracker.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.TriggerEventListener;
import android.preference.PreferenceManager;
import android.util.Log;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.models.Stopwatch;
import org.secuso.privacyfriendlyactivitytracker.models.StopwatchModel;
import org.secuso.privacyfriendlyactivitytracker.persistence.Step;
import org.secuso.privacyfriendlyactivitytracker.persistence.StepCountRepository;
import org.secuso.privacyfriendlyactivitytracker.utils.AndroidVersionHelper;

import java.util.Calendar;

import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RESET;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RUNNING;

/**
 * 걸음수를 관리하는 service
 */
public class HardwareStepCounterService extends AbstractStepDetectorService{

    private static final String LOG_TAG = HardwareStepCounterService.class.getName();
    protected TriggerEventListener listener;
    StepCountRepository stepCountRepository;

    public HardwareStepCounterService(){
        super("");
    }
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public HardwareStepCounterService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        stepCountRepository = new StepCountRepository(getApplication());
    }

    /**
     * StepSensor가 새 step을 감지하면 호출되는 callback
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_STEP_DETECTOR:
                // STEP_DETECTOR sensor 를 리용하여 새 걸음을 감지할적마다 갱신된 시간 preference 에 보관
                if (editor == null)
                    editor = sharedPref.edit();
                editor.putLong(getString(R.string.pref_training_last_updated_time), System.currentTimeMillis());
                editor.apply();

                boolean isTrainingAutoPaused = sharedPref.getBoolean(getString(R.string.pref_training_auto_pause), false);
                // 이미 운동측정이 자동으로 중지되였으면 걸음이 다시 시작되였으므로 재개
                if (isTrainingAutoPaused) {
                    StopwatchModel.getStopwatchModel().startStopwatch();
                    editor.putBoolean(getString(R.string.pref_training_auto_pause), false);
                    editor.apply();
                }
                break;
            case Sensor.TYPE_STEP_COUNTER:
                Log.i(LOG_TAG, "Received onSensorChanged");
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(HardwareStepCounterService.this);
                if (editor == null)
                    editor = sharedPref.edit();
                float numberOfHWStepsSinceLastReboot = event.values[0];
                boolean isFirstTimeLaunch = sharedPref.getBoolean(getString(R.string.pref_is_first_time_launch), true);
                float numberOfHWStepsOnLastSave = sharedPref.getFloat(HardwareStepCounterService.this.getString(R.string.pref_hw_steps_on_last_save), 0);
                float numberOfNewSteps;
                if (isFirstTimeLaunch) {
                    numberOfNewSteps = numberOfHWStepsOnLastSave;
                    editor.putBoolean(getString(R.string.pref_is_first_time_launch), false);
                    editor.apply();
                } else {
                    numberOfNewSteps = numberOfHWStepsSinceLastReboot - numberOfHWStepsOnLastSave;
                }
                Log.i(LOG_TAG, numberOfHWStepsSinceLastReboot + " - " + numberOfHWStepsOnLastSave + " = " + numberOfNewSteps);
                // Store new steps
                onStepDetected((int) numberOfNewSteps);
                // store steps since last reboot
                editor.putFloat(getString(R.string.pref_hw_steps_on_last_save), numberOfHWStepsSinceLastReboot);
                editor.apply();
                break;
        }
    }

    /**
     * 새로 감지된 걸음수를 알려주는 함수
     *
     * @param count 새로 감지된 걸음수
     */
    @Override
    protected void onStepDetected(int count) {
        if (count <= 0) {
            return;
        }

        if (sharedPref == null)
            sharedPref = PreferenceManager.getDefaultSharedPreferences(HardwareStepCounterService.this);
        if (editor == null)
            editor = sharedPref.edit();

        final int stateIndex = sharedPref.getInt(STATE, RESET.ordinal());
        final Stopwatch.State state = Stopwatch.State.values()[stateIndex];
        if (state == RUNNING) {
            //운동측정중일때 해당 운동 type에 맞는 자료 보관
            int trainingType = sharedPref.getInt(getString(R.string.pref_training_type), 0);
            Step step = new Step(0, count, trainingType == 2 ? 1 : 2, Utils.getIntDate(Calendar.getInstance()), System.currentTimeMillis());
            stepCountRepository.insertOrUpdateStep(step);
            //save training steps
            int trainingTotalSteps = sharedPref.getInt(getString(R.string.pref_training_total_steps), 0);
            int trainingSpeedSteps = sharedPref.getInt(getString(R.string.pref_training_speed_steps), 0);
            editor.putInt(getString(R.string.pref_training_total_steps), trainingTotalSteps + count);
            editor.putInt(getString(R.string.pref_training_speed_steps), trainingSpeedSteps + count);
            editor.apply();

            //app이 background상태이면 stopWatch 알림창 갱신
            if (!StopwatchModel.getStopwatchModel().getNotificationModel().isApplicationInForeground())
                StopwatchModel.getStopwatchModel().updateNotification();
        } else {
            //운동측정중이 아닌 경우 현재 선택된 운동 type(걷기 혹은 달리기)중 active된것에 맞는 자료 보관
            Step step = new Step(0, count, stepCountRepository.getActiveWalkingMode().getId(), Utils.getIntDate(Calendar.getInstance()), System.currentTimeMillis());
            stepCountRepository.insertOrUpdateStep(step);
        }

        //새 걸음수가 감지되면 launcher에 자료가 갱신된것에 대한 broadcast 보냄
        Intent sendIntent = new Intent();
        sendIntent.setPackage("ch.deletescape.lawnchair.dev");
        sendIntent.setAction("com.kr.health.STEP_CHANGED");
        sendIntent.putExtra("step", stepCountRepository.getTotalStepsByDate(Utils.getIntDate(Calendar.getInstance())));
        sendBroadcast(sendIntent);
    }

    /**
     * sensor type을 결정하는 함수
     * @return
     */
    @Override
    public int getSensorType() {
        Log.i(LOG_TAG, "getSensorType STEP_COUNTER");
        if (AndroidVersionHelper.isHardwareStepCounterEnabled(this.getPackageManager())) {
            return Sensor.TYPE_STEP_COUNTER;
        } else {
            return 0;
        }
    }
}
