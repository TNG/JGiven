package com.tngtech.jgiven.timing;

import java.util.concurrent.TimeUnit;

/**
 * Utility class for doing operations on a given Timer.
 */
public class TimerHandler {
    public static void startTimer(Timer currentTimer) {
        System.out.println("Timer has been started");
        currentTimer.start();
    }

    public static void stopAndPrintTimer(Timer currentTimer) {
        currentTimer.stop();
        long duration = 0L;
        TimeUnit validTimeUnit = TimeUnit.MICROSECONDS;

        if (currentTimer.getInnerTimer().elapsed(TimeUnit.MILLISECONDS) < 10) {
            duration = currentTimer.getInnerTimer().elapsed(TimeUnit.MICROSECONDS);
            validTimeUnit = TimeUnit.MICROSECONDS;
        } else {
            duration = currentTimer.getInnerTimer().elapsed(TimeUnit.MILLISECONDS);
        }
        System.out.println("The current test took " + duration + " " + validTimeUnit.toString() + " to execute");
    }
}
