package com.tngtech.jgiven.timing;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class ManageTimerInterceptorTest {

    Method methodMock;
    MyCallable callableMock;

    public void initialize() {

    }

    public void finished() {

    }

    @Before
    public void setup() throws NoSuchMethodException {
        methodMock = this.getClass().getDeclaredMethod("initialize");
        callableMock = new MyCallable();
        TimerConfig.setIsTimerStarted(false);
        TimerConfig.resetTimer();
        ManageTimerInterceptor.wasTimerStoppedAttempted.set(false);
        TimerConfig.setLogger(LoggerFactory.getLogger(TimerConfig.class));
    }

    @After
    public void teardown() {
        TimerConfig.setIsTimerStarted(false);
        TimerConfig.resetTimer();
        ManageTimerInterceptor.wasTimerStoppedAttempted.set(false);
    }

    @Test
    public void first_method_starts_the_timer() throws Exception {
        ManageTimerInterceptor.intercept(methodMock, callableMock);

        Assert.assertTrue(TimerConfig.isTimerStarted());
    }

    @Test
    public void first_method_sets_the_desire_to_stop_the_timer() throws Exception {
        methodMock = this.getClass().getMethod("finished");

        ManageTimerInterceptor.intercept(methodMock, callableMock);

        Assert.assertTrue(ManageTimerInterceptor.wasTimerStoppedAttempted.get());
    }

    @Test
    public void first_method_stops_the_timer_if_it_was_started() throws Exception {
        TimerConfig.setIsTimerStarted(true);
        TimerConfig.getTimer().start();
        methodMock = this.getClass().getMethod("finished");

        ManageTimerInterceptor.intercept(methodMock, callableMock);

        Assert.assertFalse(ManageTimerInterceptor.wasTimerStoppedAttempted.get());
    }

    @Test
    public void subclass_method_does_not_stop_the_timer() throws Exception {
        ManageTimerInterceptor.wasTimerStoppedAttempted.set(true);
        methodMock = this.getClass().getMethod("finished");

        ManageTimerInterceptor.intercept(methodMock, callableMock);

        Assert.assertTrue(ManageTimerInterceptor.wasTimerStoppedAttempted.get());
    }

    private static class MyCallable implements Callable<Object> {
        @Override
        public Object call() throws Exception {
            return null;
        }
    }
}
