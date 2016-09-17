package com.tngtech.jgiven.examples.userguide;

//tag::noPackage[]

import org.junit.Test;
//tag::header[]
import com.tngtech.jgiven.junit.ScenarioTest;

public class MyShinyJGivenTest extends ScenarioTest<GivenSomeState, WhenSomeAction, ThenSomeOutcome> {
//end::header[]

//tag::method[]
    @Test
    public void something_should_happen() {
        given().some_state();
        when().some_action();
        then().some_outcome();
    }
//end::method[]
}
//end::noPackage[]