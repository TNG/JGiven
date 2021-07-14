package com.tngtech.jgiven.timing;

import org.junit.Assert;
import org.junit.Test;

public class TimerConfigTest {
    @Test
    public void getTimer_creates_a_new_timer_if_not_present() {
        Assert.assertNotNull(TimerConfig.getTimer());
    }

    @Test
    public void getTimer_returns_the_same_timer_after_multiple_calls() {
        Timer currentTimer = TimerConfig.getTimer();
        Timer theSameTimer = TimerConfig.getTimer();
        Assert.assertSame(currentTimer, theSameTimer);
    }
}
