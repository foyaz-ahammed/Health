package org.secuso.privacyfriendlyactivitytracker.models;

import static org.secuso.privacyfriendlyactivitytracker.Utils.now;
import static org.secuso.privacyfriendlyactivitytracker.Utils.wallClock;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RESET;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RUNNING;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.PAUSED;

public final class Stopwatch {

    public enum State { RESET, RUNNING, PAUSED }

    static final long UNUSED = Long.MIN_VALUE;

    /** 재설정된 Stopwatch 의 변경불가능한 단일 instance */
    private static final Stopwatch RESET_STOPWATCH = new Stopwatch(RESET, UNUSED, UNUSED, 0);

    /** Stopwatch 의 현재상태. */
    private final State mState;

    /** Stopwatch 가 마지막으로 시작된 경과 시간(ms). */
    private final long mLastStartTime;

    /** Stopwatch 가 마지막으로 시작된 이후시간 */
    private final long mLastStartWallClockTime;

    /** Stopwatch가 실행중에 루적된 경과시간(ms) */
    private final long mAccumulatedTime;

    Stopwatch(State state, long lastStartTime, long lastWallClockTime, long accumulatedTime) {
        mState = state;
        mLastStartTime = lastStartTime;
        mLastStartWallClockTime = lastWallClockTime;
        mAccumulatedTime = accumulatedTime;
    }

    public State getState() { return mState; }
    public long getLastStartTime() { return mLastStartTime; }
    public long getLastWallClockTime() { return mLastStartWallClockTime; }
    public boolean isReset() { return mState == RESET; }
    public boolean isPaused() { return mState == PAUSED; }
    public boolean isRunning() { return mState == RUNNING; }

    /**
     * @return 현재 루적된 시간
     */
    public long getTotalTime() {
        if (mState != RUNNING) {
            return mAccumulatedTime;
        }

        // In practice, "now" can be any value due to device reboots. When the real-time clock
        // is reset, there is no more guarantee that "now" falls after the last start time. To
        // ensure the stopwatch is monotonically increasing, normalize negative time segments to 0,
        final long timeSinceStart = now() - mLastStartTime;
        return mAccumulatedTime + Math.max(0, timeSinceStart);
    }

    /**
     * @return 마지막으로 Stopwatch 가 시작된때로부터 루적된 시간
     */
    public long getAccumulatedTime() {
        return mAccumulatedTime;
    }

    /**
     * Stopwatch 시작함수
     * @return 실행중인 Stopwatch
     */
    Stopwatch start() {
        if (mState == RUNNING) {
            return this;
        }

        return new Stopwatch(RUNNING, now(), wallClock(), getTotalTime());
    }

    /**
     * Stopwatch 중지
     * @return 중지된 Stopwatch
     */
    Stopwatch pause() {
        if (mState != RUNNING) {
            return this;
        }

        return new Stopwatch(PAUSED, UNUSED, UNUSED, getTotalTime());
    }

    /**
     * Stopwatch 재설정
     * @return 재설정된 Stopwatch
     */
    Stopwatch reset() {
        return RESET_STOPWATCH;
    }
}