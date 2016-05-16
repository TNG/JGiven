package com.tngtech.jgiven;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.ScenarioStage;

public class ThenTestStep extends Stage<ThenTestStep> {

    @ExpectedScenarioState
    int intResult;

    @ScenarioStage
    ThenTestComposedStep thenTestComposedStep;

    @ExpectedScenarioState
    int value3;

    public void sms_and_emails_exist() {}

    public ThenTestStep the_result_is( int i ) {
        assertThat( intResult ).isEqualTo( i );
        return self();
    }

    public ThenTestStep something_has_happen() {
        return self();
    }

    @Pending
    public ThenTestStep something_else_not() {
        return self();
    }

    public ThenTestStep the_substep_value_is( int expected ) {
        thenTestComposedStep.the_substep_value_is( expected );
        return self();
    }

    public ThenTestStep the_substep_value_referred_in_the_step_is( int expected ) {
        assertThat( value3 ).isEqualTo( expected );
        return self();
    }
}
