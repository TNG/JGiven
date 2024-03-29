package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;
import org.junit.Assume;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

@Description("Scenarios can have sections")
public class AssumptionTest extends SimpleScenarioTest<AssumptionTest.TestStage> {


    @Test
    public void should_pass_on_assertJ_assumptions() throws Throwable {
        assertThatThrownBy(() -> when().I_assume_something_using_assertJ())
                .isInstanceOf(catchException(AssumptionTest::assertJAssumptionFailure));
        getScenario().finished();
        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase(0);
        assertThat(aCase.getStep(0).getStatus()).isEqualTo(StepStatus.PASSED);
    }

    @Test
    public void should_pass_on_junit5_assumptions() throws Throwable {
        assertThatThrownBy(() -> when().I_assume_something_using_junit5())
                .isInstanceOf(catchException(AssumptionTest::junitAssumptionFailure));
        getScenario().finished();
        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase(0);
        assertThat(aCase.getStep(0).getStatus()).isEqualTo(StepStatus.PASSED);
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

    private Class<? extends Exception> catchException(Runnable runnable) {
        try {
            runnable.run();
            return null;
        } catch (Exception e) {
            return e.getClass();
        }
    }
}