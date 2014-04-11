package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenTestStep extends Stage<GivenTestStep> {

    @ProvidedScenarioState
    int someIntValue;

    @ProvidedScenarioState
    int value1;

    @ProvidedScenarioState
    int value2;

    public void some_integer_value( int someIntValue ) {
        this.someIntValue = someIntValue;
    }

    public void $d_and_$d( int value1, int value2 ) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public GivenTestStep something() {
        return self();
    }

    public GivenTestStep something_else() {
        return self();
    }

    public GivenTestStep an_array( Object argument ) {
        return self();
    }
}
