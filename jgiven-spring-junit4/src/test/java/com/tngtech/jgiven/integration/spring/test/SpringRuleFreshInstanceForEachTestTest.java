package com.tngtech.jgiven.integration.spring.test;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;
import com.tngtech.jgiven.integration.spring.config.TestSpringConfig;
import com.tngtech.jgiven.integration.spring.test.proxy.ThenNewInstanceStage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@RunWith( DataProviderRunner.class )
@ContextConfiguration( classes = TestSpringConfig.class )
public class SpringRuleFreshInstanceForEachTestTest
        extends SpringRuleScenarioTest<SimpleTestSpringSteps, SimpleTestSpringSteps, ThenNewInstanceStage> {

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
