package com.tngtech.jgiven.integration.spring.junit5.test;

import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

@ExtendWith(SpringExtension.class)
@TestExecutionListeners(TestExecutionListenerTest.BeanInjectingListener.class)
class TestExecutionListenerTest {
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
    void testExecutionListenerIsExecuted(@Autowired ApplicationContext context) {
        assertThat(context.getBean(BEAN_NAME))
                .asInstanceOf(type(AtomicReference.class))
                .extracting(AtomicReference::get)
                .isEqualTo("Test execution listener updated this bean!");
    }

}
