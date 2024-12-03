package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;
import org.junit.Assume;
import org.junit.AssumptionViolatedException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@Description("Scenarios can have assumptions")
public class AssumptionTest extends SimpleScenarioTest<AssumptionTest.TestStage> {


    @Test
    public void should_report_aborted_on_assertJ_assumptions() throws Throwable {
        when().I_assume_something_using_assertJ();
        getScenario().finished();

        assertThat(getScenario().getExecutor().hasAborted()).isTrue();
        assertThat(getScenario().getExecutor().getAbortedException()).isInstanceOf(AssumptionViolatedException.class);
        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase(0);
        assertThat(aCase.getStep(0).getStatus()).isEqualTo(StepStatus.ABORTED);
    }

    @Test
    public void should_report_aborted_on_junit_assumptions() throws Throwable {
        when().I_assume_something_using_junit5();
        getScenario().finished();

        assertThat(getScenario().getExecutor().hasAborted()).isTrue();
        assertThat(getScenario().getExecutor().getAbortedException()).isInstanceOf(org.junit.AssumptionViolatedException.class);
        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase(0);
        assertThat(aCase.getStep(0).getStatus()).isEqualTo(StepStatus.ABORTED);
    }

    static class TestStage {
        void I_assume_something_using_assertJ() {
            assertJAssumptionFailure();
        }

        void I_assume_something_using_junit5() {
            junitAssumptionFailure();
        }
    }

    private static void assertJAssumptionFailure() {
        assumeThat(true).isFalse();
    }

    @SuppressWarnings("DataFlowIssue")//we want to provoke an assumption failure
    private static void junitAssumptionFailure() {
        Assume.assumeTrue(false);
    }
}
