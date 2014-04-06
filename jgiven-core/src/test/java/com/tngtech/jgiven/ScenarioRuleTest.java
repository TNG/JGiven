package com.tngtech.jgiven;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.base.ScenarioTestBase;

public class ScenarioRuleTest extends ScenarioTestBase<BeforeAfterTestStage<?>, WhenTestStep, BeforeAfterTestStage<?>> {

    @Test
    public void testBeforeAndAfterIsCalled() {
        scenario.startScenario( "Some Scenario" );
        BeforeAfterTestStage<?> steps = given().something();
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

        assertThat( scenario.getWhenStage().afterStageCalled ).as( "afterStage has not been called" ).isEqualTo( 0 );
        assertThat( scenario.getGivenStage().afterStageCalled ).as( "afterStage has been called" ).isEqualTo( 1 );
    }

    @Test
    public void whenExceptionThrownInStepThenAfterMethodsAreExecuted() {
        scenario.startScenario( "some description" );
        BeforeAfterTestStage<?> steps = given();
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

    static class ExceptionStep extends BeforeAfterTestStage<ExceptionStep> {
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

    public static class TestRule {
        public int beforeCalled;
        public int afterCalled;

        void before() {
            beforeCalled++;
        }

        void after() {
            afterCalled++;
        }
    }
}
