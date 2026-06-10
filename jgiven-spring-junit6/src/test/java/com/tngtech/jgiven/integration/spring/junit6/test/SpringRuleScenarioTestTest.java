package com.tngtech.jgiven.integration.spring.junit6.test;

import com.tngtech.jgiven.integration.spring.junit6.SpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit6.config.TestSpringConfig;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestSpringConfig.class)
public class SpringRuleScenarioTestTest extends SpringScenarioTest<SimpleTestSpringSteps, SimpleTestSpringSteps, SimpleTestSpringSteps> {

    @Test
    public void spring_can_inject_beans_into_stages() {
        given().a_step_that_is_a_spring_component();
        when().methods_on_this_component_are_called();
        then().beans_are_injected();
    }
}
