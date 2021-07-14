package com.tngtech.jgiven.timing;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

/**
 * Utility class that is used by the agents to operate on the timers at the beginning and end
 * of the test framework lifecycle.
 */
public class ManageTimerInterceptor {
    /**
     * Helper object for seeing if current method was the first one to call finish on the scenario,
     * so we can stop the timer at the very last step.
     */
    private static final ThreadLocal<Boolean> wasTimerStoppedAttempted = new ThreadLocal<Boolean>();

    private static void attemptToStartTimer() {
        if (!TimerConfig.isTimerStarted()) {
            TimerHandler.startTimer(TimerConfig.getTimer());
            TimerConfig.setIsTimerStarted(true);
        }
    }

    private static void attemptToStopTimer(boolean wasMethodTheFirstToCallFinish) {
        if (TimerConfig.isTimerStarted() && wasMethodTheFirstToCallFinish) {
            TimerHandler.stopAndPrintTimer(TimerConfig.getTimer());
            TimerConfig.setIsTimerStarted(false);
            wasTimerStoppedAttempted.set(false);
        }
    }

    private static Object manageTimerInitialization(Callable<?> callable) throws Exception {
        attemptToStartTimer();
        return callable.call();
    }

    /**
     * Used to be sure we wait for all calls to super class finish methods to terminate
     * before actually stopping the timer.
     */
    private static boolean getTraceOfCalls() {
        if (wasTimerStoppedAttempted.get() == null || wasTimerStoppedAttempted.get() == false) {
            wasTimerStoppedAttempted.set(true);
            return true;
        }
        return false;
    }

    private static Object manageTimerPrinting(Callable<?> callable) throws Exception {
        boolean wasMethodTheFirstToCallFinish = getTraceOfCalls();
        Object toBeReturned = callable.call();
        attemptToStopTimer(wasMethodTheFirstToCallFinish);

        return toBeReturned;
    }

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        if (method.getName().contains("initialize")) {
            return manageTimerInitialization(callable);
        }

        return manageTimerPrinting(callable);
    }

}
