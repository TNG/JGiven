package com.tngtech.jgiven.examples.pending;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.FeaturePending;

@RunWith( DataProviderRunner.class )
@FeaturePending
@Description( "As a good BDD practitioner,<br>"
        + "I want to write my scenarios before I start coding<br>"
        + "In order to discuss them with business stakeholders" )
public class PendingExampleTest extends SimpleScenarioTest<PendingExampleTest.TestSteps> {

    @Test
    @Pending
    public void scenarios_that_are_pending_can_be_annotated_with_the_Pending_annotation() {
        given().some_state();
        when().some_action();
        then().some_result();
    }

    @Test
    public void single_steps_can_be_annotated_with_Pending() {
        given().some_state();
        when().some_pending_action();
        then().some_result();
    }

    @Test
    @Pending
    @DataProvider( { "1st", "2nd" } )
    public void multiple_cases_can_be_pending( String actionCount ) {
        given().some_state();
        when().a_$_action( actionCount );
        then().some_result();
    }

    public static class TestSteps {

        public TestSteps some_state() {
            return this;
        }

        public TestSteps some_result() {
            return this;
        }

        public TestSteps some_action() {
            return this;
        }

        public TestSteps a_$_action( String actionCount ) {
            return this;
        }

        @Pending
        public TestSteps some_pending_action() {
            return this;
        }
    }
}
