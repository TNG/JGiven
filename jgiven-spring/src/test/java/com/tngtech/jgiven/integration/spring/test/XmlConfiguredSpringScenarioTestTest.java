package com.tngtech.jgiven.integration.spring.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tngtech.jgiven.integration.spring.SpringScenarioTest;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration("/jgiven-spring.xml")
public class XmlConfiguredSpringScenarioTestTest extends SpringScenarioTest<AnnotatedStage, AnnotatedStage, AnnotatedStage> {

    @Test
    public void spring_can_inject_beans_into_stages() {
        given().a_stage_that_is_a_spring_component();
        when().methods_on_this_component_are_called();
        then().beans_are_injected();
    }
}
