package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.junit.test.BeforeAfterTestStage;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

public class ScenarioExecutionTest extends ScenarioTest<BeforeAfterTestStage, WhenTestStep, ThenTestStep> {

    @Test
    public void before_and_after_is_correctly_executed() {
        assertThat( getScenario().getGivenStage().beforeCalled ).isEqualTo( 0 );

        given().something();

        assertThat( getScenario().getGivenStage().beforeCalled ).isEqualTo( 1 );

        when().something();

        assertThat( getScenario().getGivenStage().beforeCalled ).isEqualTo( 1 );
        assertThat( getScenario().getGivenStage().afterCalled ).isEqualTo( 1 );
    }

    static class TestStage extends Stage<TestStage> {
        boolean beforeCalled;

        @BeforeScenario
        public void beforeCalled() {
            beforeCalled = true;
        }

        public void an_exception_is_thrown() {
            throw new RuntimeException( "this exception should not be thrown" );
        }
    }

    @Test
    public void beforeStage_is_executed_for_stages_added_with_the_test_method() {
        TestStage stage = addStage( TestStage.class );
        given().something();
        assertThat( stage.beforeCalled ).isTrue();
    }

    @Test
    @NotImplementedYet
    public void NotImplementedYet_annotation_works_on_test_methods() {
        TestStage stage = addStage( TestStage.class );
        stage.given().an_exception_is_thrown();
        assertThat( true ).isTrue();
    }
}
