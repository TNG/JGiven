package com.tngtech.jgiven;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.NotImplementedYet;

public class ThenTestStep extends Stage<ThenTestStep> {
    @ExpectedScenarioState
    int intResult;

    public void sms_and_emails_exist() {}

    public ThenTestStep the_result_is( int i ) {
        assertThat( intResult ).isEqualTo( i );
        return self();
    }

    public ThenTestStep something_has_happen() {
        return self();
    }

    @NotImplementedYet
    public ThenTestStep something_else_not() {
        return self();
    }
}
