package com.tngtech.jgiven.report.text;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.GivenTestStep;
import com.tngtech.jgiven.ThenTestStep;
import com.tngtech.jgiven.WhenTestStep;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Formatf;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import com.tngtech.jgiven.format.BooleanFormatter;
import com.tngtech.jgiven.report.model.StepModel;

/**
 * Please note that we do explicitly <strong>not</strong> use the ScenarioTest class for JUnit,
 * as this would require a dependency to jgiven-junit, which we have to avoid
 * here.
 */
@RunWith( DataProviderRunner.class )
public class PlainTextReporterTest extends ScenarioTestBase<GivenTestStep, WhenTestStep, ThenTestStep> {

    @Rule
    public final ExpectedException expected = ExpectedException.none();

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

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
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

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string.replaceAll( System.getProperty( "line.separator" ), "\n" ) )
            .contains( ""
                    + " Test\n"
                    + "\n"
                    + "   Given something\n"
                    + "     And something else\n"
                    + "    When something happens\n"
                    + "    Then something has happen\n"
                    + "     But something else not" );
    }

    @Test
    public void sections_are_shown_correctly_in_the_plain_text_report() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );

        section( "A first section" );
        given().something()
            .and().something_else();

        when().something_happens();

        section( "Another section" );

        then().something_has_happen()
            .but().something_else_not();

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string.replaceAll( System.getProperty( "line.separator" ), "\n" ) )
            .contains( ""
                    + " Test\n"
                    + "\n"
                    + "   A first section\n"
                    + "\n"
                    + "   Given something\n"
                    + "     And something else\n"
                    + "    When something happens\n"
                    + "\n"
                    + "   Another section\n"
                    + "\n"
                    + "    Then something has happen\n"
                    + "     But something else not" );
    }

    @Test
    public void missing_intro_words_are_filled_with_spaces() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );

        given().something()
            .something_else();

        when().something_happens();

        then().something_has_happen()
            .something_else_not();

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string.replaceAll( System.getProperty( "line.separator" ), "\n" ) )
            .contains( ""
                    + " Test\n"
                    + "\n"
                    + "   Given something\n"
                    + "         something else\n"
                    + "    When something happens\n"
                    + "    Then something has happen\n"
                    + "         something else not" );
    }

    @Test
    public void nested_steps_are_displayed_in_the_report() throws Throwable {
        getScenario().startScenario( "test" );

        given().something_with_nested_steps();

        when().something_happens();

        then().something_has_happen()
            .something_else_not();

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string.replaceAll( System.getProperty( "line.separator" ), "\n" ) )
            .contains( ""
                    + " Test\n"
                    + "\n"
                    + "   Given something with nested steps\n"
                    + "           Given something\n"
                    + "           And something else\n"
                    + "    When something happens\n"
                    + "    Then something has happen\n"
                    + "         something else not" );

        StepModel parentStep = getScenario().getScenarioModel().getScenarioCases().get( 0 ).getStep( 0 );
        long nestedDurations = parentStep.getNestedSteps().get( 0 ).getDurationInNanos() +
                parentStep.getNestedSteps().get( 1 ).getDurationInNanos();
        assertThat( parentStep.getDurationInNanos() ).isGreaterThanOrEqualTo( nestedDurations );
    }

    @Test
    public void multilevel_nested_steps_are_displayed_in_the_report() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );

        given().something_with_multilevel_nested_steps();

        when().something_happens();

        then().something_has_happen()
            .something_else_not();

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string.replaceAll( System.getProperty( "line.separator" ), "\n" ) )
            .contains( ""
                    + " Test\n"
                    + "\n"
                    + "   Given something with multilevel nested steps\n"
                    + "           Given something with nested steps\n"
                    + "             Given something\n"
                    + "             And something else\n"
                    + "           And something further\n"
                    + "    When something happens\n"
                    + "    Then something has happen\n"
                    + "         something else not" );
    }

    @Test
    public void nested_step_failures_appear_in_the_top_level_enclosing_step() throws Throwable {
        getScenario().startScenario( "test" );

        given().something_with_nested_steps_that_fails();

        when().something_happens();

        then().something_has_happen()
            .something_else_not();

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string.replaceAll( System.getProperty( "line.separator" ), "\n" ) )
            .contains( ""
                    + " Test\n"
                    + "\n"
                    + "   Given something with nested steps that fails (failed)\n"
                    + "           Given something (passed)\n"
                    + "           And something else that fails (failed)\n"
                    + "           And something else (skipped)\n"
                    + "    When something happens (skipped)\n"
                    + "    Then something has happen (skipped)\n"
                    + "         something else not (skipped)" );
    }

    @Test
    public void parameters_are_correctly_replaced_if_there_is_an_intro_word() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        GivenTestStep stage = getScenario().addStage( GivenTestStep.class );
        stage.given().a_step_with_a_$_parameter( "test" );
        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string ).contains( "Given a step with a test parameter" );
    }

    @Test
    public void parameters_are_correctly_replaced_if_there_is_no_intro_word() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        GivenTestStep stage = getScenario().addStage( GivenTestStep.class );
        stage.a_step_with_a_$_parameter( "test" );
        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string ).contains( "a step with a test parameter" );
    }

    @Test
    public void formatter_are_applied_to_arguments() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        GivenTestStep stage = getScenario().addStage( GivenTestStep.class );
        stage.a_step_with_a_boolean_$_parameter( true );
        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string ).contains( "a step with a boolean yes parameter" );
    }

    @Format( value = BooleanFormatter.class, args = { "yes", "no" } )
    @Retention( RetentionPolicy.RUNTIME )
    @interface YesNo {}

    static class FormattedSteps {

        public void yesno_$_formatted( @YesNo boolean b ) {}

        public void quoted_$_test( @Quoted String s ) {}

        public void argument_$_multiple_formatters( @Formatf( "(%s)" ) @Quoted @YesNo boolean b ) {}

        public void argument_$_multiple_wrong_formatters( @YesNo @Formatf( "(%s)" ) @Quoted boolean b ) {}
    }

    @Test
    public void formatter_annotations_are_applied_to_arguments() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        FormattedSteps stage = getScenario().addStage( FormattedSteps.class );

        stage.yesno_$_formatted( true );
        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string ).contains( "yesno yes formatted" );
    }

    @Test
    public void quoted_is_working() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        FormattedSteps stage = getScenario().addStage( FormattedSteps.class );

        stage.quoted_$_test( "foo" );
        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string ).contains( "quoted \"foo\" test" );
    }

    @Test
    public void multiple_formatter_annotations_can_be_specified() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        FormattedSteps stage = getScenario().addStage( FormattedSteps.class );

        stage.argument_$_multiple_formatters( true );
        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string ).contains( "argument (\"yes\") multiple formatters" );
    }

    @Test
    public void chained_formatter_annotations_must_apply_to_strings() throws UnsupportedEncodingException {
        getScenario().startScenario( "test" );
        FormattedSteps stage = getScenario().addStage( FormattedSteps.class );

        expected.expect( JGivenWrongUsageException.class );
        stage.argument_$_multiple_wrong_formatters( true );
    }

    @Test
    public void substeps_access_are_not_printed_in_report() throws UnsupportedEncodingException {
        getScenario().startScenario( "substeps" );

        given().an_integer_value_set_in_a_substep( 4 );
        when().something_happens();
        then().the_substep_value_is( 4 )
            .and().the_substep_value_referred_in_the_step_is( 4 );

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );

        assertThat( string.replaceAll( System.getProperty( "line.separator" ), "\n" ) )
            .contains(
                "   Given an integer value set in a substep 4\n"
                        + "    When something happens\n"
                        + "    Then the substep value is 4\n"
                        + "     And the substep value referred in the step is 4" );
    }

    @Test
    public void step_comments_are_printed() throws UnsupportedEncodingException {
        getScenario().startScenario( "comments" );

        given().something().comment( "This is a comment." );

        String string = PlainTextReporter.toString( getScenario().getScenarioModel() );
        assertThat( string ).contains( "something [This is a comment.]" );
    }

}
