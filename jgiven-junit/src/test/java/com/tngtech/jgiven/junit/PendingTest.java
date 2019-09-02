package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.report.model.ExecutionStatus;
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

    @ScenarioStage
    PendingTest.PendingTestStepsWithRequiredField pendingTestStepsWithRequiredField;

    @Test
    @Pending
    public void required_does_not_fail_for_pending_scenarios() {
        pendingTestStepsWithRequiredField.some_action();

        assertThat( getScenario().getScenarioCaseModel().getExecutionStatus() ).isEqualTo(ExecutionStatus.SCENARIO_PENDING);
    }

    @Test
    @Pending(executeSteps = true)
    public void failing_steps_are_reported_as_pending_if_execute_steps_is_true() {
        when().some_failing_action();

        assertThat( getScenario().getScenarioCaseModel().getExecutionStatus() ).isEqualTo(ExecutionStatus.SCENARIO_PENDING);
    }

    @Test
    public void failing_steps_are_reported_as_pending_if_execute_steps_is_true_on_step_method() {
        when().some_failing_action_with_pending_annotation();

        assertThat( getScenario().getScenarioCaseModel().getExecutionStatus() ).isEqualTo(ExecutionStatus.SCENARIO_PENDING);
    }

    @Test
    @Pending(failIfPass = true)
    public void failing_tests_with_failIfPass_are_reported_as_pending() {
        when().some_failing_action();

        assertThat( getScenario().getScenarioCaseModel().getExecutionStatus() ).isEqualTo(ExecutionStatus.SCENARIO_PENDING);
    }

    public static class PendingTestStepsWithRequiredField {
        @ExpectedScenarioState(required = true)
        String someState;

        public PendingTestStepsWithRequiredField some_action() {
            return this;
        }
    }

    public static class PendingTestSteps {
        @ExpectedScenarioState
        String someState;

        public PendingTestSteps some_failing_action() {
            assertThat(someState).isNotNull();
            return this;
        }

        @Pending(executeSteps = true)
        public PendingTestSteps some_failing_action_with_pending_annotation() {
            assertThat(someState).isNotNull();
            return this;
        }

    }

}
