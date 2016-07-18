package com.tngtech.jgiven.impl;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.GivenTestStep;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.annotation.DoNotIntercept;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.report.model.ReportModel;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.StepModel;
import com.tngtech.jgiven.report.model.Tag;
import com.tngtech.jgiven.report.model.Word;

@RunWith( DataProviderRunner.class )
public class ScenarioModelBuilderTest extends ScenarioTestBase<GivenTestStep, WhenTestStep, ThenTestStep> {

    public ScenarioModelBuilder getScenarioModelBuilder() {
        ScenarioModelBuilder scenarioModelBuilder = new ScenarioModelBuilder();
        scenarioModelBuilder.setReportModel( new ReportModel() );
        return scenarioModelBuilder;
    }

    public void startScenario( String title ) {
        getScenario().setModel( new ReportModel() );
        getScenario().startScenario( title );
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithoutValue {}

    @AnnotationWithoutValue
    static class AnnotationTestClass {}

    @Test
    public void testAnnotationParsing() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getName() ).isEqualTo( "AnnotationWithoutValue" );
        assertThat( tags.get( 0 ).getValues() ).isEmpty();
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithSingleValue {
        String value();
    }

    @AnnotationWithSingleValue( "testvalue" )
    static class AnnotationWithSingleValueTestClass {}

