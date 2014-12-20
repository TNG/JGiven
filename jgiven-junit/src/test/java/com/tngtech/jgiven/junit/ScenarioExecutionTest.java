package com.tngtech.jgiven.junit;

import static com.tngtech.jgiven.annotation.ScenarioState.Resolution.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.AfterScenario;
import com.tngtech.jgiven.annotation.AfterStage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.annotation.ScenarioRule;
import com.tngtech.jgiven.annotation.ScenarioState;
import com.tngtech.jgiven.exception.AmbiguousResolutionException;
import com.tngtech.jgiven.junit.tags.ConfiguredTag;
import com.tngtech.jgiven.junit.test.BeforeAfterTestStage;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.Tag;

@RunWith( DataProviderRunner.class )
@JGivenConfiguration( TestConfiguration.class )
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

    @Test( expected = AmbiguousResolutionException.class )
    public void an_exception_is_thrown_when_stages_have_ambiguous_fields() {
        TestStageWithAmbiguousFields stage = addStage( TestStageWithAmbiguousFields.class );
        given().something();
        stage.something();
    }

    static class SomeType {}

    public static class TestStageWithAmbiguousFields {
        @ScenarioState
        SomeType oneType;

        @ScenarioState
        SomeType secondType;

        public void something() {}
    }

    @Test
    public void ambiguous_fields_are_avoided_by_using_resolution_by_name() {
        TestStageWithAmbiguousFieldsButResolutionByName stage = addStage( TestStageWithAmbiguousFieldsButResolutionByName.class );
        given().something();
        stage.something();
    }

    public static class TestStageWithAmbiguousFieldsButResolutionByName {
        @ScenarioState( resolution = NAME )
        SomeType oneType;

        @ScenarioState( resolution = NAME )
        SomeType secondType;

        public void something() {}
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_before_method_is_propagated() {
        addStage( TestStageWithExceptionInBeforeScenario.class );
        given().something();
    }

    public static class TestStageWithExceptionInBeforeScenario {
        @BeforeScenario
        public void throwException() {
            throw new IllegalStateException( "BeforeScenario" );
        }
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_after_method_is_propagated() throws Throwable {
        addStage( TestStageWithExceptionInAfterScenario.class );
        given().something();
        getScenario().getExecutor().finished();
    }

    public static class TestStageWithExceptionInAfterScenario {
        @AfterScenario
        public void throwException() {
            throw new IllegalStateException( "AfterScenario" );
        }
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_before_rule_method_is_propagated() throws Throwable {
        addStage( TestStageWithRuleThatThrowsExceptionInBefore.class );
        given().something();
    }

    public static class TestStageWithRuleThatThrowsExceptionInBefore {
        @ScenarioRule
        RuleThatThrowsExceptionInBefore rule = new RuleThatThrowsExceptionInBefore();
    }

    public static class RuleThatThrowsExceptionInBefore {
        public void before() {
            throw new IllegalStateException( "BeforeRule" );
        }
    }

    @Test( expected = IllegalStateException.class )
    public void exception_in_after_rule_method_is_propagated() throws Throwable {
        addStage( TestStageWithRuleThatThrowsExceptionInAfter.class );
        given().something();
        getScenario().getExecutor().finished();
    }

    public static class TestStageWithRuleThatThrowsExceptionInAfter {
        @ScenarioRule
        RuleThatThrowsExceptionInAfter rule = new RuleThatThrowsExceptionInAfter();
    }

    public static class RuleThatThrowsExceptionInAfter {
        public void after() {
            throw new IllegalStateException( "AfterRule" );
        }
    }

    @SuppressWarnings( "serial" )
    static class SomeExceptionInAfterStage extends RuntimeException {}

    static class AssertionInAfterStage extends Stage<AssertionInAfterStage> {
        @AfterStage
        public void after() {
            throw new SomeExceptionInAfterStage();
        }

        public void something() {}
    }

    @Test( expected = SomeExceptionInAfterStage.class )
    public void AfterStage_methods_of_the_last_stage_are_executed() throws Throwable {
        AssertionInAfterStage stage = addStage( AssertionInAfterStage.class );
        given().something();
        stage.then().something();

        // we have to call finish here because the exception is otherwise
        // thrown too late for the expected annotation
        getScenario().finished();
    }

    @Test
    public void After_methods_are_called_even_if_step_fails() throws Throwable {
        given().someFailingStep();

        try {
            given().afterScenarioCalled = 0;
            getScenario().finished();
        } catch( IllegalStateException e ) {
            assertThat( e.getMessage() ).isEqualTo( "failed step" );
        }

        assertThat( given().afterCalled ).isEqualTo( 1 );
        assertThat( given().afterScenarioCalled ).isEqualTo( 1 );
        assertThat( given().rule.afterCalled ).isEqualTo( 1 );
    }

    @Test
    @ConfiguredTag
    public void configured_tags_are_reported() throws Throwable {
        given().something();
        getScenario().finished();
        List<Tag> tags = getScenario().getModel().getLastScenarioModel().getTags();
        assertThat( tags ).isNotEmpty();
        Tag tag = tags.get( 0 );
        assertThat( tag ).isNotNull();
        assertThat( tag.getName() ).isEqualTo( "ConfiguredTag" );
    }

    @Test
    @Description( "@Description annotations are evaluated" )
    public void description_annotations_are_evaluated() throws Throwable {
        given().something();
        getScenario().finished();
        String description = getScenario().getModel().getLastScenarioModel().description;
        assertThat( description ).isEqualTo( "@Description annotations are evaluated" );
    }

}
