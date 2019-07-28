package com.tngtech.jgiven.integration.spring.test.proxy;

import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = ProxyTestConfig.class)
public class SpringTransactionalTest extends SpringRuleScenarioTest<GivenTestStageWithTransactional, WhenTestStageWithAspect, ThenTestStage> {

    @Test
    public void with_transactional_autowired_should_also_work() throws Exception {
        given().should_say_hello();

        when().appendWorld_is_called();

        then().the_message_should_equal_$("Hello World!");
    }
}
