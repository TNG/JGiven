package com.tngtech.jgiven.report.model;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    public void test( int a, int b, int expectedResult ) {
        String description = "values can be multiplied";
        scenario.startScenario( description );

        given().$d_and_$d( a, b );
        when().both_values_are_multiplied_with_each_other();
        then().the_result_is( expectedResult );

        scenario.finished();
        ScenarioModel model = scenario.getModel().getLastScenarioModel();

        assertThat( model.description ).isEqualTo( description );

        ScenarioCaseModel case0 = model.getCase( 0 );
        assertThat( case0.success ).isTrue();
        assertThat( case0.caseNr ).isEqualTo( 1 );
        assertThat( case0.arguments ).isEmpty();
        assertThat( case0.steps ).hasSize( 3 );
        assertThat( case0.steps ).extracting( "failed" ).isEqualTo( asList( false, false, false ) );
        assertThat( case0.steps ).extracting( "notImplementedYet" ).isEqualTo( asList( false, false, false ) );
        assertThat( case0.steps ).extracting( "skipped" ).isEqualTo( asList( false, false, false ) );

        StepModel step0 = case0.steps.get( 0 );
        assertThat( step0.words ).hasSize( 4 );
        assertThat( step0.getCompleteSentence() ).isEqualTo( "Given " + a + " and " + b );
        assertThat( step0.words ).extracting( "isArg" ).isEqualTo( Arrays.asList( false, true, false, true ) );

        StepModel step2 = case0.steps.get( 2 );
        assertThat( step2.words ).hasSize( 3 );
        assertThat( step2.words ).extracting( "isArg" ).isEqualTo( Arrays.asList( false, false, true ) );
    }

    @IsTag
    @Retention( RetentionPolicy.RUNTIME )
    @interface AnnotationWithoutValue {}

    @AnnotationWithoutValue
    static class AnnotationTestClass {}

    @Test
    public void testAnnotationParsing() throws Exception {
        List<Tag> tags = ReportModelBuilder.toTags( AnnotationTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).name ).isEqualTo( "AnnotationWithoutValue" );
        assertThat( tags.get( 0 ).value ).isEqualTo( null );
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
        List<Tag> tags = ReportModelBuilder.toTags( AnnotationWithSingleValueTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).name ).isEqualTo( "AnnotationWithSingleValue" );
        assertThat( tags.get( 0 ).value ).isEqualTo( "testvalue" );
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
        List<Tag> tags = ReportModelBuilder.toTags( AnnotationWithArrayValueTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 2 );
        assertThat( tags.get( 0 ).name ).isEqualTo( "AnnotationWithArray" );
        assertThat( tags.get( 0 ).value ).isEqualTo( "foo" );
        assertThat( tags.get( 1 ).name ).isEqualTo( "AnnotationWithArray" );
        assertThat( tags.get( 1 ).value ).isEqualTo( "bar" );
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
        List<Tag> tags = ReportModelBuilder.toTags( AnnotationWithoutExplodedArrayValueTestClass.class.getAnnotations()[0] );
        assertThat( tags ).hasSize( 1 );
        assertThat( tags.get( 0 ).name ).isEqualTo( "AnnotationWithoutExplodedArray" );
        assertThat( tags.get( 0 ).value ).isEqualTo( new String[] { "foo", "bar" } );
    }

    @DataProvider
    public static Object[][] argumentTestData() {
        return new Object[][] {
            { null, "null" },
            { "Foo", "Foo" },
            { 123, "123" },
            { true, "true" },
            { new String[] { "a" }, "[a]" },
            { new String[] {}, "[]" },
            { new String[][] { { "a", "b" }, { "c" } }, "[[a, b], [c]]" },
        };
    }

    @Test
    @UseDataProvider( "argumentTestData" )
    public void testArrayArguments( Object argument, String expected ) {
        scenario.startScenario( "test" );

        given().an_array( argument );

        scenario.finished();
        StepModel step = scenario.getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 2 ).value ).isEqualTo( expected );
    }

    @Test
    public void the_Description_annotation_is_evaluated() {
        scenario.startScenario( "Scenario with a @Description tag" );
        given().a_step_with_a_description();
        scenario.finished();
        StepModel step = scenario.getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 1 ).value ).isEqualTo( "a step with a (special) description" );
    }

    @Test
    public void printf_annotation_uses_the_PrintfFormatter() {
        scenario.startScenario( "printf_annotation_uses_the_PrintfFormatter" );
        given().a_step_with_a_printf_annotation_$( 5.2 );
        scenario.finished();
        StepModel step = scenario.getModel().getFirstStepModelOfLastScenario();
        assertThat( step.words.get( 2 ).value ).isEqualTo( "5.20" );
    }

    @Test
    public void testTagEquals() {
        assertThat( new Tag( "test", "1" ) ).isEqualTo( new Tag( "test", "1" ) );
    }
}
