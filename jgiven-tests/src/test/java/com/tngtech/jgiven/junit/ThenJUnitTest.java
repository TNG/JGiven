package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.runner.Result;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class ThenJUnitTest<SELF extends ThenJUnitTest<?>> extends ThenReportModel<SELF> {
    @ProvidedScenarioState
    Result result;

    public void the_test_passes() {
        assertThat( result.getFailureCount() ).isEqualTo( 0 );
    }
}
