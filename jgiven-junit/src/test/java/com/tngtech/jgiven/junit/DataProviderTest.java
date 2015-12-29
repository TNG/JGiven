package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.annotation.CaseDescription;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.format.BooleanFormatter;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import com.tngtech.jgiven.report.analysis.CaseArgumentAnalyser;
import com.tngtech.jgiven.report.model.ScenarioCaseModel;
import com.tngtech.jgiven.report.model.ScenarioModel;
import com.tngtech.jgiven.report.model.Word;

@RunWith( DataProviderRunner.class )
public class DataProviderTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @DataProvider
    public static Object[][] dataProvider() {
        return new Object[][] {
            { -2, false, 0 },
            { 22, true, 1 } };
    }

    @Test
    @UseDataProvider( "dataProvider" )
    public void DataProviderRunner_can_be_used( int intArg, boolean booleanArg, int caseNr ) {
        given().some_integer_value( intArg )
            .and().some_boolean_value( booleanArg );
        when().multiply_with_two();
        then().the_value_is_$not_greater_than_zero( booleanArg );

        ScenarioCaseModel scenarioModel = getScenario().getScenarioCaseModel();
        List<String> arguments = scenarioModel.getExplicitArguments();
        assertThat( arguments ).containsExactly( "" + intArg, "" + booleanArg, "" + caseNr );
    }

    @Test
    @DataProvider( { "0", "1" } )
    public void underlines_in_parameters_are_replaced_with_spaces( int int_arg ) {
        given().some_integer_value( int_arg );

        List<String> explicitParameters = getScenario().getScenarioModel().getExplicitParameters();
        assertThat( explicitParameters ).containsExactly( "int arg" );
    }

    @DataProvider
    public static Object[][] trickyData() {
        return new Object[][] {
            { 0, 0, 0 },
            { 0, 1, 0 },
            { 0, 0, 1 },
        };
    }

    @Test
    @UseDataProvider( "trickyData" )
    public void DataProviderRunner_with_tricky_data( int firstArg, int secondArg, int thirdArg ) {
        given().some_integer_value( firstArg )
            .and().another_integer_value( secondArg )
            .and().a_third_integer_value( thirdArg );

        when().multiply_with_two();

        ScenarioModel scenarioModel = getScenario().getScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 3 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            Word word = scenarioModel.getCase( 0 ).getStep( 0 ).getWord( 2 );
            assertThat( word.isArg() ).isTrue();
            assertThat( word.getArgumentInfo().isParameter() ).isFalse();
            assertParameter( scenarioModel.getCase( 0 ), 1, scenarioModel.getExplicitParameters().get( 1 ) );
            assertParameter( scenarioModel.getCase( 0 ), 2, scenarioModel.getExplicitParameters().get( 2 ) );
        }
    }

    private void assertParameter( ScenarioCaseModel case0, int step, String parameter ) {
        Word word = case0.getStep( step ).getWords().get( 2 );
        assertThat( word.getArgumentInfo().getParameterName() ).isEqualTo( parameter );
    }

    @Test
    @DataProvider( { "1", "2", "3" } )
    public void derived_parameters_work( Integer arg ) {
        given().some_integer_value( arg )
            .and().another_integer_value( arg * 10 );

        when().multiply_with_two();

        ScenarioModel scenarioModel = getScenario().getScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 3 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            ScenarioCaseModel case0 = scenarioModel.getCase( 0 );
            assertParameter( case0, 0, scenarioModel.getExplicitParameters().get( 0 ) );
            assertParameter( case0, 1, "secondArg" );
        }
    }

    @Test
    @DataProvider( { "1", "2" } )
    public void arguments_with_the_same_name_but_different_values_are_handled_correctly( Integer arg ) throws Throwable {
        given().some_integer_value( arg + 1 )
            .and().some_integer_value( arg + 2 );

        getScenario().finished();

        ScenarioModel scenarioModel = getScenario().getModel().getLastScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 2 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            ScenarioCaseModel case0 = scenarioModel.getCase( 0 );
            assertParameter( case0, 0, "someIntValue" );
            assertParameter( case0, 1, "someIntValue2" );
        }
    }

    @Test
    @DataProvider( { "1", "2" } )
    public void table_parameters_work_with_primitive_arrays( Integer arg ) {
        given().a_step_with_a_table_parameter_and_primitive_array( 1, 2, 3 );
    }

    @Test
    @DataProvider( { "true", "false" } )
    public void parameters_of_methods_can_be_formatted( @Format( value = BooleanFormatter.class, args = { "foo", "bar" } ) boolean b )
            throws Throwable {
        given().some_boolean_value( b );
        if( b ) {
            when().something();
        }

        getScenario().finished();

        List<ScenarioCaseModel> cases = getScenario().getModel().getLastScenarioModel().getScenarioCases();
        assertThat( cases.get( cases.size() - 1 ).getExplicitArguments() ).containsExactly( b ? "foo" : "bar" );
    }

    @Test
    @DataProvider( { "the first case, true", "the second case, false" } )
    @CaseDescription( "$0" )
    public void parameters_can_be_treated_as_case_description( String description, boolean b ) throws Throwable {

        given().something();

        getScenario().finished();

        List<ScenarioCaseModel> cases = getScenario().getModel().getLastScenarioModel().getScenarioCases();
        assertThat( cases.get( cases.size() - 1 ).getDescription() ).isEqualTo( description );

    }

    @Test
    @DataProvider( { "0", "1" } )
    public void duration_of_multiple_cases_is_summed_up( int nr ) throws Throwable {

        given().something();

        getScenario().finished();

        if( nr == 0 ) {
            getScenario().getModel().getLastScenarioModel().setDurationInNanos( 0 );
        } else {
            assertThat( getScenario().getModel().getLastScenarioModel().getDurationInNanos() ).isNotEqualTo( 0 );
        }
    }

    @Test
    @DataProvider( {
        "foo",
        "bar"
    } )
    public void two_identically_formatted_arguments_should_be_unified_in_one_parameter( @Quoted String arg ) throws Throwable {
        given().some_quoted_string_value( arg )
            .and().another_quoted_string_value( arg );

        getScenario().finished();
        ScenarioModel scenarioModel = getScenario().getModel().getLastScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 2 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            assertThat( scenarioModel.getDerivedParameters() ).hasSize( 1 );
            assertThat( scenarioModel.getDerivedParameters().get( 0 ) ).isEqualTo( "arg" );
        }
    }

    @Test
    @DataProvider( {
        "foo",
        "bar"
    } )
    public void two_differently_formatted_arguments_but_with_the_same_value_should_become_two_parameters( String param ) throws Throwable {
        given().some_quoted_string_value( param )
            .and().some_string_value( param );

        getScenario().finished();
        ScenarioModel scenarioModel = getScenario().getModel().getLastScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 2 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            assertThat( scenarioModel.getDerivedParameters() ).containsExactly( "someQuotedStringValue", "someStringValue" );
        }
    }

    @Test
    @DataProvider( {
        "foo",
        "bar"
    } )
    public void different_data_tables_lead_to_different_cases( String param ) throws Throwable {
        GivenTestStep.TableClass table = new GivenTestStep.TableClass();
        table.value = param;
        given().some_data_table( table );

        getScenario().finished();
        ScenarioModel scenarioModel = getScenario().getModel().getLastScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 2 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            assertThat( scenarioModel.isCasesAsTable() ).isFalse();
        }
    }

    @Test
    @DataProvider( {
        "foo",
        "bar"
    } )
    public void equal_data_tables_are_handled_correctly( String param ) throws Throwable {
        GivenTestStep.TableClass table = new GivenTestStep.TableClass();
        table.value = "same value in all cases";
        given().some_data_table( table );

        getScenario().finished();
        ScenarioModel scenarioModel = getScenario().getModel().getLastScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 2 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            assertThat( scenarioModel.isCasesAsTable() ).isTrue();
            assertThat( scenarioModel.getDerivedParameters() ).isEmpty();
        }
    }

    @Test
    @DataProvider( { "", "foo" } )
    public void some_scenario( String value ) throws Throwable {
        given().some_string_value( value );

        getScenario().finished();
        ScenarioModel scenarioModel = getScenario().getModel().getLastScenarioModel();
        if( scenarioModel.getScenarioCases().size() == 2 ) {
            CaseArgumentAnalyser analyser = new CaseArgumentAnalyser();
            analyser.analyze( scenarioModel );
            assertThat( scenarioModel.isCasesAsTable() ).isTrue();
        }
    }

}
