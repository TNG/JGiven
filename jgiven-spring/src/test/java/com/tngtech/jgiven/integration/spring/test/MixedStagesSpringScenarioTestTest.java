package com.tngtech.jgiven.integration.spring.test;

import com.tngtech.jgiven.integration.spring.SpringScenarioTest;
import com.tngtech.jgiven.integration.spring.config.TestSpringConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = TestSpringConfig.class )
public class MixedStagesSpringScenarioTestTest extends SpringScenarioTest<AnnotatedStage, SomeWhen, SomeThen> {

    @Test
    public void using_beans_and_ordinary_stages_together() {
        given().a_stage_that_is_a_spring_component();
        when().is_used_in_combination_with_ordinary_stages();
        then().mixing_them_works_as_expected();
    }
}
