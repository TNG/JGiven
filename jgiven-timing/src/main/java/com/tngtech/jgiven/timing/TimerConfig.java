package com.tngtech.jgiven.timing;

/**
 * Helper class for managing the timers for each individual thread.
 */
public class TimerConfig {
    private static final ThreadLocal<Timer> currentThreadTimer = new ThreadLocal<>();

    /**
     * Method for creating and returning a Timer object associated with the current thread.
     */
    protected static Timer getTimer() {
        if (currentThreadTimer.get() == null) {
            currentThreadTimer.set(new Timer());
        }
        return currentThreadTimer.get();
    }

    protected static void resetTimer() {
        currentThreadTimer.set(null);
    }
}
