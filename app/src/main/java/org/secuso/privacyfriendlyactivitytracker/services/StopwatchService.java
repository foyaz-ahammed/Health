package org.secuso.privacyfriendlyactivitytracker.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.activities.ExerciseMeasureActivity;
import org.secuso.privacyfriendlyactivitytracker.activities.MainActivity;
import org.secuso.privacyfriendlyactivitytracker.models.StopwatchModel;

/**
 * 이 service 는 Stopwatch 알림이 알림창을 방해하지 않고 Stopwatch 의 상태를 변경할수 있도록 하기 위해서만 존재한다.
 * Activity 가 대신 사용된 경우(표시되지 않은 경우에도) 알림 관리자는 알림창을 방해하지 않고 Stopwatch 를 시작/일시중지/
 * lapping/재설정하는 경우와 충돌하는 알림창을 암시적으로 닫는다.
 */
public final class StopwatchService extends Service {

    private static final String ACTION_PREFIX = "com.android.deskclock.action.";

    public static final String ACTION_SHOW_STOPWATCH = ACTION_PREFIX + "SHOW_STOPWATCH";
    // 현재 Stopwatch 시작
    public static final String ACTION_START_STOPWATCH = ACTION_PREFIX + "START_STOPWATCH";
    // 현재 실행중인 Stopwatch 를 일시중지
    public static final String ACTION_PAUSE_STOPWATCH = ACTION_PREFIX + "PAUSE_STOPWATCH";
    public static final String ACTION_RESUME_STOPWATCH = ACTION_PREFIX + "RESUME_STOPWATCH";
    // Stopwatch 가 중지되면 재설정
    public static final String ACTION_RESET_STOPWATCH = ACTION_PREFIX + "RESET_STOPWATCH";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        switch (action) {
            case ACTION_SHOW_STOPWATCH: {

                final Intent showMain = new Intent(this, MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(showMain);
                final Intent showTraining = new Intent(this, ExerciseMeasureActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(showTraining);
                break;
            }
            case ACTION_START_STOPWATCH:
            case ACTION_RESUME_STOPWATCH: {
                // 사용자가 운동측정을 시작 및 재개하였을때 preference 에 자동중지상태 갱신
                boolean supportStepDetectorSensor = getPackageManager().hasSystemFeature(
                        PackageManager.FEATURE_SENSOR_STEP_DETECTOR);
                if (supportStepDetectorSensor) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean(getString(R.string.pref_training_user_pause), false);
                    editor.putBoolean(getString(R.string.pref_training_auto_pause), false);
                    editor.putLong(getString(R.string.pref_training_last_updated_time), System.currentTimeMillis());
                    editor.apply();
                }
                StopwatchModel.getStopwatchModel().startStopwatch();
                break;
            }
            case ACTION_PAUSE_STOPWATCH: {
                StopwatchModel.getStopwatchModel().pauseStopwatch();
                break;
            }
        }

        return START_NOT_STICKY;
    }
}
