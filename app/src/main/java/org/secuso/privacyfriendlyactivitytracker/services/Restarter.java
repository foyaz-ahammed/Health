package org.secuso.privacyfriendlyactivitytracker.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.secuso.privacyfriendlyactivitytracker.R;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Broadcast Listened", "Service tried to stop");

        Intent serviceIntent = new Intent(context, HardwareStepCounterService.class);
        serviceIntent.putExtra("started_foreground_service", true);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}