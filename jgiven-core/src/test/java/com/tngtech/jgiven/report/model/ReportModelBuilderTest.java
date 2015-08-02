package com.tngtech.jgiven.report.model;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.collect.Lists;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.GivenTestStep;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.base.ScenarioTestBase;

@RunWith( DataProviderRunner.class )
public class ReportModelBuilderTest extends ScenarioTestBase<GivenTestStep, WhenTestStep, ThenTestStep> {

    private ReportModelBuilder reportModelBuilder;

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
        getScenario().startScenario( description );

        given().$d_and_$d( a, b );
        when().both_values_are_multiplied_with_each_other();
        then().the_result_is( expectedResult );

        getScenario().finished();
        ScenarioModel model = getScenario().getModel().getLastScenarioModel();

        assertThat( model.getDescription() ).isEqualTo( description );

        ScenarioCaseModel case0 = model.getCase( 0 );
        assertThat( case0.success ).isTrue();
        assertThat( case0.getCaseNr() ).isEqualTo( 1 );
        assertThat( case0.getExplicitArguments() ).isEmpty();
        assertThat( case0.getSteps() ).hasSize( 3 );
        assertThat( case0.getSteps() ).extracting( "failed" ).isEqualTo( asList( false, false, false ) );
        assertThat( case0.getSteps() ).extracting( "notImplementedYet" ).isEqualTo( asList( false, false, false ) );
        assertThat( case0.getSteps() ).extracting( "skipped" ).isEqualTo( asList( false, false, false ) );

        StepModel step0 = case0.getSteps().get( 0 );
        assertThat( step0.words ).hasSize( 4 );
        assertThat( step0.getCompleteSentence() ).isEqualTo( "Given " + a + " and " + b );
        assertThat( extractIsArg( step0.words ) ).isEqualTo( Arrays.asList( false, true, false, true ) );

        StepModel step2 = case0.getSteps().get( 2 );
        assertThat( step2.words ).hasSize( 3 );
        assertThat( extractIsArg( step2.words ) ).isEqualTo( Arrays.asList( false, false, true ) );
    }

    public static List<Boolean> extractIsArg( List<Word> words ) {
        ArrayList<Boolean> result = Lists.newArrayList();
        for( Word word : words ) {
            result.add( word.isArg() );
        }
        return result;
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithoutValue {}

    @AnnotationWithoutValue
    static class AnnotationTestClass {}

    @Test
    public void testAnnotationParsing() throws Exception {
        List<Tag> tags = new ReportModelBuilder().toTags( AnnotationTestClass.class.getAnnotations()[0] );
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
        List<Tag> tags = new ReportModelBuilder().toTags( AnnotationWithSingleValueTestClass.class.getAnnotations()[0] );
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
        ReportModelBuilder modelBuilder = new ReportModelBuilder();
        List<Tag> tags = modelBuilder.toTags( AnnotationWithNameTestClass.class.getAnnotations()[0] );
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
        ReportModelBuilder modelBuilder = new ReportModelBuilder();
        List<Tag> tags = modelBuilder.toTags( AnnotationWithIgnoredValueTestClass.class.getAnnotations()[0] );
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
        List<Tag> tags = new ReportModelBuilder().toTags( AnnotationWithArrayValueTestClass.class.getAnnotations()[0] );
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
        List<Tag> tags = new ReportModelBuilder().toTags( AnnotationWithoutExplodedArrayValueTestClass.class.getAnnotations()[0] );
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
        List<Tag> tags = new ReportModelBuilder().toTags( AnnotationWithDescription.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getDescription() ).isEqualTo( "Some Description" );
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
        getScenario().startScenario( "test" );

        given().an_array( argument );

        getScenario().finished();
        StepModel step = getScenario().getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 2 ).getValue() ).isEqualTo( expected );
    }

    @Test
    public void the_Description_annotation_is_evaluated() throws Throwable {
        getScenario().startScenario( "Scenario with a @Description tag" );
        given().a_step_with_a_description();
        getScenario().finished();
        StepModel step = getScenario().getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 1 ).getValue() ).isEqualTo( "a step with a (special) description" );
    }

    @Test
    public void the_Description_annotation_on_intro_words_is_evaluated() throws Throwable {
        getScenario().startScenario( "Scenario with an @As annotaiton" );
        given().an_intro_word_with_an_as_annotation().something();
        getScenario().finished();
        StepModel step = getScenario().getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 0 ).getValue() ).isEqualTo( "another description" );
    }

    @Test
    public void printf_annotation_uses_the_PrintfFormatter() throws Throwable {
        getScenario().startScenario( "printf_annotation_uses_the_PrintfFormatter" );
        given().a_step_with_a_printf_annotation_$( 5.2 );
        getScenario().finished();
        StepModel step = getScenario().getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 2 ).getFormattedValue() ).isEqualTo( String.format( "%.2f", 5.2 ) );
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
        getScenario().startScenario( "Test" );
        stage.abstract_step();
        getScenario().finished();
        StepModel step = getScenario().getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 0 ).getFormattedValue() ).isEqualTo( "abstract step" );
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
        reportModelBuilder = new ReportModelBuilder();
        List<Tag> tags = reportModelBuilder.toTags( AnnotationWithParentTag.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).getTags() ).containsAll( Arrays.asList(
            "ParentTag", "ParentTagWithValue-SomeValue" ) );
    }

}
