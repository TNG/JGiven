package com.tngtech.jgiven.examples.notimplementedyet;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.junit.SimpleScenarioTest;
import com.tngtech.jgiven.tags.FeatureNotImplementedYet;

@Description( "As a good BDD practitioner,<br>"
        + "I want to write my scenarios before I start coding<br>"
        + "In order to discuss them with business stakeholders" )
public class NotImplementYetExampleTest extends SimpleScenarioTest<NotImplementYetExampleTest.TestSteps> {

    @Test
    @FeatureNotImplementedYet
    // @NotImplementedYet
    public void tests_that_are_not_implemented_yet_can_be_annotated_with_the_NotImplementedYet_annotation() {
        given().some_state();
        when().some_action();
        then().some_result();

        Assertions.assertThat( true ).isFalse();
    }

    @Test
    @FeatureNotImplementedYet
    @NotImplementedYet
    public void a_tests_that_are_not_implemented_yet_can_be_annotated_with_the_NotImplementedYet_annotation() {
        given().some_state();
        when().some_action();
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

    }
}
