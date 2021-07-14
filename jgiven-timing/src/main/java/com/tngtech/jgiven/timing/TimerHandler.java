package com.tngtech.jgiven.timing;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for doing operations on a given Timer.
 */
public class TimerHandler {
    protected static void startTimer(Timer currentTimer) {
        TimerConfig.timingLogger.info("The timer has been started");
        currentTimer.start();
    }

    protected static void stopAndPrintTimer(Timer currentTimer) {
        currentTimer.stop();
        long duration = 0L;
        TimeUnit validTimeUnit = TimeUnit.MICROSECONDS;

        if (currentTimer.getInnerTimer().elapsed(TimeUnit.MILLISECONDS) < 10) {
            duration = currentTimer.getInnerTimer().elapsed(TimeUnit.MICROSECONDS);
            validTimeUnit = TimeUnit.MICROSECONDS;
        } else {
            duration = currentTimer.getInnerTimer().elapsed(TimeUnit.MILLISECONDS);
        }
        TimerConfig.timingLogger.info("The current test took " + duration + " "
              + validTimeUnit + " to execute");
    }
}
