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
public class Interceptor {
    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) {
        if (method.getName().contains("initialize")) {
            if (!TimerConfig.isTimerInjected()) {
                TimerHandler.startTimer(TimerConfig.getTimer());
                TimerConfig.setIsTimerInjected(true);
            }
            try {
                return callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }  else {
            try {
                return callable.call();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (TimerConfig.isTimerInjected()) {
                    TimerHandler.stopAndPrintTimer(TimerConfig.getTimer());
                    TimerConfig.setIsTimerInjected(false);
                }
            }
        }
        return null;
    }

}
