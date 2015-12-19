package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.StepFunction;

public class ExceptionReportingTest extends SimpleScenarioTest<ExceptionReportingTest.TestSteps> {

    @Test
    public void exception_type_is_contained_in_the_report() throws Throwable {
        when().$( "Something", new StepFunction<TestSteps>() {
            @Override
            public void apply( TestSteps testSteps ) throws Exception {
                throw new IllegalStateException( "failing state for testing" );
            }
        } );

        try {
            getScenario().finished();
        } catch( IllegalStateException ignore ) {}

        String errorMessage = getScenario().getScenarioModel().getCase( 0 ).getErrorMessage();
        assertThat( errorMessage ).isEqualTo( "java.lang.IllegalStateException: failing state for testing" );
    }

    public static class TestSteps extends Stage<TestSteps> {

    }
}
