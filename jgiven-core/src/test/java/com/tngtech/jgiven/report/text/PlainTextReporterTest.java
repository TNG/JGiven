package com.tngtech.jgiven.report.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.GivenTestStep;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.format.BooleanFormatter;

/**
 * Please note that we do explicitly <strong>not</strong> use the ScenarioTest class for JUnit,
 * as this would require a dependency to jgiven-junit, which we have to avoid
 * here.
 */
@RunWith( DataProviderRunner.class )
public class PlainTextReporterTest extends ScenarioTestBase<GivenTestStep, WhenTestStep, ThenTestStep> {
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
    public void parameters_are_reported_correctly( int a, int b, int expectedResult ) throws Exception {
        getScenario().startScenario( "values can be multiplied" );

        given().$d_and_$d( a, b );
        when().both_values_are_multiplied_with_each_other();
        then().the_result_is( expectedResult );

        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string )
            .contains( "Given " + a + " and " + b )
            .contains( "When both values are multiplied with each other" )
            .contains( "Then the result is " + expectedResult );
    }

    @Test
    public void plain_text_report_works_as_expected() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );

        given().something()
            .and().something_else();

        when().something_happens();

        then().something_has_happen()
            .but().something_else_not();

        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string )
            .contains( ""
                    + " Scenario: Test\n"
                    + "\n"
                    + "   Given something\n"
                    + "     And something else\n"
                    + "    When something happens\n"
                    + "    Then something has happen\n"
                    + "     But something else not"
            );
    }

    @Test
    public void parameters_are_correctly_replaced_if_there_is_an_intro_word() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        GivenTestStep stage = getScenario().addStage( GivenTestStep.class );
        stage.given().a_step_with_a_$_parameter( "test" );
        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string ).contains( "Given a step with a test parameter" );
    }

    @Test
    public void parameters_are_correctly_replaced_if_there_is_no_intro_word() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        GivenTestStep stage = getScenario().addStage( GivenTestStep.class );
        stage.a_step_with_a_$_parameter( "test" );
        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string ).contains( "a step with a test parameter" );
    }

    @Test
    public void formatter_are_applied_to_arguments() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        GivenTestStep stage = getScenario().addStage( GivenTestStep.class );
        stage.a_step_with_a_boolean_$_parameter( true );
        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string ).contains( "a step with a boolean yes parameter" );
    }

    @Format( value = BooleanFormatter.class, args = { "yes", "no" } )
    @Retention( RetentionPolicy.RUNTIME )
    @interface YesNo {}

    static class FormattedSteps {

        public void yesno_$_formatted( @YesNo boolean b ) {}

        public void quoted_$_test( @Quoted String s ) {}
    }

    @Test
    public void formatter_annotations_are_applied_to_arguments() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        FormattedSteps stage = getScenario().addStage( FormattedSteps.class );

        stage.yesno_$_formatted( true );
        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string ).contains( "yesno yes formatted" );
    }

    @Test
    public void quoted_is_working() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        FormattedSteps stage = getScenario().addStage( FormattedSteps.class );

        stage.quoted_$_test( "foo" );
        String string = PlainTextReporter.toString( getScenario().getModel() );
        assertThat( string ).contains( "quoted \"foo\" test" );
    }

}
