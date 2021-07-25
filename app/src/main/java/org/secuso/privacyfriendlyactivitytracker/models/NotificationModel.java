package org.secuso.privacyfriendlyactivitytracker.models;

/**
 * 운동측정 알림창을 위한 model
 */
public final class NotificationModel {

    private boolean mApplicationInForeground;

    /**
     * Application 이 background 혹은 foreground 상태임을 보관하는 함수
     * @param inForeground true 이면 application 이 foreground 상태
     */
    public void setApplicationInForeground(boolean inForeground) {
        mApplicationInForeground = inForeground;
    }

    /**
     * 현재 application 의 foreground 상태를 얻는 함수
     * @return true 이면 application 이 foreground 상태
     */
    public boolean isApplicationInForeground() {
        return mApplicationInForeground;
    }

    /**
     * Stopwatch 의 알림 id 를 얻는 함수
     * @return Stopwatch 알림을 식별하는 값
     */
    int getStopwatchNotificationId() {
        return Integer.MAX_VALUE - 1;
    }

    /**
     * Stopwatch 알림을 위한 group key 를 얻는 함수
     * @return Stopwatch 알림의 group key
     */
    String getStopwatchNotificationGroupKey() {
        return "3";
    }
}
