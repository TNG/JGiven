package com.tngtech.jgiven.junit;

import static com.tngtech.jgiven.annotation.ScenarioState.Resolution.NAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.CurrentScenario;
import com.tngtech.jgiven.CurrentStep;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.attachment.Attachment;
import com.tngtech.jgiven.attachment.MediaType;
import com.tngtech.jgiven.exception.AmbiguousResolutionException;
import com.tngtech.jgiven.junit.tags.ConfiguredTag;
import com.tngtech.jgiven.junit.test.BeforeAfterTestStage;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.model.AttachmentModel;

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
        List<String> tagIds = getScenario().getScenarioModel().getTagIds();
        assertThat( tagIds ).isNotEmpty();
        String tagId = tagIds.get( 0 );
        assertThat( tagId ).isNotNull();
        assertThat( tagId ).isEqualTo( "ConfiguredTag-Test" );
    }

    @Test
    @Description( "@Description annotations are evaluated" )
    public void description_annotations_are_evaluated() throws Throwable {
        given().something();
        getScenario().finished();
        String description = getScenario().getScenarioModel().getDescription();
        assertThat( description ).isEqualTo( "@Description annotations are evaluated" );
    }

    static class SomeStageProvidingAString {
        @ProvidedScenarioState
        String someString = "test";

        public void something() {}
    }

    static class SomeStageWithAHiddenMethod {
        @ExpectedScenarioState
        String someString;

        @Hidden
        public void someHiddenStep() {
            assertThat( someString ).isNotNull();
        }
    }

    @Test
    public void hidden_steps_see_injected_values() {
        SomeStageProvidingAString stage1 = addStage( SomeStageProvidingAString.class );
        SomeStageWithAHiddenMethod stage2 = addStage( SomeStageWithAHiddenMethod.class );

        stage1.something();
        stage2.someHiddenStep();
    }

    static class SomeStageWithABeforeMethod {
        @ExpectedScenarioState
        String someString;

        @BeforeStage
        public void someHiddenStep() {
            assertThat( someString ).isNotNull();
        }

        public void something() {}
    }

    @Test
    public void before_stage_methods_see_injected_values() {
        SomeStageProvidingAString stage1 = addStage( SomeStageProvidingAString.class );
        SomeStageWithABeforeMethod stage2 = addStage( SomeStageWithABeforeMethod.class );

        stage1.something();
        stage2.something();
    }

    static class AttachmentStepClass {
        @ScenarioState
        CurrentStep currentStep;

        public void add_attachment() {
            currentStep.addAttachment( Attachment.fromText( "FOOBAR", MediaType.PLAIN_TEXT ) );
        }

        public void set_description() {
            currentStep.setExtendedDescription( "An extended description" );
        }
    }

    @Test
    public void attachments_can_be_added_to_steps() {
        AttachmentStepClass steps = addStage( AttachmentStepClass.class );

        steps.add_attachment();

        List<AttachmentModel> attachments = getScenario().getScenarioCaseModel().getFirstStep().getAttachments();

        assertThat( attachments ).hasSize( 1 );
        assertThat( attachments.get( 0 ).getValue() ).isEqualTo( "FOOBAR" );
        assertThat( attachments.get( 0 ).getMediaType() ).isEqualTo( MediaType.PLAIN_TEXT.asString() );
    }

    @Test
    public void extended_descriptions_can_be_set_using_the_current_step() {
        AttachmentStepClass steps = addStage( AttachmentStepClass.class );

        steps.set_description();

        String description = getScenario().getScenarioCaseModel().getFirstStep().getExtendedDescription();

        assertThat( description ).isEqualTo( "An extended description" );
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface DynamicTag {}

    static class CurrentScenarioStage {
        @ScenarioState
        CurrentScenario currentScenario;

        public void add_tag() {
            currentScenario.addTag( DynamicTag.class, "value" );
        }
    }

    @Test
    public void tags_can_be_added_using_the_current_scenario() {
        CurrentScenarioStage steps = addStage( CurrentScenarioStage.class );

        steps.add_tag();

        List<String> tagIds = getScenario().getScenarioModel().getTagIds();
        assertThat( tagIds ).hasSize( 1 );
        assertThat( tagIds.get( 0 ) ).isEqualTo( "DynamicTag-value" );
    }

    static abstract class AbstractStage {
        public abstract void abstract_step();
    }

    static class ConcreteStage extends AbstractStage {
        @Override
        public void abstract_step() {}
    }

    @Test
    public void abstract_steps_should_appear_in_the_report_model() throws Throwable {
        ConcreteStage stage = addStage( ConcreteStage.class );
        stage.abstract_step();

    }

}
