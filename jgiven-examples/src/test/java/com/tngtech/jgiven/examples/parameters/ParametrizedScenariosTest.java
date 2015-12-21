package com.tngtech.jgiven.examples.parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Strings;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import com.tngtech.jgiven.annotation.CaseDescription;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@RunWith( DataProviderRunner.class )
public class ParametrizedScenariosTest extends SimpleScenarioTest<ParameterFormattingTest.TestSteps> {

    @Test
    @DataProvider( {
        "true",
        "false"
    } )
    public void multiple_cases_are_reported_if_a_data_table_cannot_be_generated( boolean value ) {
        if( value ) {
            given().the_power_light_$_on( true );
        } else {
            given().a_machine_that_is( true );
        }
    }

    @Test
    @DataProvider( {
        "This is the first case, true",
        "This is another case, false"
    } )
    @CaseDescription( "$0" )
    public void cases_can_have_custom_descriptions( String description, boolean value ) {
        if( value ) {
            given().the_power_light_$_on( true );
        } else {
            given().a_machine_that_is( true );
        }
    }

    @Test
    @DataProvider( {
        "This is the first case, true",
        "This is another case, false"
    } )
    @CaseDescription( "$0" )
    public void custom_descriptions_of_cases_appear_as_a_separate_column_in_the_data_table( String description, boolean value ) {
        given().the_power_light_$_on( value );
    }

    @Test
    @DataProvider( {
        "1",
        "2"
    } )
    @CaseDescription( "$0" )
    public void parameter_values_with_very_long_text_are_truncated_in_the_report( int caseNr ) {
        given().a_very_long_parameter_value( "" + caseNr + Strings.repeat( "x", 4000 ) );
    }

    @DataProvider
    public static Object[][] manyValues() {
        Object[][] result = new Object[100][];

        for( int i = 0; i < result.length; i++ ) {

            result[i] = new Object[] { "some grouping value " + ( i / 10 ), "value " + ( i % 10 ) };
        }

        return result;
    }

    @Test
    @ExtendedDescription( "This scenario shows how large case tables are shown in JGiven. As soon as a table has more than 2 entries,"
            + " grouping by values is possible. This scenario also has some failing steps for demonstration purposes."
            + "<p>Btw. this description was created with the <a target='_blank' href='http://jgiven.org/javadoc/com/tngtech/jgiven/annotation/ExtendedDescription.html'>@ExtendedDescription</a> annotation" )
    @UseDataProvider( "manyValues" )
    public void a_scenario_with_many_cases( String grouping, String value ) {
        given().some_group_value( grouping )
            .and().another_value( value );
    }
}
