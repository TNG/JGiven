package com.tngtech.jgiven.example.projects.junit5;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith( JGivenExtension.class )
@JGivenConfiguration( JGivenTestConfiguration.class )
class JUnit5Test {

    @ScenarioStage
    GivenStage givenStage;

    @ScenarioStage
    WhenStage whenStage;

    @ScenarioStage
    ThenStage thenStage;

    @Test
    @Tag("JUnit5_Tag")
    void scenario_with_JUnit5() {
        givenStage.given().message( "Hello JUnit" );
        whenStage.when().handle_message();
        thenStage.then().the_result_is( "Hello JUnit 5!" );
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hi JUnit", "Bye JUnit"})
    void parameterized_scenario(String message) {

        givenStage.given().message(message);
        whenStage.when().handle_message();
        thenStage.then().the_result_is(message + " 5!");
    }
}
