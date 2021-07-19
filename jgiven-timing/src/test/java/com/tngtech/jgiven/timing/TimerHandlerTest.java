package com.tngtech.jgiven.timing;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.common.base.Stopwatch;
import com.google.common.testing.FakeTicker;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("UnstableApiUsage")
public class TimerHandlerTest {
    private Timer timerMock;
    private Stopwatch stopwatchMock;
    private Logger loggerMock;

    @Before
    public void setup() {
        timerMock = mock(Timer.class);
        loggerMock = mock(Logger.class);
        stopwatchMock = mock(Stopwatch.class);
        TimerHandler.setLogger(loggerMock);
    }

    @After
    public void teardown() {
        TimerHandler.setLogger(LoggerFactory.getLogger(TimerHandler.class));
    }

    @Test
    public void startTimer_starts_the_internal_timer() {
        TimerHandler.startTimer(timerMock);

        verify(timerMock, times(1)).start();
    }

    @Test
    public void startTimer_logs_the_event() {
        TimerHandler.startTimer(timerMock);

        verify(loggerMock).info(TimerHandler.TIMER_STARTED_MESSAGE);
    }

    @Test
    public void stopTimer_stops_the_internal_timer() {
        timerMock.setTimer(Stopwatch.createStarted(new FakeTicker()));

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(timerMock, times(1)).stop();
    }

    @Test
    public void stopTimer_gets_the_correct_duration_for_millis() {
        doReturn(15L).when(stopwatchMock).elapsed(TimeUnit.MILLISECONDS);
        doCallRealMethod().when(timerMock).setTimer(any());
        doCallRealMethod().when(timerMock).elapsed(any());
        timerMock.setTimer(stopwatchMock);

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(loggerMock).info(TimerHandler.TIMER_STOPPED_MESSAGE, 15L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void stopTimer_gets_the_correct_duration_for_micros() {
        doReturn(15L).when(stopwatchMock).elapsed(TimeUnit.MICROSECONDS);
        doCallRealMethod().when(timerMock).setTimer(any());
        doCallRealMethod().when(timerMock).elapsed(any());
        timerMock.setTimer(stopwatchMock);

        TimerHandler.stopAndPrintTimer(timerMock);

        verify(loggerMock).info(TimerHandler.TIMER_STOPPED_MESSAGE, 15L, TimeUnit.MICROSECONDS);
    }
}
