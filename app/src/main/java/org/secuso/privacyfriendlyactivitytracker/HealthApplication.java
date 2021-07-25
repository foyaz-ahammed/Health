package org.secuso.privacyfriendlyactivitytracker;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.secuso.privacyfriendlyactivitytracker.models.StopwatchModel;

public class HealthApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        final Context context = getApplicationContext();
        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        //운동측정 Stopwatch 초기화
        StopwatchModel.getStopwatchModel().init(context, sharedPref);
    }
}
