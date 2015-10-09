package com.tngtech.jgiven.examples.parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.annotation.CaseDescription;
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
}
