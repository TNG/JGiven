package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JGivenExtension.class)
class JUnit5ExtensionTest {

    @ScenarioStage
    GivenStage givenStage;

    @ScenarioStage
    WhenStage whenStage;

    @ScenarioStage
    ThenStage thenStage;

    @Test
    public void JGiven_works_with_JUnit5() {
        givenStage.some_state();
        whenStage.some_action();
        thenStage.some_outcome();
    }

    @Test
    @Pending
    public void Pending_works() {
        whenStage.some_failing_step();
    }
}
