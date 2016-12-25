package com.tngtech.jgiven.example.projects.junit5;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.JGivenExtension;

@ExtendWith( JGivenExtension.class )
public class JUnit5Test {

    @ScenarioStage
    GivenStage givenStage;

    @ScenarioStage
    WhenStage whenStage;

    @ScenarioStage
    ThenStage thenStage;

    @Test
    public void scenario_with_JUnit5() {
        givenStage.given().message( "Hello JUnit" );
        whenStage.when().handle_message();
        thenStage.then().the_result_is( "Hello JUnit 5!" );
    }
}