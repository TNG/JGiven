package com.tngtech.jgiven.integration.spring.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;
import com.tngtech.jgiven.integration.spring.config.TestSpringConfig;

@RunWith( DataProviderRunner.class )
@ContextConfiguration( classes = TestSpringConfig.class )
public class SpringRuleScenarioWithDataProviderTest extends SpringRuleScenarioTest<SimpleTestSpringSteps, SimpleTestSpringSteps, SimpleTestSpringSteps> {

    @Test
    @DataProvider({"John", "Doe"})
    public void spring_can_inject_beans_into_stages(String name) {
        given().a_step_that_is_a_spring_component();
        when().method_with_parameter_is_called(name);
        then().beans_are_injected();
    }
}
