package com.tngtech.jgiven.integration.spring.test.proxy;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;

@ContextConfiguration( classes = ProxyTestConfig.class )
public class SpringProxyTest extends SpringRuleScenarioTest<GivenTestStageWithAspect, WhenTestStageWithAspect, ThenTestStage> {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void with_spring_proxies_autowired_should_also_work() throws Exception {
        given().should_say_hello();

        when().appendWorld_is_called();

        then().the_message_should_equal_$("Hello World!");
    }
}
