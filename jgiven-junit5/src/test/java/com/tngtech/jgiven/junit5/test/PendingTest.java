package com.tngtech.jgiven.junit5.test;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.junit5.SimpleScenarioTest;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;
import org.junit.jupiter.api.Test;

public class PendingTest extends SimpleScenarioTest<PendingTest.PendingTestSteps> {

    @Test
    @Pending
    public void required_does_not_fail_for_pending_scenarios() {
        when().some_action();
    }

    public static class PendingTestSteps {
        @ExpectedScenarioState
        String someState;

        public PendingTestSteps some_action() {
            assertThat(someState).isNotNull();
            return this;
        }
    }

}
