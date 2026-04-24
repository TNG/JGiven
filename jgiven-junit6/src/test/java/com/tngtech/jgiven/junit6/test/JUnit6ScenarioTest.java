package com.tngtech.jgiven.junit6.test;

import com.tngtech.jgiven.junit6.ScenarioTest;
import org.junit.jupiter.api.Test;

public class JUnit6ScenarioTest extends ScenarioTest<GivenStage, WhenStage, ThenStage> {

    @Test
    public void JGiven_works_with_JUnit6_DISABLED() {
        given().some_state();
        when().some_action();
        then().some_outcome();
    }


}
