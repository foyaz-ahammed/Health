package org.secuso.privacyfriendlyactivitytracker.receivers;

import android.content.Context;
import android.content.Intent;
//import android.support.v4.content.WakefulBroadcastReceiver;
import android.os.Build;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

import org.secuso.privacyfriendlyactivitytracker.services.HardwareStepCounterService;

/**
 * 걸음수관련 Service 를 실행시키는 receiver
 */
public class HardwareStepCountReceiver extends WakefulBroadcastReceiver {
    private static final String LOG_CLASS = HardwareStepCountReceiver.class.getName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.i(LOG_CLASS, "Received hardware step count alarm");
        Intent serviceIntent = new Intent(context, HardwareStepCounterService.class);
        context.startService(serviceIntent);
    }
}
