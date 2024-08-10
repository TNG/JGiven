package com.tngtech.jgiven.examples;

import com.tngtech.jgiven.examples.tags.FailingOnPurpose;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.Issue;
import org.junit.Assume;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

@FailingOnPurpose
public class FailingScenarioTest extends SimpleScenarioTest<FailingScenarioTest.Steps> {



    @Test
    @Issue("#234")
    public void a_scenario_with_a_multi_line_error_message() {
        given().multi_line_error_message();
    }



    @Test
    @Issue("#1625")
    @FailingOnPurpose
    public void failing_assumption(){
       given().false_assumption();
    }

    public static class Steps {
        public Steps multi_line_error_message() {
            assertThat(true).as("This\nmessage\nhas\nmultiple lines").isFalse();
            return this;
        }

        public Steps false_assumption(){
            assumeThat(true).as("This assumption fails on purpose").isFalse();
            return this;
        }
    }
}
