package org.secuso.privacyfriendlyactivitytracker.models;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.Builder;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.widget.RemoteViews;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.services.StopwatchService;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Stopwatch 와 기록된 lap 의 최신상태를 반영하는 알림설정 class
 */
class StopwatchNotificationBuilder {

    /**
     * 모든 Stopwatch 알림을 포함하는 channel
     */
    private static final String STOPWATCH_NOTIFICATION_CHANNEL_ID = "HealthTrainingDurationNotification";

    public void buildChannel(Context context, NotificationManagerCompat notificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    STOPWATCH_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.training_duration),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Notification build(Context context, NotificationModel nm, Stopwatch stopwatch, float distance) {
        @StringRes final int eventLabel = R.string.notification_label;

        // Intent to load the app when the notification is tapped.
        final Intent showApp = new Intent(context, StopwatchService.class)
                .setAction(StopwatchService.ACTION_SHOW_STOPWATCH);

        final PendingIntent pendingShowApp = PendingIntent.getService(context, 0, showApp,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_UPDATE_CURRENT);

        // Compute some values required below.
        final boolean running = stopwatch.isRunning();
        final String pname = context.getPackageName();
        final Resources res = context.getResources();
        final long base = SystemClock.elapsedRealtime() - stopwatch.getTotalTime();

        final RemoteViews content = new RemoteViews(pname, R.layout.chronometer_notif_content);
        content.setChronometer(R.id.chronometer, base, null, running);
        content.setTextViewText(R.id.distance, context.getString(R.string.with_kilometer, distance >= 0 ? distance : 0f));

        final Intent pause = new Intent(context, StopwatchService.class)
                .setAction(StopwatchService.ACTION_PAUSE_STOPWATCH);
        final PendingIntent pausePendingIntent = PendingIntent.getService(context, 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
        content.setOnClickPendingIntent(R.id.pause, pausePendingIntent);

        final Intent start = new Intent(context, StopwatchService.class)
                .setAction(StopwatchService.ACTION_START_STOPWATCH);
        final PendingIntent startPendingIntent = PendingIntent.getService(context, 0, start, PendingIntent.FLAG_UPDATE_CURRENT);
        content.setOnClickPendingIntent(R.id.start, startPendingIntent);

        if (running) {
            content.setViewVisibility(R.id.start, GONE);
            content.setViewVisibility(R.id.pause, VISIBLE);
            content.setViewVisibility(R.id.paused, GONE);
        } else {
            content.setViewVisibility(R.id.start, VISIBLE);
            content.setViewVisibility(R.id.pause, GONE);
            content.setViewVisibility(R.id.paused, VISIBLE);
        }

        final Builder notification = new NotificationCompat.Builder(
                context, STOPWATCH_NOTIFICATION_CHANNEL_ID)
                .setLocalOnly(true)
                .setOngoing(running)
                .setCustomContentView(content)
                .setContentIntent(pendingShowApp)
                .setAutoCancel(stopwatch.isPaused())
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setSmallIcon(R.drawable.ic_health_active)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setColor(ContextCompat.getColor(context, R.color.white));

        notification.setGroup(nm.getStopwatchNotificationGroupKey());

        return notification.build();
    }
}
