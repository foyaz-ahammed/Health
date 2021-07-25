package org.secuso.privacyfriendlyactivitytracker.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.models.Stopwatch;
import org.secuso.privacyfriendlyactivitytracker.models.StopwatchModel;
import org.secuso.privacyfriendlyactivitytracker.utils.StepDetectionServiceHelper;

import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.PAUSED;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RESET;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RUNNING;

/**
 * 걸음수를 위한 기초 service
 */
public abstract class AbstractStepDetectorService extends IntentService implements SensorEventListener, SharedPreferences.OnSharedPreferenceChangeListener {
    /** Stopwatch의 상태를 보관하는 key */
    public static final String STATE = "sw_state";

    private static final String LOG_TAG = AbstractStepDetectorService.class.getName();
    private final IBinder mBinder = new StepDetectorBinder();
    private PowerManager.WakeLock mWakeLock;

    public SharedPreferences sharedPref;
    public SharedPreferences.Editor editor;

    // 운동측정 자동중지를 위한 handler 와 runnable
    Handler handler = new Handler();
    Runnable autoPauseRunnable = new Runnable() {
        @Override
        public void run() {
            if (sharedPref == null)
                sharedPref = PreferenceManager.getDefaultSharedPreferences(AbstractStepDetectorService.this);
            long trainingLastSaved = sharedPref.getLong(getString(R.string.pref_training_last_updated_time), System.currentTimeMillis());
            boolean isUserPaused = sharedPref.getBoolean(getString(R.string.pref_training_user_pause), false);
            boolean isAutoPaused = sharedPref.getBoolean(getString(R.string.pref_training_auto_pause), false);
            // 운동을 하지 않은 시간이 15초이상이면 자동으로 중지
            if (!isUserPaused && !isAutoPaused && System.currentTimeMillis() - trainingLastSaved > 15000) {
                if (editor == null)
                    editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_training_auto_pause), true);
                editor.apply();
                StopwatchModel.getStopwatchModel().pauseStopwatch();
            }
            handler.postDelayed(this, 1000);
        }
    };

    /**
     * Service 가 실행된후부터 루적된 걸음수
     */
    private int total_steps = 0;

    /**
     * IntentService 창조. subclass 의 구성자로부터 호출된다.
     * @param name worker thread 의 이름을 지정하는데만 리용되며 debugging 에만 중요하다.
     */
    public AbstractStepDetectorService(String name) {
        super(name);
    }

    /**
     * 감지된 걸음수를 subscriber 에게 알려준다.
     * @param count 감지된 걸음수 (0이상)
     */
    protected void onStepDetected(int count) {
        if (count <= 0) {
            return;
        }
        this.total_steps += count;
        Log.i(LOG_TAG, count + " Step(s) detected. Steps since service start: " + this.total_steps);
    }

    // subclass 들에서 구현
    @Override
    public abstract void onSensorChanged(SensorEvent event);

    /**
     * The sensor type(s) on which the step detection service should listen
     * 걸음수 감지 service 가 리용할 sensor 형태
     * @return 요청된 sensor 형태
     * @see SensorManager#getDefaultSensor
     */
    public abstract int getSensorType();

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // currently doing nothing here.
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "Creating service.");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(LOG_TAG, "Removing task");
        // 장치가 STEP_DETECTOR sensor 를 지원하지 않으면 app 이 종료될때 운동측정 중지
        boolean supportStepDetectorSensor = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        if (!supportStepDetectorSensor) {
            final int stateIndex = sharedPref.getInt(STATE, RESET.ordinal());
            final Stopwatch.State state = Stopwatch.State.values()[stateIndex];
            if (state == RUNNING) {
                StopwatchModel.getStopwatchModel().pauseStopwatch();
            }
        }
        super.onTaskRemoved(rootIntent);
        Intent intent = new Intent("com.android.ServiceStopped");
        sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "Destroying service.");
        Log.w("AbstractService", "onDestroy is called");
        // wake lock 해제
        acquireOrReleaseWakeLock();
        // Sensor Listener 들에 대한 등록취소
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
        //Shared Preference listener 들에 대한 등록취소
        if (sharedPref == null)
            sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartserver");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);

        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "Starting service.");
        acquireOrReleaseWakeLock();

        if(!StepDetectionServiceHelper.isStepDetectionEnabled(getApplicationContext())){
            stopSelf();
        }
        // Sensor 들을 등록
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Log.i(LOG_TAG, "onStartCommand sensor type is " + this.getSensorType());
        Sensor sensor = sensorManager.getDefaultSensor(this.getSensorType());
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        // 운동측정 자동중지를 위해 STEP_DETECTOR  sensor 등록
        boolean supportStepDetectorSensor = getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
        if (supportStepDetectorSensor) {
            sensorManager.registerListener(this, sensorManager.getDefaultSensor(
                    Sensor.TYPE_STEP_DETECTOR), SensorManager.SENSOR_DELAY_FASTEST);
        }

        //Preference 에서 일별걸음수목표를 얻는다.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String d = sharedPref.getString(getString(R.string.pref_daily_step_goal), "10000");
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        if (intent != null) {
            boolean startedForegroundService = intent.getBooleanExtra("started_foreground_service", false);
            if (startedForegroundService) {
                intent.putExtra("started_foreground_service", false);
                startService(intent);
                Log.i(LOG_TAG, "background service just has been started");
            }

            // 운동측정 자동중지를 위한 handler 실행
            if (supportStepDetectorSensor) {
                if (intent.hasExtra("training_started")) {
                    checkTrainingStats(!intent.getBooleanExtra("training_started", false));
                } else {
                    final int stateIndex = sharedPref.getInt(STATE, RESET.ordinal());
                    final Stopwatch.State state = Stopwatch.State.values()[stateIndex];
                    if (state == RUNNING || state == PAUSED) {
                        checkTrainingStats(false);
                    }
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.mBinder;
    }

    @Override
    public void onHandleIntent(Intent intent) {
        // currently doing nothing here.
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.pref_use_wake_lock))){
            acquireOrReleaseWakeLock();
        }
    }

    /**
     * 운동측정 자동중지 handler 실행
     * 현재 운동측정중이면 Handler 를 실행하고 운동측정중지상태이면 handler 취소
     * @param isStopped 운동측정 중지상태여부
     */
    private void checkTrainingStats(boolean isStopped) {
        handler.removeCallbacks(autoPauseRunnable);
        if (!isStopped)
            handler.postDelayed(autoPauseRunnable, 1000);
    }

    /**
     * wake lock 설정 또는 해제
     */
    private void acquireOrReleaseWakeLock(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean useWakeLock = sharedPref.getBoolean(getString(R.string.pref_use_wake_lock), false);
        boolean useWakeLockDuringTraining = sharedPref.getBoolean(getString(R.string.pref_use_wake_lock_during_training), true);
        if(mWakeLock == null && (useWakeLock || (useWakeLockDuringTraining))) {
            acquireWakeLock();
        }
        if(mWakeLock != null && !(useWakeLock || (useWakeLockDuringTraining))){
            releaseWakeLock();
        }
    }

    /**
     * wake lock 설정
     */
    private void acquireWakeLock(){
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if(mWakeLock == null || !mWakeLock.isHeld()) {
            mWakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepDetectorWakeLock");
            mWakeLock.acquire();
        }
    }

    /**
     * wake lock 가 설정되여 있으면 해제
     */
    private void releaseWakeLock(){
        if(mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }
    /**
     * Class used for the client Binder.
     */
    public class StepDetectorBinder extends Binder {

        public AbstractStepDetectorService getService() {
            return AbstractStepDetectorService.this;
        }
    }
}
