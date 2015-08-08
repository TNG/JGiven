package com.tngtech.jgiven.junit;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

public class StandaloneScenarioRuleTest {

    @Rule
    public StandaloneScenarioRule scenarioRule = new StandaloneScenarioRule();

    @ClassRule
    public static ScenarioReportRule reportRule = new ScenarioReportRule();

    @ScenarioStage
    GivenTestStep givenStage;

    @ScenarioStage
    WhenTestStep whenStage;

    @ScenarioStage
    ThenTestStep thenStage;

    @Test
    public void JGiven_can_be_used_with_just_a_rule() {
        givenStage
            .given().some_integer_value( 5 )
            .and().another_integer_value( 6 );

        whenStage
            .when().both_values_are_multiplied_with_each_other();

        thenStage
            .then().the_result_is( 30 );

    }
}
