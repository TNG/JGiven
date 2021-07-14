package com.tngtech.jgiven.timing;

/**
 * Helper class for managing the timers for each individual thread.
 */
public class TimerConfig {
    private static final ThreadLocal<Timer> currentThreadTimer = new ThreadLocal<Timer>();
    private static boolean isTimerInjected = false;

    /**
     * Method for creating and returning a Timer object associated with the current thread.
     */
    public static Timer getTimer() {
        if (currentThreadTimer.get() == null) {
            currentThreadTimer.set(new Timer());
        }
        return currentThreadTimer.get();
    }

    public static void setIsTimerInjected(boolean isTimerInjected) {
        TimerConfig.isTimerInjected = isTimerInjected;
    }

    public static boolean isTimerInjected() {
        return isTimerInjected;
    }
}
