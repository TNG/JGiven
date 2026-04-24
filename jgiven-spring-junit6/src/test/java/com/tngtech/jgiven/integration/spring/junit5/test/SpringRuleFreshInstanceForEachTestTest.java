package com.tngtech.jgiven.integration.spring.junit5.test;

import com.tngtech.jgiven.integration.spring.junit5.config.TestSpringConfig;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit5.test.proxy.ThenNewInstanceStage;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration( classes = TestSpringConfig.class )
public class SpringRuleFreshInstanceForEachTestTest
        extends SpringScenarioTest<SimpleTestSpringSteps, SimpleTestSpringSteps, ThenNewInstanceStage> {

    private static Object previousInstance;

    @Test
    public void spring_should_have_new_stage_instance_for_each_test_case_A() {
        checkStepInstance();
    }

    @Test
    public void spring_should_have_new_stage_instance_for_each_test_case_B() {
        checkStepInstance();
    }

    private void checkStepInstance() {
        given().a_step_that_is_a_spring_component();
        then().the_step_instance_is_not_the_same_as_on_previous_run( previousInstance );
        previousInstance = then();
    }

}
