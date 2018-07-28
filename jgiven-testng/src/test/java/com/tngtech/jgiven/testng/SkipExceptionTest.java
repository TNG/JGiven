package com.tngtech.jgiven.testng;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.StepStatus;
import org.testng.SkipException;
import org.testng.annotations.Test;

@Description( "SkipException are handled correctly" )
public class SkipExceptionTest extends SimpleScenarioTest<TestNgTest.TestSteps> {

    @Test
    public void TestNG_skipped_exceptions_should_be_treated_correctly() throws Throwable {
        try {
            given().skipped_exception_is_thrown();
            Assertions.fail( "SkipException should have been thrown" );
        } catch( SkipException e ) {}

        getScenario().finished();

        ScenarioCaseModel aCase = getScenario().getModel().getLastScenarioModel().getCase( 0 );
        assertThat( aCase.getStep( 0 ).getStatus() ).isEqualTo( StepStatus.PASSED );
    }

}
