package com.tngtech.jgiven.junit5.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import com.tngtech.jgiven.annotation.CaseAs;
import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.junit5.ScenarioTest;

import java.util.stream.Stream;

@ExtendWith(JGivenExtension.class)
public class ParameterizedTestTest extends ScenarioTest<GivenStage, WhenStage, ThenStage> {

    @ParameterizedTest(name = "{index} [{arguments}] param name")
    @ValueSource(strings = {"Hello", "World"})
    @NullSource
    @CaseAs("Case $1")
    public void parameterized_scenario(String param) {
        given().some_state();
        when().some_action_with_a_parameter(param);
        then().some_outcome();

        assertThat(getScenario().getScenarioCaseModel().getDescription()).isIn("Case Hello", "Case World", "Case null");
    }

    @ParameterizedTest
    @MethodSource("parametersWithNullInSubsequentTestCases")
    void parameterized_scenario_with_null_arguments_in_subsequent_test_cases(String param1, String param2) {
        given().some_state();
        when().some_action_with_a_parameter(param1);
        when().some_action_with_a_parameter(param2);
        then().some_outcome();

        assertThat(getScenario().getScenarioCaseModel().getExplicitArguments()).containsExactly(String.valueOf(param1), String.valueOf(param2));
    }


    @ParameterizedTest
    @MethodSource("parametersWithNullInFirstTestCase")
    void parameterized_scenario_with_null_arguments_in_first_iteration(String param1, String param2) {
        given().some_state();
        when().some_action_with_a_parameter(param1);
        when().some_action_with_a_parameter(param2);
        then().some_outcome();

        assertThat(getScenario().getScenarioCaseModel().getExplicitArguments()).containsExactly(String.valueOf(param1), String.valueOf(param2));
    }

    static Stream<Arguments> parametersWithNullInSubsequentTestCases() {
        return Stream.of(
                Arguments.of("Hello", "World"),
                Arguments.of(null, "World"),
                Arguments.of("Hello", null),
                Arguments.of(null, null)
        );
    }

    static Stream<Arguments> parametersWithNullInFirstTestCase() {
        return Stream.of(
                Arguments.of(null, "World"),
                Arguments.of("Hello", "World")//,
        );
    }
}
