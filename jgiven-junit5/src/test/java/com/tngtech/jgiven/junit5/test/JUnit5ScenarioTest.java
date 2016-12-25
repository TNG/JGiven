package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.junit5.ScenarioTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.JGivenExtension;

public class JUnit5ScenarioTest extends ScenarioTest<GivenStage, WhenStage, ThenStage> {

    @Test
    public void JGiven_works_with_JUnit5() {
        given().some_state();
        when().some_action();
        then().some_outcome();
    }

}
