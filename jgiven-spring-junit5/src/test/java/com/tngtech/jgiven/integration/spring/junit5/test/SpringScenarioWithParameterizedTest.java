package com.tngtech.jgiven.integration.spring.junit5.test;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit5.config.TestSpringConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration( classes = TestSpringConfig.class )
public class SpringScenarioWithParameterizedTest extends SpringScenarioTest<SimpleTestSpringSteps, SimpleTestSpringSteps, SimpleTestSpringSteps> {

    @ParameterizedTest( name = "{index} [{arguments}] param name" )
    @ValueSource( strings = { "Hello", "World" } )
    public void spring_can_inject_beans_into_stages(String param) {
        given().a_step_that_is_a_spring_component();
        when().method_with_parameter_is_called(param);
        then().beans_are_injected();
    }
}
