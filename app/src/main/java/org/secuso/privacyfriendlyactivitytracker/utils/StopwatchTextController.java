package org.secuso.privacyfriendlyactivitytracker.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.TextView;

import org.secuso.privacyfriendlyactivitytracker.R;

import static android.text.format.DateUtils.HOUR_IN_MILLIS;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;

/**
 * 제공된 시간을 millisecond 단위로 변형하여 Stopwatch 로 현시하는 Controller
 */
public final class StopwatchTextController {

    private final TextView mMainTextView;

    private long mLastTime = Long.MIN_VALUE;

    private static final long TEN_MINUTES = 10 * DateUtils.MINUTE_IN_MILLIS;
    private static final long HOUR = DateUtils.HOUR_IN_MILLIS;
    private static final long TEN_HOURS = 10 * HOUR;
    private static final long HUNDRED_HOURS = 100 * HOUR;

    public StopwatchTextController(TextView mainTextView) {
        mMainTextView = mainTextView;
    }

    /**
     * 시간형식 설정함수
     * @param accumulatedTime 축적된 시간
     */
    public void setTimeString(long accumulatedTime) {
        // 초단위로 현시하므로 milliseconds 단위에서 시간이 갱신되면 무시
        if ((mLastTime / 10) == (accumulatedTime / 10)) {
            return;
        }

        final int hours = (int) (accumulatedTime / HOUR_IN_MILLIS);
        int remainder = (int) (accumulatedTime % HOUR_IN_MILLIS);

        final int minutes = (int) (remainder / MINUTE_IN_MILLIS);
        remainder = (int) (remainder % MINUTE_IN_MILLIS);

        final int seconds = (int) (remainder / SECOND_IN_MILLIS);

        // Avoid unnecessary computations and garbage creation if seconds have not changed since
        // last layout pass.
        if ((mLastTime / SECOND_IN_MILLIS) != (accumulatedTime / SECOND_IN_MILLIS)) {
            final Context context = mMainTextView.getContext();
            final String time = context.getString(R.string.time_format1, hours, minutes, seconds);
            mMainTextView.setText(time);
        }
        mLastTime = accumulatedTime;
    }
}