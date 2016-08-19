package com.tngtech.jgiven.integration.spring.test;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.SimpleSpringScenarioTest;
import com.tngtech.jgiven.integration.spring.SpringScenarioExecutor;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( classes = SpringOrderTest.TestConfiguration.class )
@FixMethodOrder( MethodSorters.NAME_ASCENDING )
public class SpringOrderTest extends SimpleSpringScenarioTest<SpringOrderTest.TestStage> {

    static class TestStage extends Stage<TestStage> {

        public void testAssertionFailed() {
            assertThat( true ).isFalse();
        }

        public void testAssertionPassed() {
            assertThat( true ).isTrue();

        }

    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public SpringScenarioExecutor springScenarioExecutor() {
            return new SpringScenarioExecutor();
        }
    }

    @Test
    public void _1() {
        then().testAssertionPassed();
    }

    @Test
    public void _2() throws Exception {
        then().testAssertionFailed();
    }
}