package com.tngtech.jgiven.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.DoNotIntercept;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.exception.JGivenExecutionException;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

public class ScenarioExecutorTest {

    @Rule
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Test
    public void methods_annotated_with_BeforeStage_are_executed_before_the_first_step_is_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        BeforeStageStep steps = executor.addStage( BeforeStageStep.class );
        executor.startScenario( "Test" );
        steps.before_stage_was_executed();
    }

    @Test
    public void methods_annotated_with_AfterStage_are_executed_before_the_first_step_of_the_next_stage_is_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        AfterStageStep steps = executor.addStage( AfterStageStep.class );
        NextSteps nextSteps = executor.addStage( NextSteps.class );
        executor.startScenario( "Test" );
        steps.after_stage_was_not_yet_executed();
        nextSteps.after_stage_was_executed();
    }

    @Test
    public void scenario_with_dry_run_enabled_does_not_execute_steps() throws Exception {
        System.setProperty( "jgiven.report.dry-run", "true" );
        ScenarioExecutor executor = new ScenarioExecutor();
        ExceptionTestStep steps = executor.addStage( ExceptionTestStep.class );
        executor.startScenario( ExceptionTestStep.class, ExceptionTestStep.class.getMethod( "something_throws_exception" ),
                Collections.emptyList() );
        steps.something_throws_exception();

        assertThat( executor.hasFailed() ).isFalse();
    }

    @Test
    public void scenario_with_dry_run_disable_fails() throws Exception {
        System.setProperty( "jgiven.report.dry-run", "false" );
        ScenarioExecutor executor = new ScenarioExecutor();
        ExceptionTestStep steps = executor.addStage( ExceptionTestStep.class );
        executor.startScenario( ExceptionTestStep.class, ExceptionTestStep.class.getMethod( "something_throws_exception" ),
                Collections.emptyList() );
        steps.something_throws_exception();

        assertThat( executor.hasFailed() ).isTrue();
    }

    @Test
    public void methods_annotated_with_Pending_are_not_really_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        PendingTestStep steps = executor.addStage( PendingTestStep.class );
        executor.startScenario( "Test" );
        steps.something_pending();

        assertThat( executor.hasFailed() ).isFalse();
    }

    @Test
    public void methods_annotated_with_Pending_must_follow_fluent_interface_convention_or_return_null() {
        ScenarioExecutor executor = new ScenarioExecutor();
        PendingTestStep steps = executor.addStage( PendingTestStep.class );
        executor.startScenario( "Test" );
        assertThat( steps.something_pending_with_wrong_signature() ).isNull();
    }

    @Test
    public void stepclasses_annotated_with_Pending_are_not_really_executed() {
        ScenarioExecutor executor = new ScenarioExecutor();
        PendingTestStepClass steps = executor.addStage( PendingTestStepClass.class );
        executor.startScenario( "Test" );
        steps.something_pending();

        assertThat( executor.hasFailed() ).isFalse();
    }

    @Test
    public void steps_are_injected() {
        ScenarioExecutor executor = new ScenarioExecutor();
        TestClass testClass = new TestClass();
        executor.injectStages( testClass );

        assertThat( testClass.step ).isNotNull();
        assertThat( testClass.step.subStep ).isNotNull();
    }

    @Test
    public void recursive_steps_are_injected_correctly() {
        ScenarioExecutor executor = new ScenarioExecutor();
        RecursiveTestClass testClass = new RecursiveTestClass();

        executor.injectStages( testClass );

        assertThat( testClass.step ).isNotNull();
        assertThat( testClass.step.step ).isSameAs( testClass.step );
    }

    @Test
    public void BeforeStage_methods_may_not_have_parameters() {
        ScenarioExecutor executor = new ScenarioExecutor();
        BeforeStageWithParameters stage = executor.addStage( BeforeStageWithParameters.class );
        executor.startScenario( "Test" );
       assertThatThrownBy(stage::something)
        .isInstanceOf( JGivenExecutionException.class )
           .hasMessageContainingAll("Could not execute method 'setup' of class 'BeforeStageWithParameters'",
               ", because it requires parameters");

    }

    @Test
    public void DoNotIntercept_methods_are_executed_even_if_previous_steps_fail() {
        ScenarioExecutor executor = new ScenarioExecutor();
        DoNotInterceptClass stage = executor.addStage( DoNotInterceptClass.class );
        executor.startScenario( "Test" );
        stage.a_failing_step();
        int i = stage.returnFive();
        assertThat( i ).isEqualTo( 5 );
    }

    @Test
    public void DoNotIntercept_methods_do_not_trigger_a_stage_change() {
        ScenarioExecutor executor = new ScenarioExecutor();
        AfterStageStep withAfterStage = executor.addStage( AfterStageStep.class );
        withAfterStage.after_stage_was_not_yet_executed();
        DoNotInterceptClass doNotIntercept = executor.addStage( DoNotInterceptClass.class );
        executor.startScenario( "Test" );

        doNotIntercept.an_unintercepted_step();
        assertThat( withAfterStage.afterStageExecuted ).as( "@AfterStage was executed" ).isFalse();

        doNotIntercept.an_intercepted_step();
        assertThat( withAfterStage.afterStageExecuted ).as( "@AfterStage was executed" ).isTrue();
    }

    static class TestClass {
        @ScenarioStage
        TestStep step;
    }

    static class TestStep {
        @ScenarioStage
        TestSubStep subStep;
    }

    static class TestSubStep {
    }

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
        protected void setup( int someParam ) {
        }

        public void something() {
        }
    }

    static class NextSteps {
        @ExpectedScenarioState
        boolean afterStageExecuted;

        public void after_stage_was_executed() {
            assertThat( afterStageExecuted ).isTrue();
        }
    }

    static class ExceptionTestStep {

        public ExceptionTestStep something_throws_exception() {
            throw new UnsupportedOperationException();
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

    static class DoNotInterceptClass {
        public void a_failing_step() {
            assertThat( true ).isFalse();
        }

        public void an_intercepted_step() {
        }

        @DoNotIntercept
        public void an_unintercepted_step() {
        }

        @DoNotIntercept
        public int returnFive() {
            return 5;
        }
    }
}
