package org.secuso.privacyfriendlyactivitytracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.persistence.MeasureDatabase;
import org.secuso.privacyfriendlyactivitytracker.persistence.StepCountRepository;
import org.secuso.privacyfriendlyactivitytracker.services.HardwareStepCounterService;
import org.secuso.privacyfriendlyactivitytracker.utils.StepDetectionServiceHelper;

import java.util.Calendar;

/**
 * 체계기동이 끝났다는 broadcast 를 받고 step service 를 기동
 */

public class OnBootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("Health, ", "boot completed");
        // reset hardware step count since last reboot
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(context.getString(R.string.pref_hw_steps_on_last_save), 0);
        editor.apply();

        // step service 실행
        Intent serviceIntent = new Intent(context, HardwareStepCounterService.class);
        serviceIntent.putExtra("started_foreground_service", true);
        ContextCompat.startForegroundService(context, serviceIntent);

        StepDetectionServiceHelper.schedulePersistenceService(context);
        StepDetectionServiceHelper.scheduleStepNotification(context);

        MeasureDatabase db = MeasureDatabase.getInstance(context);
        Intent sendIntent = new Intent();
        sendIntent.setPackage("ch.deletescape.lawnchair.dev");
        sendIntent.setAction("com.kr.health.STEP_CHANGED");
        sendIntent.putExtra("step", db.stepCountDao().getTotalStepsByDate(Utils.getIntDate(Calendar.getInstance())));
        context.sendBroadcast(sendIntent);
    }
}
