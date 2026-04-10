package com.tngtech.jgiven.integration.spring.junit5.test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import com.tngtech.jgiven.integration.spring.junit5.DualSpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit5.SimpleSpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit5.SpringScenarioTest;
import com.tngtech.jgiven.integration.spring.junit5.config.TestSpringConfig;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration( classes = TestSpringConfig.class )
public class AdditionalStageTest extends SimpleSpringScenarioTest<SimpleTestSpringSteps> {

    @Nested
    @ContextConfiguration( classes = TestSpringConfig.class )
    class SimpleTest extends SimpleSpringScenarioTest<SimpleTestSpringSteps> {
        @ScenarioStage
        AdditionalStage additionalStage;

        @Test
        public void beans_are_injected_in_additional_stages() {
            additionalStage.when().an_additional_stage_is_injected();
            additionalStage.then().spring_beans_of_this_stage_are_injected();
        }
    }

    @Nested
    @ContextConfiguration( classes = TestSpringConfig.class )
    class DualTest extends DualSpringScenarioTest<SimpleTestSpringSteps, SimpleTestSpringSteps> {
        @ScenarioStage
        AdditionalStage additionalStage;

        @Test
        public void beans_are_injected_in_additional_stages() {
            additionalStage.when().an_additional_stage_is_injected();
            additionalStage.then().spring_beans_of_this_stage_are_injected();
        }

    }

    @Nested
    @ContextConfiguration( classes = TestSpringConfig.class )
    class GivenWhenThenStagesTest extends SpringScenarioTest<SimpleTestSpringSteps, SimpleTestSpringSteps, SimpleTestSpringSteps> {
        @ScenarioStage
        AdditionalStage additionalStage;

        @Test
        public void beans_are_injected_in_additional_stages() {
            additionalStage.when().an_additional_stage_is_injected();
            additionalStage.then().spring_beans_of_this_stage_are_injected();
        }
    }

    @JGivenStage
    static class AdditionalStage extends Stage<AdditionalStage> {
        private final TestBean testBean;

        AdditionalStage(TestBean testBean) {
            this.testBean = testBean;
        }

        public AdditionalStage an_additional_stage_is_injected() {
            return this;
        }

        public AdditionalStage spring_beans_of_this_stage_are_injected() {
            Assertions.assertThat( testBean ).isNotNull();
            return this;
        }
    }
}
