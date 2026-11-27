package com.tngtech.jgiven.example.springboot;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.integration.spring.EnableJGiven;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import com.tngtech.jgiven.integration.spring.SimpleSpringRuleScenarioTest;
import org.junit.Test;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(includeFilters = @ComponentScan.Filter(JGivenStage.class))
@EnableJGiven
@JGivenConfiguration( HelloJGivenConfiguration.class )
public class JpaTest extends SimpleSpringRuleScenarioTest<JpaStage> {

    @Test
    public void test_entity_manager_can_be_used() throws Exception {
        given().test_entity_manager_is_defined();
    }

}
