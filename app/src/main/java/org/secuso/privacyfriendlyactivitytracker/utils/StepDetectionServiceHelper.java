package org.secuso.privacyfriendlyactivitytracker.utils;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.secuso.privacyfriendlyactivitytracker.Factory;
import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.receivers.DailyNotificationReceiver;
import org.secuso.privacyfriendlyactivitytracker.receivers.HardwareStepCountReceiver;
import org.secuso.privacyfriendlyactivitytracker.receivers.StepCountPersistenceReceiver;

import java.util.Calendar;
import java.util.Date;

/**
 * Service 관련 클라스
 */
public class StepDetectionServiceHelper {

    private static final String LOG_CLASS = StepDetectionServiceHelper.class.getName();

    //Step service 실행
    public static void startAllIfEnabled(boolean forceRealTimeStepDetection, Context context){
        Log.i(LOG_CLASS, "Start of all services requested");
        // Start the step detection if enabled or training is active
        if (isStepDetectionEnabled(context)) {
            if(forceRealTimeStepDetection || isRealTimeStepDetectionRequired(context) || !AndroidVersionHelper.isHardwareStepCounterEnabled(context.getPackageManager())) {
                Log.i(LOG_CLASS, "Start step detection");
                StepDetectionServiceHelper.startStepDetection(context);
                // schedule stepCountPersistenceService
                StepDetectionServiceHelper.schedulePersistenceService(context);
            }else{
                Log.i(LOG_CLASS, "Schedule hardware step counter request");
                StepDetectionServiceHelper.startHardwareStepCounter(context);
            }
        }
    }

    /**
     * Service 실행
     *
     * @param context The application context
     */
    public static void startStepDetection(Context context) {
        Log.i(LOG_CLASS, "Started step detection service.");
        Intent stepDetectorServiceIntent = new Intent(context, Factory.getStepDetectorServiceClass(context.getPackageManager()));
        context.getApplicationContext().startService(stepDetectorServiceIntent);
    }

    /**
     * Step Service 실행
     * @param context The application context
     */
    public static void startHardwareStepCounter(Context context){
        Intent hardwareStepCounterServiceIntent = new Intent(context, HardwareStepCountReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, hardwareStepCounterServiceIntent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND, 10);

        // Set inexact repeating alarm
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), AlarmManager.INTERVAL_HOUR, sender);
        Log.i(LOG_CLASS, "Scheduled hardware step counter alert at start time " + calendar.toString());
    }

    /**
     *  30분간격으로 service상태를 확인하기 위한 alarm 설정
     *
     * @param context The application context
     */
    @SuppressLint("ShortAlarm")
    public static void schedulePersistenceService(Context context) {
        Intent stepCountPersistenceServiceIntent = new Intent(context, StepCountPersistenceReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 2, stepCountPersistenceServiceIntent, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Fire at next half hour
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = unroundedMinutes % 30;
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MINUTE, (30-mod));

        // Set repeating alarm
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTime().getTime(), AlarmManager.INTERVAL_HALF_HOUR, sender);
        Log.i(LOG_CLASS, "Scheduled repeating persistence service at start time " + calendar.toString());
    }

    /**
     * 매일 알림 설정
     * @param context The application context
     */
    public static void scheduleStepNotification(Context context) {
        Log.w("ServiceHelper", "scheduleStepNotification is called");
        Intent stepNotificationIntent = new Intent(context, DailyNotificationReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, stepNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        am.cancel(sender);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.HOUR_OF_DAY, 20);
        notificationTime.set(Calendar.MINUTE, 0);
        notificationTime.set(Calendar.SECOND, 0);
        notificationTime.set(Calendar.MILLISECOND, 0);
        long notifyTime = sharedPref.getLong(context.getString(R.string.pref_notification_time), notificationTime.getTimeInMillis());
        Calendar notifyCalendar = Calendar.getInstance();
        notifyCalendar.setTimeInMillis(notifyTime);;
        Calendar notificationCalendar = Calendar.getInstance();
        notificationCalendar.set(Calendar.HOUR_OF_DAY, notifyCalendar.get(Calendar.HOUR_OF_DAY));
        notificationCalendar.set(Calendar.MINUTE, notifyCalendar.get(Calendar.MINUTE));
        notificationCalendar.set(Calendar.SECOND, 0);
        notificationCalendar.set(Calendar.MILLISECOND, 0);

        am.setRepeating(AlarmManager.RTC_WAKEUP, notifyCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, sender);
    }

    /**
     * Starts the step detection service
     *
     * @param context The application context
     */
    public static void startPersistenceService(Context context) {
        Log.i(LOG_CLASS, "Started persistence service.");
        Intent stepCountPersistenceServiceIntent = new Intent(context, StepCountPersistenceReceiver.class);
        context.sendBroadcast(stepCountPersistenceServiceIntent);
    }

    /**
     * 걸음수를 측정할수 있는지 확인여부
     * @param context The application context
     * @return 걸음수를 측정할수 있으면 true, 아니면 false
     */
    public static boolean isStepDetectionEnabled(Context context) {
        // Get user preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isStepDetectionEnabled = sharedPref.getBoolean(context.getString(R.string.pref_step_counter_enabled), true);
        boolean isWalkingModeLearningActive = sharedPref.getBoolean(context.getString(R.string.pref_walking_mode_learning_active), false);
        boolean isDistanceMeasurementActive = sharedPref.getLong(context.getString(R.string.pref_distance_measurement_start_timestamp), -1) > 0;
        return isStepDetectionEnabled || isWalkingModeLearningActive || isDistanceMeasurementActive;
    }

    /**
     * Do we need real time step detection or is it ok if we do some more calculation in background
     * and send step detection events delayed?
     * @param context The application context
     * @return true if real time step detection is required
     */
    public static boolean isRealTimeStepDetectionRequired(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isWalkingModeLearningActive = sharedPref.getBoolean(context.getString(R.string.pref_walking_mode_learning_active), false);
        boolean isDistanceMeasurementActive = sharedPref.getLong(context.getString(R.string.pref_distance_measurement_start_timestamp), -1) > 0;
        return isWalkingModeLearningActive || isDistanceMeasurementActive;

    }
}
