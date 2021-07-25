package org.secuso.privacyfriendlyactivitytracker.receivers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import org.secuso.privacyfriendlyactivitytracker.R;
import org.secuso.privacyfriendlyactivitytracker.Utils;
import org.secuso.privacyfriendlyactivitytracker.persistence.MeasureDatabase;
import org.secuso.privacyfriendlyactivitytracker.persistence.Step;

import java.util.Calendar;
import java.util.List;

/**
 * 매일 알림을 위한 receiver
 */
public class DailyNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long when = System.currentTimeMillis();

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "healthStepNotification";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "stepNotification", NotificationManager.IMPORTANCE_HIGH);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        //자료기지에서 오늘 걸음수와 카로리 얻기
        Calendar calendar = Calendar.getInstance();
        MeasureDatabase db = MeasureDatabase.getInstance(context);
        List<Step> todayStepData = db.stepCountDao().getDayData(Utils.getIntDate(calendar));
        int todaySteps = db.stepCountDao().getTotalStepsByDate(Utils.getIntDate(calendar));
        float walkingDistance = 0, runningDistance = 0;
        float walkingLength = db.walkingModesDao().getWalkingStepLength();
        float runningLength = db.walkingModesDao().getRunningStepLength();
        for (int i = 0; i < todayStepData.size(); i ++) {
            if (todayStepData.get(i).getWalkingMode() == 1)
                walkingDistance += todayStepData.get(i).getStepCount() * walkingLength;
            else runningDistance += todayStepData.get(i).getStepCount() * runningLength;
        }

        final RemoteViews content = new RemoteViews(context.getPackageName(), R.layout.daily_notif_content);
        content.setTextViewText(R.id.step, context.getString(R.string.with_steps, todaySteps));
        content.setTextViewText(R.id.calorie, context.getString(R.string.with_calories, Math.round(walkingDistance / 1000 * 30 + runningDistance / 1000 * 60)));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_health_active)
                .setContent(content)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_HIGH)
                .setVibrate(new long[0])
                .setAutoCancel(true).setWhen(when);
        notificationManager.notify(0, builder.build());
    }
}
