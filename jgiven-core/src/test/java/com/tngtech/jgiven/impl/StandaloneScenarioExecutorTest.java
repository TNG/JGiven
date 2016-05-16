package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.exception.JGivenExecutionException;

public class StandaloneScenarioExecutorTest {
    @Rule
    public final ExpectedException expectedExceptionRule = ExpectedException.none();

    @Test
    public void methods_annotated_with_BeforeStage_are_executed_before_the_first_step_is_executed() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        BeforeStageStep steps = executor.addStage( BeforeStageStep.class );
        executor.startScenario( "Test" );
        steps.before_stage_was_executed();
    }

    @Test
    public void methods_annotated_with_AfterStage_are_executed_before_the_first_step_of_the_next_stage_is_executed() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        AfterStageStep steps = executor.addStage( AfterStageStep.class );
        NextSteps nextSteps = executor.addStage( NextSteps.class );
        executor.startScenario( "Test" );
        steps.after_stage_was_not_yet_executed();
        nextSteps.after_stage_was_executed();
    }

    @Test
    public void methods_annotated_with_NotImplementedYet_are_not_really_executed() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        NotImplementedYetTestStep steps = executor.addStage( NotImplementedYetTestStep.class );
        executor.startScenario( "Test" );
        steps.something_not_implemented_yet();
        assertThat( true ).as( "No exception was thrown" ).isTrue();
    }

    @Test
    public void methods_annotated_with_NotImplemented_must_follow_fluent_interface_convention_or_return_null() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        NotImplementedYetTestStep steps = executor.addStage( NotImplementedYetTestStep.class );
        executor.startScenario( "Test" );
        assertThat( steps.something_not_implemented_yet_with_wrong_signature() ).isNull();
    }

    @Test
    public void stepclasses_annotated_with_NotImplementedYet_are_not_really_executed() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        NotImplementedYetTestStepClass steps = executor.addStage( NotImplementedYetTestStepClass.class );
        executor.startScenario( "Test" );
        steps.something_not_implemented_yet();
        assertThat( true ).as( "No exception was thrown" ).isTrue();
    }

    @Test
    public void methods_annotated_with_Pending_are_not_really_executed() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        PendingTestStep steps = executor.addStage( PendingTestStep.class );
        executor.startScenario( "Test" );
        steps.something_pending();
        assertThat( true ).as( "No exception was thrown" ).isTrue();
    }

    @Test
    public void methods_annotated_with_Pending_must_follow_fluent_interface_convention_or_return_null() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        PendingTestStep steps = executor.addStage( PendingTestStep.class );
        executor.startScenario( "Test" );
        assertThat( steps.something_pending_with_wrong_signature() ).isNull();
    }

    @Test
    public void stepclasses_annotated_with_Pending_are_not_really_executed() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        PendingTestStepClass steps = executor.addStage( PendingTestStepClass.class );
        executor.startScenario( "Test" );
        steps.something_pending();
        assertThat( true ).as( "No exception was thrown" ).isTrue();
    }

    @Test
    public void steps_are_injected() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        TestClass testClass = new TestClass();
        executor.injectStages( testClass );

        assertThat( testClass.step ).isNotNull();
        assertThat( testClass.step.subStep ).isNotNull();
    }

    @Test
    public void recursive_steps_are_injected_correctly() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        RecursiveTestClass testClass = new RecursiveTestClass();

        executor.injectStages( testClass );

        assertThat( testClass.step ).isNotNull();
        assertThat( testClass.step.step ).isSameAs( testClass.step );
    }

    @Test
    public void BeforeStage_methods_may_not_have_parameters() {
        expectedExceptionRule.expect( JGivenExecutionException.class );
        expectedExceptionRule.expectMessage( "Could not execute method 'setup' of class 'BeforeStageWithParameters'" );
        expectedExceptionRule.expectMessage( ", because it requires parameters" );

        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        BeforeStageWithParameters stage = executor.addStage( BeforeStageWithParameters.class );
        executor.startScenario( "Test" );
        stage.something();
    }

    @Test
    public void DoNotIntercept_methods_are_executed_even_if_previous_steps_fail() {
        ScenarioExecutor executor = new StandaloneScenarioExecutor();
        DoNotInterceptClass stage = executor.addStage( DoNotInterceptClass.class );
        executor.startScenario( "Test" );
        stage.a_failing_step();
        int i = stage.returnFive();
        assertThat( i ).isEqualTo( 5 );
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

    static class BeforeStageWithParameters {
        @BeforeStage
        protected void setup( int someParam ) {}

        public void something() {}
    }

    static class NextSteps {
        @ExpectedScenarioState
        boolean afterStageExecuted;

        public void after_stage_was_executed() {
            assertThat( afterStageExecuted ).isTrue();
        }
    }

    static class PendingTestStep {
        @Pending
        public PendingTestStep something_pending() {
            throw new UnsupportedOperationException();
        }

        @Pending
        public String something_pending_with_wrong_signature() {
            return "something";
        }
    }

    @Pending
    static class PendingTestStepClass {
        public PendingTestStepClass something_pending() {
            throw new UnsupportedOperationException();
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

    static class DoNotInterceptClass {
        public void a_failing_step() {
            assertThat( true ).isFalse();
        }

        @DoNotIntercept
        public int returnFive() {
            return 5;
        }
    }

}
