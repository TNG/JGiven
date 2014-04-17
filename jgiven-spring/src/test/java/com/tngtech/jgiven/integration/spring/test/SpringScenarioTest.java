package com.tngtech.jgiven.integration.spring.test;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tngtech.jgiven.integration.spring.SpringCanWire;
import com.tngtech.jgiven.integration.spring.test.SpringScenarioTest.SimpleTestSpringSteps;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = TestSpringConfig.class )
public class SpringScenarioTest extends SimpleScenarioTest<SimpleTestSpringSteps> {
    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Before
    public void setupSpring() {
        wireSteps( new SpringCanWire( beanFactory ) );
    }

    @Test
    public void spring_can_inject_beans_into_stages() {
        then().test_bean_is_injected();
    }

    static class SimpleTestSpringSteps {
        @Autowired
        TestBean testBean;

        public void test_bean_is_injected() {
            Assertions.assertThat( testBean ).isNotNull();
        }
    }
}
