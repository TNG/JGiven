package com.tngtech.jgiven.integration.spring.junit5.test.proxy;

import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = ProxyTestConfig.class)
public class SpringTransactionalTest extends SpringScenarioTest<GivenTestStageWithTransactional, WhenTestStage, ThenTestStage> {

    @Test
    public void with_transactional_autowired_should_also_work() throws Exception {
        given().should_say_hello();

        when().appendWorld_is_called();

        then().the_message_should_equal_$("Hello World!");
    }
}
