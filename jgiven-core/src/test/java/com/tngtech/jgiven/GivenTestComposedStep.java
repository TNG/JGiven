package com.tngtech.jgiven;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;

public class GivenTestComposedStep extends Stage<GivenTestComposedStep> {

    @ProvidedScenarioState
    int value3;

    public void some_integer_value_in_the_substep( int someIntValue ) {
        this.value3 = someIntValue;
    }


}
