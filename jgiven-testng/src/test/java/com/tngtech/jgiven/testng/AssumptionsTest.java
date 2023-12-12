package com.tngtech.jgiven.testng;

import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;
import org.testng.SkipException;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assumptions.assumeThat;

@Test(singleThreaded = true)
public class AssumptionsTest extends SimpleScenarioTest<AssumptionsTest.TestStage> {


    @Test
    public void should_pass_on_assertJ_assumptions() throws Throwable {
        assertThatThrownBy(() -> when().I_assume_something_using_assertJ())
                .isInstanceOf(catchException(AssumptionsTest::assertJAssumptionFailure));
        getScenario().finished();
        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase( 0 );
        assertThat( aCase.getStep( 0 ).getStatus() ).isEqualTo( StepStatus.PASSED );
    }

    @Test
    public void should_pass_on_junit5_assumptions() throws Throwable {
        assertThatThrownBy(() -> when().I_assume_something_using_junit5())
                .isInstanceOf(catchException(AssumptionsTest::testNgAssumptionFailure));
        getScenario().finished();
        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase( 0 );
        assertThat( aCase.getStep( 0 ).getStatus() ).isEqualTo( StepStatus.PASSED );
    }

    static class TestStage {
        void I_assume_something_using_assertJ() {
            assertJAssumptionFailure();
        }

        void I_assume_something_using_junit5() {
            testNgAssumptionFailure();
        }
    }

    private static void assertJAssumptionFailure(){
        assumeThat( true ).isFalse();
    }

    private static void testNgAssumptionFailure(){
        throw new SkipException("TestNG assumption failure");
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
