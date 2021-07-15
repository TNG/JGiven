package com.tngtech.jgiven.timing;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

public class TimerConfigTest {
    @After
    public void teardown() {
        TimerConfig.resetTimer();
    }

    @Test
    public void getTimer_creates_a_new_timer_if_not_present() {
        TimerConfig.resetTimer();
        assertThat(TimerConfig.getTimer()).isNotNull();
    }

    @Test
    public void getTimer_returns_the_same_timer_after_multiple_calls() {
        Timer currentTimer = TimerConfig.getTimer();
        Timer theSameTimer = TimerConfig.getTimer();
        assertThat(currentTimer).isSameAs(theSameTimer);
    }
}
