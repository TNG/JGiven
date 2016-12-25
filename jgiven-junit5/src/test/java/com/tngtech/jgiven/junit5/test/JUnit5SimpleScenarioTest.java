package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.junit.jupiter.api.Test;

public class JUnit5SimpleScenarioTest extends SimpleScenarioTest<GeneralStage> {

    @Test
    public void JGiven_works_with_JUnit5() {
        given().some_state();
        when().some_action();
        then().some_outcome();
    }

}
