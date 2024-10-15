package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

class AssumptionsTest extends SimpleScenarioTest<AssumptionsTest.TestStage> {


    @Test
    void should_pass_on_assertJ_assumptions() throws Throwable {
        when().I_assume_something_using_assertJ();
        getScenario().finished();

        assertThat(getScenario().getExecutor().hasAborted()).isTrue();
        assertThat(getScenario().getExecutor().getAbortedException()).isInstanceOf(org.junit.AssumptionViolatedException.class);
        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase(0);
        assertThat(aCase.getStep(0).getStatus()).isEqualTo(StepStatus.ABORTED);
    }

    @Test
    void should_pass_on_junit5_assumptions() throws Throwable {
        when().I_assume_something_using_junit5();
        getScenario().finished();

        assertThat(getScenario().getExecutor().hasAborted()).isTrue();
        assertThat(getScenario().getExecutor().getAbortedException()).isInstanceOf(org.opentest4j.TestAbortedException.class);
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

    private static void assertJAssumptionFailure(){
        assumeThat( true ).isFalse();
    }

    private static void junitAssumptionFailure(){
        Assumptions.abort();
    }
}
