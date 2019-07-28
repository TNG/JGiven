package com.tngtech.jgiven.integration.spring.test;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import com.tngtech.jgiven.integration.spring.config.TestSpringConfig;

@ContextConfiguration( classes = TestSpringConfig.class )
public class SimpleSpringRuleScenarioTestTest extends SimpleSpringRuleScenarioTest<SimpleTestSpringSteps> {

    @Test
    public void spring_can_inject_beans_into_stages() {
        given().a_step_that_is_a_spring_component();
        when().methods_on_this_component_are_called();
        then().beans_are_injected();
    }
}
