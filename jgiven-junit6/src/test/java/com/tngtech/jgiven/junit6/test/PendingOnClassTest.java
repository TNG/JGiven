package com.tngtech.jgiven.junit6.test;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.junit6.SimpleScenarioTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
