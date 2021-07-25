package org.secuso.privacyfriendlyactivitytracker.models;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import androidx.core.app.NotificationManagerCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.persistence.MeasureDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Stopwatch class
 */
public final class StopwatchModel {

    private Context mContext;

    private SharedPreferences mPrefs;

    /** 알림자료를 가져오는 model */
    private NotificationModel mNotificationModel;

    /** Stopwatch와 관련된 체계알림을 생성하고 파괴하는데 사용되는 manager */
    private NotificationManagerCompat mNotificationManager;

    /** locale이 변경되면 Stopwatch 알림 갱신 */
    @SuppressWarnings("FieldCanBeLocal")
    private final BroadcastReceiver mLocaleChangedReceiver = new LocaleChangedReceiver();

    /** Stopwatch 또는 lap 이 변경되면 listener 들에 알림 */
    private final List<StopwatchListener> mStopwatchListeners = new ArrayList<>();

    /** Delegate that builds platform-specific stopwatch notifications. */
    private final StopwatchNotificationBuilder mNotificationBuilder =
            new StopwatchNotificationBuilder();

    private Stopwatch mStopwatch;

    MeasureDatabase mDatabase;

    private static final StopwatchModel stopwatchModel = new StopwatchModel();

    public StopwatchModel(Context context, SharedPreferences prefs, NotificationModel notificationModel) {
        mContext = context;
        mPrefs = prefs;
        mNotificationModel = notificationModel;
        mNotificationManager = NotificationManagerCompat.from(context);

        // Update stopwatch notification when locale changes.
        final IntentFilter localeBroadcastFilter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
        mContext.registerReceiver(mLocaleChangedReceiver, localeBroadcastFilter);
    }

    public StopwatchModel() {}

    public void init(Context context, SharedPreferences sharedPref) {
        if (mContext != context) {
            mContext = context;
            mPrefs = sharedPref;
            mNotificationModel = new NotificationModel();
            mNotificationManager = NotificationManagerCompat.from(context);

            // Update stopwatch notification when locale changes.
            final IntentFilter localeBroadcastFilter = new IntentFilter(Intent.ACTION_LOCALE_CHANGED);
            mContext.registerReceiver(mLocaleChangedReceiver, localeBroadcastFilter);
        }
    }

    public static StopwatchModel getStopwatchModel() {
        return stopwatchModel;
    }

    public NotificationModel getNotificationModel() {
        return mNotificationModel;
    }

    /**
     * @param stopwatchListener Stopwatch 가 변경 또는 Lap 이 추가될때 알림을 받을 stopwatchListener
     */
    public void addStopwatchListener(StopwatchListener stopwatchListener) {
        mStopwatchListeners.add(stopwatchListener);
    }

    /**
     * @param stopwatchListener Stopwatch 가 변경 또는 Lap 이 추가될때 알림을 더는 받지 않는 stopwatchListener
     */
    public void removeStopwatchListener(StopwatchListener stopwatchListener) {
        mStopwatchListeners.remove(stopwatchListener);
    }

    public void startStopwatch() {
        setStopwatch(getStopwatch().start());
    }

    public void pauseStopwatch() {
        setStopwatch(getStopwatch().pause());
    }

    public void resetStopwatch() {
        setStopwatch(getStopwatch().reset());
    }

    /**
     * @return 현재의 Stopwatch
     */
    public Stopwatch getStopwatch() {
        if (mStopwatch == null) {
            mStopwatch = StopwatchDAO.getStopwatch(mPrefs);
        }

        return mStopwatch;
    }

    /**
     * @param stopwatch 새 상태의 Stopwatch
     */
    public Stopwatch setStopwatch(Stopwatch stopwatch) {
        final Stopwatch before = getStopwatch();
        if (before != stopwatch) {
            StopwatchDAO.setStopwatch(mPrefs, stopwatch);
            mStopwatch = stopwatch;

            // 최신 Stopwatch 상태를 반영하도록 Stopwatch 알림을 변경
            if (!mNotificationModel.isApplicationInForeground()) {
                updateNotification();
            }

            // Listener 들에 Stopwatch 변경 알림
            for (StopwatchListener stopwatchListener : mStopwatchListeners) {
                stopwatchListener.stopwatchUpdated(before, stopwatch);
            }
        }

        return stopwatch;
    }

    /**
     * Stopwatch 및 기록된 lap의 최신상태를 반영하도록 알림을 갱신하는 함수
     */

    public void updateNotification() {
        final Stopwatch stopwatch = getStopwatch();

        //Stopwatch 에 시간이 없거나 app 이 foreground 상태이면 알림 숨기기
        if (stopwatch.isReset() || mNotificationModel.isApplicationInForeground()) {
            mNotificationManager.cancel(mNotificationModel.getStopwatchNotificationId());
            return;
        }

        //운동측정거리 얻기
        if (mDatabase == null)
            mDatabase = MeasureDatabase.getInstance(mContext);
        float distance;
        int exerciseType = mPrefs.getInt(mContext.getString(R.string.pref_training_type), 0);
        int totalTrainingSteps = mPrefs.getInt(mContext.getString(R.string.pref_training_total_steps), 0);
        if (exerciseType == 2)
            distance = (float) Math.round(totalTrainingSteps * mDatabase.walkingModesDao().getWalkingStepLength()) / 1000;
        else
            distance = (float) Math.round(totalTrainingSteps * mDatabase.walkingModesDao().getRunningStepLength()) / 1000;

        //최신 Stopwatch 상태 반영하는 알림 현시
        final Notification notification;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            notification = mNotificationBuilder.build(mContext, mNotificationModel, stopwatch, distance);
            mNotificationBuilder.buildChannel(mContext, mNotificationManager);
            mNotificationManager.notify(mNotificationModel.getStopwatchNotificationId(), notification);
        }
    }

    /**
     * Local 이 변경되면 알림 갱신하는 class
     */
    private final class LocaleChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateNotification();
        }
    }
}
