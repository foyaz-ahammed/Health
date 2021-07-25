package org.secuso.privacyfriendlyactivitytracker.receivers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.legacy.content.WakefulBroadcastReceiver;

import org.secuso.privacyfriendlyactivitytracker.Factory;
import org.secuso.privacyfriendlyactivitytracker.services.HardwareStepCounterService;

/**
 * 걸음수관련 service를 항상 켜놓도록 하기 위한 broadcastReceiver
 */
public class StepCountPersistenceReceiver extends WakefulBroadcastReceiver {
    private static final String LOG_CLASS = StepCountPersistenceReceiver.class.getName();
    /**
     * The application context
     */
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(LOG_CLASS, "Storing the steps");
        this.context = context.getApplicationContext();
        Log.i(LOG_CLASS, "isStepServiceRunning is " + isStepServiceRunning());
        if (!isStepServiceRunning()) {

            Intent serviceIntent = new Intent(context, Factory.getStepDetectorServiceClass(context.getPackageManager()));
            serviceIntent.putExtra("started_foreground_service", true);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

    /**
     * 현재 service가 실행중인지 확인
     * @return 실행중이면 true, 아니면 false
     */
    private boolean isStepServiceRunning() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (HardwareStepCounterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
