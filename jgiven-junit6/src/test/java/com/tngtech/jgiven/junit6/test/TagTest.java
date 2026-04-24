package com.tngtech.jgiven.junit6.test;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit6.JGivenExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Tag("class-tag")
@ExtendWith( JGivenExtension.class )
public class TagTest {

    @ScenarioStage
    GivenStage givenStage;

    @ScenarioStage
    WhenStage whenStage;

    @ScenarioStage
    ThenStage thenStage;

    @Test
    @Tag("method-tag")
    public void scenario_with_a_JUnit6_tag() {
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
