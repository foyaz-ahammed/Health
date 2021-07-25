package org.secuso.privacyfriendlyactivitytracker.models;

import android.content.SharedPreferences;

import org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State;
import static org.secuso.privacyfriendlyactivitytracker.models.Stopwatch.State.RESET;

final class StopwatchDAO {

    /** Stopwatch 의 상태를 저장하는 key */
    private static final String STATE = "sw_state";

    /** Stopwatch 의 마지막시작시간을 저장하는 key. */
    private static final String LAST_START_TIME = "sw_start_time";

    /** Stopwatch 가 마지막으로 시작된 epoch time 을 저장하는 key */
    private static final String LAST_WALL_CLOCK_TIME = "sw_wall_clock_time";

    /** Stopwatch 의 루적된 경과시간을 저장하는 key */
    private static final String ACCUMULATED_TIME = "sw_accum_time";

    private StopwatchDAO() {}

    /**
     * @return 영구적인 저장소에서부터 불러온 Stopwatch 또는 존재하지 않는 경우 재설정된 Stopwatch
     */
    static Stopwatch getStopwatch(SharedPreferences prefs) {
        final int stateIndex = prefs.getInt(STATE, RESET.ordinal());
        final State state = State.values()[stateIndex];
        final long lastStartTime = prefs.getLong(LAST_START_TIME, Stopwatch.UNUSED);
        final long lastWallClockTime = prefs.getLong(LAST_WALL_CLOCK_TIME, Stopwatch.UNUSED);
        final long accumulatedTime = prefs.getLong(ACCUMULATED_TIME, 0);
        Stopwatch s = new Stopwatch(state, lastStartTime, lastWallClockTime, accumulatedTime);

        // Stopwatch 가 부정확한 시간을 알리는 경우, 자료삭제
        if (s.getTotalTime() < 0) {
            s = s.reset();
            setStopwatch(prefs, s);
        }
        return s;
    }

    /**
     * Stopwatch 설정함수
     * @param stopwatch Stopwatch 의 마지막상태
     */
    static void setStopwatch(SharedPreferences prefs, Stopwatch stopwatch) {
        final SharedPreferences.Editor editor = prefs.edit();

        if (stopwatch.isReset()) {
            editor.remove(STATE)
                    .remove(LAST_START_TIME)
                    .remove(LAST_WALL_CLOCK_TIME)
                    .remove(ACCUMULATED_TIME);
        } else {
            editor.putInt(STATE, stopwatch.getState().ordinal())
                    .putLong(LAST_START_TIME, stopwatch.getLastStartTime())
                    .putLong(LAST_WALL_CLOCK_TIME, stopwatch.getLastWallClockTime())
                    .putLong(ACCUMULATED_TIME, stopwatch.getAccumulatedTime());
        }

        editor.apply();
    }
}
