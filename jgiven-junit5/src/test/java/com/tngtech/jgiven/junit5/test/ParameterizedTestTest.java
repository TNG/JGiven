package com.tngtech.jgiven.junit5.test;

import static org.assertj.core.api.Java6Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.tngtech.jgiven.annotation.CaseAs;
import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.junit5.ScenarioTest;

@ExtendWith( JGivenExtension.class )
public class ParameterizedTestTest extends ScenarioTest<GivenStage, WhenStage, ThenStage> {

    @ParameterizedTest( name = "{index} [{arguments}] param name" )
    @ValueSource( strings = { "Hello", "World" } )
    @CaseAs( "Case $1" )
    public void parameterized_scenario( String param ) {
        given().some_state();
        when().some_action_with_a_parameter( param );
        then().some_outcome();

        assertThat( getScenario().getScenarioCaseModel().getDescription() ).isIn( "Case Hello", "Case World" );
    }

}
