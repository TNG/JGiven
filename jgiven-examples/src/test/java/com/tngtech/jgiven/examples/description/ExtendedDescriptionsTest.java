package com.tngtech.jgiven.examples.description;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.ExtendedDescription;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.Issue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith( JUnitParamsRunner.class )
@Issue("#236")
@Description( "Example for different possibilities to annotate test cases with extended descriptions" )
public class ExtendedDescriptionsTest extends SimpleScenarioTest<ExtendedDescriptionsTest.Annotations> {

    @Test
    public void steps_can_have_extended_descriptions() {
        given().some_boolean_value( true );
    }

    @Test
    public void steps_can_have_extended_descriptions_with_arguments() {
        given().some_int_value( 1 );
    }

    @Test
    public void steps_can_have_multiple_arguments_referenced_in_extended_descriptions() {
        given().some_bool_$_and_int_$_value( false, 0 );
    }

    @Test
    @Parameters ({
            "false, 0",
            "true, 1"
    })
    public void scenarios_with_multiple_argument_parameters_can_be_shown_via_click_on_table( boolean bool, int i ) {
        given().some_bool_$_and_int_$_value( bool, i );
        // TODO screenshot test & implement frontend "click on table"
    }

    public static class Annotations extends Stage<Annotations> {

        @ExtendedDescription( "This is a boolean value" )
        public Annotations some_boolean_value ( boolean bool ){
            return this;
        }

        @ExtendedDescription( "We can reference the first argument with $$ or $$1 : $" )
        public Annotations some_int_value ( int i ){
            return this;
        }

        @ExtendedDescription( "Different number of arguments can be referenced in different order - int : $2, bool : $1" )
        public Annotations some_bool_$_and_int_$_value ( boolean bool, int i ){
            return this;
        }
    }
}
