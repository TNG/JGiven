package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;
import org.assertj.core.api.Assertions;
import org.junit.AssumptionViolatedException;
import org.junit.Ignore;
import org.junit.Test;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;

public class PendingTest extends SimpleScenarioTest<PendingTest.PendingTestSteps> {

    @Test
    @Ignore("Currently fails because the required attribute is set")
    @Pending
    public void required_does_not_fail_for_pending_scenarios() throws Throwable {
        when().some_action();
    }

    public static class PendingTestSteps {
        @ExpectedScenarioState(required = true)
        String someState;

        public void some_action() {
            assertThat(someState).isNotNull();
        }
    }

}
