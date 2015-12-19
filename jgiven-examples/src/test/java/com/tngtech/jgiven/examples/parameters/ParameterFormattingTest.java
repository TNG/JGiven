package com.tngtech.jgiven.examples.parameters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Format;
import com.tngtech.jgiven.format.BooleanFormatter;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@RunWith( DataProviderRunner.class )
public class ParameterFormattingTest extends SimpleScenarioTest<ParameterFormattingTest.TestSteps> {

    @Test
    @DataProvider( {
        "true, true",
        "false, false"
    } )
    public void parameters_can_be_formatted( boolean onOff, boolean isOrIsNot ) {

        given().a_machine_that_is( onOff );
        then().the_power_light_$_on( isOrIsNot );

    }

    public static class TestSteps extends Stage<TestSteps> {

        public void a_machine_that_is( @Format( value = BooleanFormatter.class, args = { "on", "off" } ) boolean onOff ) {}

        public void the_power_light_$_on( @Format( value = BooleanFormatter.class, args = { "is", "is not" } ) boolean isOrIsNot ) {}

        public void a_very_long_parameter_value( String x ) {

        }

        public TestSteps some_group_value( String grouping ) {
            return this;
        }

        public void another_value( String value ) {
            assertThat( value ).doesNotContain( "5" );
        }
    }

}
