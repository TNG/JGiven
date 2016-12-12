package com.tngtech.jgiven.integration.spring.test.proxy;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.integration.spring.SpringRuleScenarioTest;
import com.tngtech.jgiven.integration.spring.SpringScenarioTest;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration( classes = ProxyTestConfig.class )
public class SpringProxyTest extends SpringRuleScenarioTest<GivenTestStageWithAspect, WhenTestStageWithAspect, ThenTestStage> {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @Ignore("Reproduces Issue #254, ignored until fixed")
    public void with_spring_proxies_autowired_should_also_work() throws Exception {
        given().should_say_hello();

        when().appendWorld_is_called();

        then().the_message_should_equal_$("Hello World!");
    }
}
