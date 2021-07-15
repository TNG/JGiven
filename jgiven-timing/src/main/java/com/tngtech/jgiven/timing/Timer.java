package com.tngtech.jgiven.timing;

import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

/**
 * A wrapper class for a timer.
 */
public class Timer {
    private Stopwatch timer;
    private boolean isTimerStarted = false;

    void setTimer(Stopwatch givenStopwatch) {
        this.timer = givenStopwatch;
        this.isTimerStarted = true;
    }

    void start() {
        this.timer = Stopwatch.createStarted();
        this.isTimerStarted = true;
    }

    void stop() {
        timer.stop();
        this.isTimerStarted = false;
    }

    boolean getIsTimerStarted() {
        return this.isTimerStarted;
    }

    long elapsed(TimeUnit givenTimeUnit) {
        return this.timer.elapsed(givenTimeUnit);
    }
}
