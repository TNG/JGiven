package com.tngtech.jgiven.junit5.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;

@Pending
public class PendingOnClassTest extends SimpleScenarioTest<PendingOnClassTest.PendingTestSteps> {

    @Test
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
