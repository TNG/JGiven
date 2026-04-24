package com.tngtech.jgiven.integration.spring.junit5.test;

import com.tngtech.jgiven.integration.spring.junit5.SimpleSpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit5.config.TestSpringConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;


@ContextConfiguration( classes = TestSpringConfig.class )
public class SimpleSpringRuleScenarioTestTest extends SimpleSpringScenarioTest<SimpleTestSpringSteps> {

    @Test
    public void spring_can_inject_beans_into_stages() {
        given().a_step_that_is_a_spring_component();
        when().methods_on_this_component_are_called();
        then().beans_are_injected();
    }
}
