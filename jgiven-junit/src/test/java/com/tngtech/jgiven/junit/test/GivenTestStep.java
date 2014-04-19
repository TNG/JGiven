package com.tngtech.jgiven.junit.test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenTestStep extends Stage<GivenTestStep> {

    @ProvidedScenarioState
    int someIntValue;

    @ProvidedScenarioState
    int value1;

    @ProvidedScenarioState
    int value2;

    public GivenTestStep some_integer_value( int someIntValue ) {
        this.someIntValue = someIntValue;
        return self();
    }

    public void some_boolean_value( boolean someBooleanValue ) {

    }

    public void $d_and_$d( int value1, int value2 ) {
        this.value1 = value1;
        this.value2 = value2;
    }

}
