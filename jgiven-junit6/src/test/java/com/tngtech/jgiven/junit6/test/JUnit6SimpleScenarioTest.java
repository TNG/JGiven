package com.tngtech.jgiven.junit6.test;

import com.tngtech.jgiven.junit6.SimpleScenarioTest;
import org.junit.jupiter.api.Test;

public class JUnit6SimpleScenarioTest extends SimpleScenarioTest<GeneralStage> {

    @Test
    public void JGiven_works_with_JUnit6_FOO_BAR() {
        given().some_state();
        when().some_action();
        then().some_outcome();
    }

}
