package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;

import static org.assertj.core.api.Assertions.assertThat;

public class ThenTestComposedStep extends Stage<ThenTestComposedStep> {

    @ExpectedScenarioState
    int value3;

    public ThenTestComposedStep the_substep_value_is(int expected) {
        assertThat( value3 ).isEqualTo( expected );
        return self();
    }
}
