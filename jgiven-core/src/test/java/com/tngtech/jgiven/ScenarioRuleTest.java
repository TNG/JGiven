package com.tngtech.jgiven;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.ScenarioRuleTest.TestSteps;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.base.ScenarioTestBase;

public class ScenarioRuleTest extends ScenarioTestBase<TestSteps<?>, WhenTestStep, TestSteps<?>> {

    @Test
    public void testBeforeAndAfterIsCalled() {
        scenario.startScenario( "Some Scenario" );
        TestSteps<?> steps = given().something();
        TestRule rule = steps.rule;
        assertThat( rule.beforeCalled ).isEqualTo( 1 );
        assertThat( steps.beforeCalled ).isEqualTo( 1 );
        when();
        then();
        assertThat( rule.afterCalled ).isEqualTo( 0 );
        assertThat( steps.afterCalled ).isEqualTo( 0 );

        scenario.finished();

        assertThat( rule.beforeCalled ).isEqualTo( 1 );
        assertThat( rule.afterCalled ).isEqualTo( 1 );
        assertThat( steps.beforeCalled ).isEqualTo( 1 );
        assertThat( steps.afterCalled ).isEqualTo( 1 );
    }

    @Test
    public void methods_annotated_with_AfterStage_are_called() {
        scenario.startScenario( "methods_annotated_with_AfterStage_are_called" );
        given().something();
        when().something_happens();

        assertThat( scenario.getWhenSteps().afterStageCalled ).as( "afterStage has not been called" ).isEqualTo( 0 );
        assertThat( scenario.getGivenSteps().afterStageCalled ).as( "afterStage has been called" ).isEqualTo( 1 );
    }

    @Test
    public void whenExceptionThrownInStepThenAfterMethodsAreExecuted() {
        scenario.startScenario( "some description" );
        TestSteps<?> steps = given();
        try {
            when().an_exception_is_thrown();
        } catch( Exception e ) {
            assertThat( steps.rule.afterCalled ).isEqualTo( 1 );
            assertThat( steps.afterCalled ).isEqualTo( 1 );
        }
    }

    @Test
    public void whenExceptionThrownInBeforeOfRuleThenAfterMethodIsStillCalled() {
        ExceptionStep steps = scenario.addSteps( ExceptionStep.class );
        try {
            scenario.startScenario( "some description" );
            steps.given().something();
        } catch( Exception e ) {
            TestRule rule = steps.exceptionRule;
            assertThat( rule.beforeCalled ).isEqualTo( 1 );
            assertThat( rule.afterCalled ).isEqualTo( 1 );
            assertThat( steps.beforeCalled ).isEqualTo( 0 );
            assertThat( steps.afterCalled ).isEqualTo( 0 );
        }
    }

    static class ExceptionStep extends TestSteps<ExceptionStep> {
        @ScenarioRule
        BeforeExceptionRule exceptionRule = new BeforeExceptionRule();
    }

    static class BeforeExceptionRule extends TestRule {
        @Override
        void before() {
            super.before();
            throw new RuntimeException();
        }
    }

    public static class TestSteps<SELF extends TestSteps<?>> extends Stage<SELF> {
        @ScenarioRule
        TestRule rule = new TestRule();

        int beforeCalled;
        int afterCalled;
        int afterStageCalled;

        public SELF something() {
            return self();
        }

        @AfterStage
        void someAfterStageMethod() {
            afterStageCalled++;
        }

        @BeforeScenario
        void someBeforeMethod() {
            beforeCalled++;
        }

        @AfterScenario
        void someAfterMethod() {
            afterCalled++;
        }
    }

    static class TestRule {
        int beforeCalled;
        int afterCalled;

        void before() {
            beforeCalled++;
        }

        void after() {
            afterCalled++;
        }
    }
}
