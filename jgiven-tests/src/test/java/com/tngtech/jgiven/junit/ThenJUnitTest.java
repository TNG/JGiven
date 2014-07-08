package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.runner.Result;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class ThenJUnitTest<SELF extends ThenJUnitTest<?>> extends ThenReportModel<SELF> {
    @ProvidedScenarioState
    Result result;

    public void the_test_is_ignored() {
        // this is actually not correct, because it depends on the JUnit executor whether
        // a test is ignored if an AssumptionException is thrown.
        // The standard JUnit executor will report the test as passed and not ignored,
        // we thus only test for not failed here
        the_test_passes();
    }

    public void the_test_passes() {
        assertThat( result.getFailureCount() ).as( "failure count" ).isEqualTo( 0 );
    }

    public SELF the_test_fails() {
        assertThat( result.getFailureCount() ).as( "failure count" ).isGreaterThan( 0 );
        return self();
    }

    public void the_test_fails_with_message( String expectedMessage ) {
        the_test_fails();
        assertThat( result.getFailures().get( 0 ).getMessage() ).as( "failure message" ).isEqualTo( expectedMessage );
    }

}
