package com.tngtech.jgiven.timing;

import com.google.common.base.Stopwatch;

/**
 * A wrapper class for a timer.
 */
public class Timer {
    private Stopwatch timer;

    public void reset() {
        this.timer.reset();
    }

    public void start() {
        this.timer = Stopwatch.createStarted();
    }

    public void stop() {
        timer.stop();
    }

    public Stopwatch getInnerTimer() {
        return this.timer;
    }
}
