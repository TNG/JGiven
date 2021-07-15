package com.tngtech.jgiven.timing;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.base.Stopwatch;
import com.google.common.testing.FakeTicker;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimerHandlerTest {
    private Timer timerMock;
    private Stopwatch stopwatchMock;
    private Logger loggerMock;

    @Before
    public void setup() {
        timerMock = Mockito.mock(Timer.class);
        loggerMock = Mockito.mock(Logger.class);
        stopwatchMock = Mockito.mock(Stopwatch.class);
        TimerConfig.setLogger(loggerMock);
    }

    @After
    public void teardown() {
        TimerConfig.setLogger(LoggerFactory.getLogger(TimerConfig.class));
    }

    @Test
    public void startTimer_starts_the_internal_timer() {
        TimerHandler.startTimer(timerMock);

        verify(timerMock, times(1)).start();
    }

    @Test
    public void startTimer_logs_the_event() {
        TimerHandler.startTimer(timerMock);

        verify(loggerMock).info("The timer has been started");
    }

    @Test
    public void stopTimer_stops_the_internal_timer() {
        doReturn(Stopwatch.createStarted(new FakeTicker())).when(timerMock).getInnerTimer();

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(timerMock, times(1)).stop();
    }

    @Test
    public void stopTimer_calls_get_inner_timer_twice_if_less_than_10_millis() {
        FakeTicker ticker = new FakeTicker().advance(7, TimeUnit.MILLISECONDS);
        Stopwatch stopwatch = Stopwatch.createStarted(ticker);
        doReturn(stopwatch).when(timerMock).getInnerTimer();

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(timerMock, times(2)).getInnerTimer();
    }

    @Test
    public void stopTimer_calls_get_inner_timer_twice_if_more_than_10_millis() {
        FakeTicker ticker = new FakeTicker().advance(11, TimeUnit.MILLISECONDS);
        Stopwatch stopwatch = Stopwatch.createStarted(ticker);
        doReturn(stopwatch).when(timerMock).getInnerTimer();

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(timerMock, times(2)).getInnerTimer();
    }

    @Test
    public void stopTimer_gets_the_correct_duration_for_millis() {
        doReturn(15L).when(stopwatchMock).elapsed(TimeUnit.MILLISECONDS);
        doReturn(stopwatchMock).when(timerMock).getInnerTimer();

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(stopwatchMock, times(2)).elapsed(TimeUnit.MILLISECONDS);
        verify(loggerMock).info("The current test took 15 MILLISECONDS to execute");
    }

    @Test
    public void stopTimer_gets_the_correct_duration_for_micros() {
        TimerConfig.setLogger(loggerMock);
        doReturn(15L).when(stopwatchMock).elapsed(TimeUnit.MICROSECONDS);
        doReturn(stopwatchMock).when(timerMock).getInnerTimer();

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(stopwatchMock, times(1)).elapsed(TimeUnit.MILLISECONDS);
        verify(stopwatchMock, times(1)).elapsed(TimeUnit.MICROSECONDS);
        verify(loggerMock).info("The current test took 15 MICROSECONDS to execute");
    }
}
