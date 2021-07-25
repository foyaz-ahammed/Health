package org.secuso.privacyfriendlyactivitytracker.models;

/**
 * Stopwatch 또는 lap 의 변경사항을 알리는 interface
 */
public interface StopwatchListener {

    /**
     * @param before 갱신되기전 stopwatch 의 상태
     * @param after 갱신된 후의 stopwatch 상태
     */
    void stopwatchUpdated(Stopwatch before, Stopwatch after);

}