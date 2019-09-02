package com.tngtech.jgiven.testng;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.report.model.ExecutionStatus;
import org.assertj.core.api.Assertions;
import org.testng.SkipException;
import org.testng.annotations.Test;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;

@Description( "Pending annotation is handled correctly" )
public class PendingTest extends SimpleScenarioTest<TestNgTest.TestSteps> {

    @Test
    @Pending
    public void pending_annotation_should_catch_exceptions() {
        given().something();
        when().something_fails();
        then().nothing_happens();

        ScenarioCaseModel aCase = getScenario().getScenarioCaseModel();
        assertThat( aCase.getExecutionStatus() ).isEqualTo( ExecutionStatus.SCENARIO_PENDING );
    }

    @Test
    @Pending(executeSteps = true)
    public void pending_annotation_should_catch_exceptions_when_executing_steps() {
        given().something();
        when().something_fails();
        then().nothing_happens();

        ScenarioCaseModel aCase = getScenario().getScenarioCaseModel();
        assertThat( aCase.getExecutionStatus() ).isEqualTo( ExecutionStatus.SCENARIO_PENDING );
    }

    @Test
    public void pending_annotation_on_failing_steps_should_catch_exceptions() {
        given().something();
        when().something_fails_with_pending_annotation();
        then().nothing_happens();

        ScenarioCaseModel aCase = getScenario().getScenarioCaseModel();
        assertThat( aCase.getExecutionStatus() ).isEqualTo( ExecutionStatus.SOME_STEPS_PENDING );
    }

}
