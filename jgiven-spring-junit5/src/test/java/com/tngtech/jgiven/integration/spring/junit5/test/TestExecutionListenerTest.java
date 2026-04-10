package com.tngtech.jgiven.integration.spring.junit5.test;

import com.tngtech.jgiven.integration.spring.junit5.SimpleSpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit5.config.TestSpringConfig;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

@ContextConfiguration(classes = { TestSpringConfig.class, TestExecutionListenerTest.SpringTestConfiguration.class })
//@TestExecutionListeners(TestExecutionListenerTest.BeanInjectingListener.class)
class TestExecutionListenerTest extends SimpleSpringScenarioTest<SimpleTestSpringSteps> {
    private static final String BEAN_NAME = "messageHolder";

    @Configuration
    static class SpringTestConfiguration {

        @Bean(BEAN_NAME)
        AtomicReference<String> messageHolder() {
            return new AtomicReference<>("Test execution listener has not modified this bean");
        }
    }

    static class BeanInjectingListener implements TestExecutionListener {
        @Override
        public void beforeTestMethod(TestContext testContext) {
            testContext.getApplicationContext().getBean(BEAN_NAME, AtomicReference.class)
                    .set("Test execution listener updated this bean!");
        }
    }

    @Test
    void test_execution_listener_is_executed() {
        given().a_step_that_is_a_spring_component();
        when().methods_on_this_component_are_called();
        then().bean_$_is_reference_to_string_$(BEAN_NAME, "Test execution listener updated this bean!");
    }

}
