package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;

public class ScenarioExecutorTest {
    @Test
    public void methods_annotated_with_BeforeStage_are_executed_before_the_first_step_is_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        BeforeStageStep steps = executor.addSteps( BeforeStageStep.class );
        executor.startScenario( "Test" );
        steps.before_stage_was_executed();
    }

    @Test
    public void methods_annotated_with_AfterStage_are_executed_before_the_first_step_of_the_next_stage_is_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        AfterStageStep steps = executor.addSteps( AfterStageStep.class );
        NextSteps nextSteps = executor.addSteps( NextSteps.class );
        executor.startScenario( "Test" );
        steps.after_stage_was_not_yet_executed();
        nextSteps.after_stage_was_executed();
    }

    @Test
    public void methods_annotated_with_NotImplementedYet_are_not_really_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        NotImplementedYetTestStep steps = executor.addSteps( NotImplementedYetTestStep.class );
        executor.startScenario( "Test" );
        steps.something_not_implemented_yet();
        assertThat( true ).as( "No exception was thrown" ).isTrue();
    }

    @Test
    public void methods_annotated_with_NotImplemented_must_follow_fluent_interface_convention_or_return_null() {
        ScenarioExecutor executor = new ScenarioExecutor();
        NotImplementedYetTestStep steps = executor.addSteps( NotImplementedYetTestStep.class );
        executor.startScenario( "Test" );
        assertThat( steps.something_not_implemented_yet_with_wrong_signature() ).isNull();
    }

    @Test
    public void stepclasses_annotated_with_NotImplementedYet_are_not_really_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        NotImplementedYetTestStepClass steps = executor.addSteps( NotImplementedYetTestStepClass.class );
        executor.startScenario( "Test" );
        steps.something_not_implemented_yet();
        assertThat( true ).as( "No exception was thrown" ).isTrue();
    }

    @Test
    public void steps_are_injected() {
        ScenarioExecutor executor = new ScenarioExecutor();
        TestClass testClass = new TestClass();
        executor.injectSteps( testClass );

        assertThat( testClass.step ).isNotNull();
        assertThat( testClass.step.subStep ).isNotNull();
    }

    @Test
    public void recursive_steps_are_injected_correctly() {
        ScenarioExecutor executor = new ScenarioExecutor();
        RecursiveTestClass testClass = new RecursiveTestClass();

        executor.injectSteps( testClass );

        assertThat( testClass.step ).isNotNull();
        assertThat( testClass.step.step ).isSameAs( testClass.step );
    }

    static class TestClass {
        @ScenarioStage
        TestStep step;
    }

    static class TestStep {
        @ScenarioStage
        TestSubStep subStep;
    }

    static class TestSubStep {}

    static class RecursiveTestClass {
        @ScenarioStage
        RecursiveTestStep step;
    }

    static class RecursiveTestStep {
        @ScenarioStage
        RecursiveTestStep step;
    }

    static class BeforeStageStep {
        boolean beforeStageExecuted;

        @BeforeStage
        protected void setup() {
            beforeStageExecuted = true;
        }

        public void before_stage_was_executed() {
            assertThat( beforeStageExecuted ).isTrue();
        }
    }

    static class AfterStageStep {
        @ProvidedScenarioState
        boolean afterStageExecuted;

        @AfterStage
        protected void setup() {
            afterStageExecuted = true;
        }

        public void after_stage_was_not_yet_executed() {
            assertThat( afterStageExecuted ).isFalse();
        }
    }

    static class NextSteps {
        @ExpectedScenarioState
        boolean afterStageExecuted;

        public void after_stage_was_executed() {
            assertThat( afterStageExecuted ).isTrue();
        }
    }

    static class NotImplementedYetTestStep {
        @NotImplementedYet
        public NotImplementedYetTestStep something_not_implemented_yet() {
            throw new UnsupportedOperationException();
        }

        @NotImplementedYet
        public String something_not_implemented_yet_with_wrong_signature() {
            return "something";
        }
    }

    @NotImplementedYet
    static class NotImplementedYetTestStepClass {
        public NotImplementedYetTestStepClass something_not_implemented_yet() {
            throw new UnsupportedOperationException();
        }
    }
}
