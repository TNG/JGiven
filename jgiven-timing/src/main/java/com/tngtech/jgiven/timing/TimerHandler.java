package com.tngtech.jgiven.timing;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for doing operations on a given Timer.
 */
public class TimerHandler {
    @VisibleForTesting
    static final String TIMER_STARTED_MESSAGE = "The timer has been started";
    @VisibleForTesting
    static final String TIMER_STOPPED_MESSAGE = "The current test took {} {} to execute";

    protected static Logger timingLogger = LoggerFactory.getLogger(TimerHandler.class);

    protected static void startTimer(Timer currentTimer) {
        timingLogger.info(TIMER_STARTED_MESSAGE);
        currentTimer.start();
    }

    protected static void stopAndPrintTimer(Timer currentTimer) {
        currentTimer.stop();
        long duration = 0L;
        TimeUnit validTimeUnit = TimeUnit.MILLISECONDS;

        if (currentTimer.elapsed(TimeUnit.MILLISECONDS) < 10) {
            duration = currentTimer.elapsed(TimeUnit.MICROSECONDS);
            validTimeUnit = TimeUnit.MICROSECONDS;
        } else {
            duration = currentTimer.elapsed(TimeUnit.MILLISECONDS);
        }
        timingLogger.info(TIMER_STOPPED_MESSAGE, duration, validTimeUnit);
    }

    protected static void setLogger(Logger givenLogger) {
        timingLogger = givenLogger;
    }
}
