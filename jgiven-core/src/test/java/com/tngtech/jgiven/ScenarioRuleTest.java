package com.tngtech.jgiven;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.report.model.ReportModel;

public class ScenarioRuleTest extends ScenarioTestBase<BeforeAfterTestStage<?>, WhenTestStep, BeforeAfterTestStage<?>> {

    @Test
    public void testBeforeAndAfterIsCalled() throws Throwable {
        getScenario().setModel( new ReportModel() );
        getScenario().startScenario( "Some Scenario" );
        BeforeAfterTestStage<?> steps = given().something();
        TestRule rule = steps.rule;
        assertThat( rule.beforeCalled ).isEqualTo( 1 );
        assertThat( steps.beforeCalled ).isEqualTo( 1 );
        when();
        then();
        assertThat( rule.afterCalled ).isEqualTo( 0 );
        assertThat( steps.afterCalled ).isEqualTo( 0 );

        getScenario().finished();

        assertThat( rule.beforeCalled ).isEqualTo( 1 );
        assertThat( rule.afterCalled ).isEqualTo( 1 );
        assertThat( steps.beforeCalled ).isEqualTo( 1 );
        assertThat( steps.afterCalled ).isEqualTo( 1 );
    }

    @Test
    public void methods_annotated_with_AfterStage_are_called() {
        getScenario().startScenario( "methods_annotated_with_AfterStage_are_called" );
        given().something();
        when().something_happens();

        assertThat( getScenario().getWhenStage().afterStageCalled ).as( "afterStage has not been called" ).isEqualTo( 0 );
        assertThat( getScenario().getGivenStage().afterStageCalled ).as( "afterStage has been called" ).isEqualTo( 1 );
    }

    @Test
    public void afterStage_methods_are_only_invoked_once() {
        getScenario().startScenario( "methods_annotated_with_AfterStage_are_called_only_once" );
        given().something();
        when().something_happens();
        given().something();
        when().something_happens();
        assertThat( getScenario().getGivenStage().afterStageCalled ).as( "afterStage has been called" ).isEqualTo( 1 );
    }

    @Test
    public void whenExceptionThrownInStepThenAfterMethodsAreExecuted() {
        getScenario().startScenario( "some description" );
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
        ExceptionStep steps = getScenario().addStage( ExceptionStep.class );
        try {
            getScenario().startScenario( "some description" );
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