    @Test
    public void testAnnotationWithValueParsing() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithSingleValueTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getName() ).isEqualTo( "AnnotationWithSingleValue" );
        assertThat( tags.get( 0 ).getValues() ).containsExactly( "testvalue" );
    }

    @IsTag( name = "AnotherName" )
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithName {}

    @AnnotationWithName( )
    static class AnnotationWithNameTestClass {}

    @Test
    public void testAnnotationWithName() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithNameTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        Tag tag = tags.get( 0 );
        assertThat( tag.getName() ).isEqualTo( "AnotherName" );
        assertThat( tag.getValues() ).isEmpty();
        assertThat( tag.toIdString() ).isEqualTo( "AnnotationWithName" );
    }

    @IsTag( ignoreValue = true )
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithIgnoredValue {
        String value();
    }

    @AnnotationWithIgnoredValue( "testvalue" )
    static class AnnotationWithIgnoredValueTestClass {}

    @Test
    public void testAnnotationWithIgnoredValueParsing() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithIgnoredValueTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        Tag tag = tags.get( 0 );
        assertThat( tag.getName() ).isEqualTo( "AnnotationWithIgnoredValue" );
        assertThat( tag.getValues() ).isEmpty();
        assertThat( tag.toIdString() ).isEqualTo( "AnnotationWithIgnoredValue" );
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithArray {
        String[] value();
    }

    @AnnotationWithArray( { "foo", "bar" } )
    static class AnnotationWithArrayValueTestClass {}

    @Test
    public void testAnnotationWithArrayParsing() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithArrayValueTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 2 );
        assertThat( tags.get( 0 ).getName() ).isEqualTo( "AnnotationWithArray" );
        assertThat( tags.get( 0 ).getValues() ).containsExactly( "foo" );
        assertThat( tags.get( 1 ).getName() ).isEqualTo( "AnnotationWithArray" );
        assertThat( tags.get( 1 ).getValues() ).containsExactly( "bar" );
    }

    @IsTag( explodeArray = false )
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithoutExplodedArray {
        String[] value();
    }

    @AnnotationWithoutExplodedArray( { "foo", "bar" } )
    static class AnnotationWithoutExplodedArrayValueTestClass {}

    @Test
    public void testAnnotationWithoutExplodedArrayParsing() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithoutExplodedArrayValueTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getName() ).isEqualTo( "AnnotationWithoutExplodedArray" );
        assertThat( tags.get( 0 ).getValues() ).containsExactly( "foo", "bar" );
    }

    @IsTag( description = "Some Description" )
    @Retention( RetentionPolicy.RUNTIME )
    @interface TagWithDescription {}

    @TagWithDescription
    static class AnnotationWithDescription {}

    @Test
    public void testAnnotationWithDescription() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithDescription.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getDescription() ).isEqualTo( "Some Description" );
    }

    @IsTag( description = "Some Description", ignoreValue = true )
    @Retention( RetentionPolicy.RUNTIME )
    @TagWithDescription
    @interface TagWithDescriptionAndIgnoreValue {
        String value();
    }

    @TagWithDescriptionAndIgnoreValue( value = "some value" )
    static class AnnotationWithDescriptionAndIgnoreValue {}

    @Test
    public void testAnnotationWithDescriptionAndIgnoreValue() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithDescriptionAndIgnoreValue.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getValues() ).isEmpty();
        assertThat( tags.get( 0 ).getDescription() ).isEqualTo( "Some Description" );
        assertThat( tags.get( 0 ).getTags() ).hasSize( 1 );
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface ParentTag {}

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface ParentTagWithValue {
        String value();
    }

    @ParentTagWithValue( "SomeValue" )
    @ParentTag
    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface TagWithParentTags {}

    @TagWithParentTags
    static class AnnotationWithParentTag {}

    @Test
    public void testAnnotationWithParentTag() throws Exception {
        List<Tag> tags = getScenarioModelBuilder().toTags( AnnotationWithParentTag.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getTags() ).containsAll( Arrays.asList(
            "ParentTag", "ParentTagWithValue-SomeValue" ) );
    }

    @IsTag( value = "default" )
    @Retention( RetentionPolicy.RUNTIME )
    @interface DynamicTag {}

    @Test
    public void testAddTagDynamically() throws Exception {
        ReportModel reportModel = new ReportModel();
        ScenarioModelBuilder scenarioModelBuilder = getScenarioModelBuilder();
        scenarioModelBuilder.setReportModel( reportModel );
        scenarioModelBuilder.scenarioStarted( "Test" );

        scenarioModelBuilder.tagAdded( DynamicTag.class, "A", "B" );
        scenarioModelBuilder.tagAdded( DynamicTag.class );
        assertThat( reportModel.getTagMap() ).hasSize( 3 );

        Iterator<Tag> iterator = reportModel.getTagMap().values().iterator();
        assertThat( iterator.next().getValues() ).containsExactly( "A" );
        assertThat( iterator.next().getValues() ).containsExactly( "B" );
        assertThat( iterator.next().getValues() ).containsExactly( "default" );
    }

    @DataProvider
    public static Object[][] testData() {
        return new Object[][] {
            { 5, 6, 30 },
            { 2, 2, 4 },
            { -5, 1, -5 },
        };
    }

    @Test
    @UseDataProvider( "testData" )
    public void test( int a, int b, int expectedResult ) throws Throwable {
        String description = "values can be multiplied";
        startScenario( description );

        given().$d_and_$d( a, b );
        when().both_values_are_multiplied_with_each_other();
        then().the_result_is( expectedResult );

        getScenario().finished();
        ScenarioModel model = getScenario().getScenarioModel();

        assertThat( model.getDescription() ).isEqualTo( description );

        ScenarioCaseModel case0 = model.getCase( 0 );
        assertThat( case0.isSuccess() ).isTrue();
        assertThat( case0.getCaseNr() ).isEqualTo( 1 );
        assertThat( case0.getExplicitArguments() ).isEmpty();
        assertThat( case0.getSteps() ).hasSize( 3 );
        assertThat( case0.getSteps() ).extracting( "failed" ).isEqualTo( asList( false, false, false ) );
        assertThat( case0.getSteps() ).extracting( "pending" ).isEqualTo( asList( false, false, false ) );
        assertThat( case0.getSteps() ).extracting( "skipped" ).isEqualTo( asList( false, false, false ) );

        StepModel step0 = case0.getSteps().get( 0 );
        assertThat( step0.getWords() ).hasSize( 4 );
        assertThat( step0.getCompleteSentence() ).isEqualTo( "Given " + a + " and " + b );
        assertThat( extractIsArg( step0.getWords() ) ).isEqualTo( Arrays.asList( false, true, false, true ) );

        StepModel step2 = case0.getSteps().get( 2 );
        assertThat( step2.getWords() ).hasSize( 3 );
        assertThat( extractIsArg( step2.getWords() ) ).isEqualTo( Arrays.asList( false, false, true ) );
    }

    public static List<Boolean> extractIsArg( List<Word> words ) {
        ArrayList<Boolean> result = Lists.newArrayList();
        for( Word word : words ) {
            result.add( word.isArg() );
        }
        return result;
    }

    @DataProvider
    public static Object[][] argumentTestData() {
        return new Object[][] {
            { null, "null" },
            { "Foo", "Foo" },
            { 123, "123" },
            { true, "true" },
            { new String[] { "a" }, "a" },
            { new String[] {}, "" },
            { new String[][] { { "a", "b" }, { "c" } }, "a, b, c" },
        };
    }

    @Test
    @UseDataProvider( "argumentTestData" )
    public void testArrayArguments( Object argument, String expected ) throws Throwable {
        startScenario( "test" );

        given().an_array( argument );

        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getWords().get( 2 ).getValue() ).isEqualTo( expected );
    }

    @Test
    public void characters_are_not_dropped_when_using_the_As_annotation() throws Throwable {
        startScenario( "Scenario with a @As tag" );
        given().a_step_with_a_bracket_after_a_dollar( 42 );
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getCompleteSentence() ).isEqualTo( "Given a step with a bracket after a dollar 42 ]" );
    }

    @Test
    public void a_custom_AsProvider_can_be_used() throws Throwable {
        startScenario( "Scenario with a @As tag" );
        given().a_step_with_an_As_annotation_and_a_custom_provider();
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getCompleteSentence() )
            .isEqualTo( "Given Custom AsProvider output: a_step_with_an_As_annotation_and_a_custom_provider" );
    }

    @Test
    public void camel_case_is_supported_in_steps() throws Throwable {
        startScenario( "Scenario camel case steps" );
        given().aStepInCamelCase();
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getCompleteSentence() ).isEqualTo( "Given a step in camel case" );
    }

    @Test
    public void camel_case_is_supported_in_steps_with_parameters() throws Throwable {
        startScenario( "Scenario camel case steps with parameter" );
        given().aStepInCamelCaseWithA$Parameter( "dollar" );
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getCompleteSentence() ).isEqualTo( "Given a step in camel case with a dollar parameter" );
    }

    @Test
    public void all_uppercase_steps_are_formatted_correctly() throws Throwable {
        startScenario( "Scenario with all uppercase step" );
        given().ALLUPPERCASE();
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getCompleteSentence() ).isEqualTo( "Given ALLUPPERCASE" );
    }

    @Test
    public void the_Description_annotation_on_intro_words_is_evaluated() throws Throwable {
        startScenario( "Scenario with an @As annotation" );
        given().an_intro_word_with_an_as_annotation().something();
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getWords().get( 0 ).getValue() ).isEqualTo( "another description" );
    }

    @Test
    public void printf_annotation_uses_the_PrintfFormatter() throws Throwable {
        startScenario( "printf_annotation_uses_the_PrintfFormatter" );
        given().a_step_with_a_printf_annotation_$( 5.2 );
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getWords().get( 2 ).getFormattedValue() ).isEqualTo( String.format( "%.2f", 5.2 ) );
    }

    @Test
    public void testTagEquals() {
        assertThat( new Tag( "test", "1" ) ).isEqualTo( new Tag( "test", "1" ) );
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
        startScenario( "Test" );
        stage.abstract_step();
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getWords().get( 0 ).getFormattedValue() ).isEqualTo( "abstract step" );
    }

    @Test
    public void DoNotIntercept_method_are_not_appearing_in_the_report() throws Throwable {
        DoNotInterceptClass stage = addStage( DoNotInterceptClass.class );
        startScenario( "Test" );
        stage.do_not_intercept();
        stage.normal_step();
        getScenario().finished();
        StepModel step = getScenario().getScenarioCaseModel().getFirstStep();
        assertThat( step.getWords().get( 0 ).getFormattedValue() ).isEqualTo( "normal step" );
    }

    static class DoNotInterceptClass {
        @DoNotIntercept
        public void do_not_intercept() {}

        public void normal_step() {}
    }

    @Test
    public void error_message_is_correctly_stored() throws Throwable {
        FailingTestStage stage = addStage( FailingTestStage.class );
        startScenario( "Test" );
        stage.a_failing_test();
        try {
            getScenario().finished();
        } catch( Exception ignore ) {

        }
        ScenarioCaseModel scenarioCaseModel = getScenario().getScenarioCaseModel();
        assertThat( scenarioCaseModel.getErrorMessage() ).isEqualTo( "java.lang.IllegalArgumentException: test error" );
        assertThat( scenarioCaseModel.getStackTrace().get( 0 ) ).matches(
            "com.tngtech.jgiven.impl.ScenarioModelBuilderTest\\$FailingTestStage.a_failing_test\\(ScenarioModelBuilderTest.java:\\d+\\)" );
    }

    static class FailingTestStage {
        public void a_failing_test() {
            throw new IllegalArgumentException( "test error" );
        }
    }
}
