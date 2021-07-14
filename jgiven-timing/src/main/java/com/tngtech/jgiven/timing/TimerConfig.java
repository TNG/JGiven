package com.tngtech.jgiven.timing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for managing the timers for each individual thread.
 */
public class TimerConfig {
    private static final ThreadLocal<Timer> currentThreadTimer = new ThreadLocal<Timer>();
    private static final ThreadLocal<Boolean> isTimerStarted = new ThreadLocal<Boolean>();
    protected static Logger timingLogger = LoggerFactory.getLogger(TimerConfig.class);

    /**
     * Method for creating and returning a Timer object associated with the current thread.
     */
    protected static Timer getTimer() {
        if (currentThreadTimer.get() == null) {
            currentThreadTimer.set(new Timer());
        }
        return currentThreadTimer.get();
    }

    protected static void setIsTimerStarted(boolean isTimerStarted) {
        TimerConfig.isTimerStarted.set(isTimerStarted);
    }

    protected static boolean isTimerStarted() {
        if (TimerConfig.isTimerStarted.get() == null) {
            setIsTimerStarted(false);
        }
        return TimerConfig.isTimerStarted.get();
    }
}
