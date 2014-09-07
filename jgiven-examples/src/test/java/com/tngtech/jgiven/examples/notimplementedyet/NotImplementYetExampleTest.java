package com.tngtech.jgiven.examples.notimplementedyet;

import org.junit.Test;

import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.annotation.NotImplementedYet;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@Description( "As a good BDD practitioner,<br>"
        + "I want to write my scenarios before I start coding<br>"
        + "In order to discuss them with business stakeholders" )
public class NotImplementYetExampleTest extends SimpleScenarioTest<NotImplementYetExampleTest.TestSteps> {

    @Test
    @NotImplementedYet
    public void tests_that_are_not_implemented_yet_can_be_annotated_with_the_NotImplementedYet_annotation() {
        given().some_state();
        when().some_action();
        then().some_result();
    }

    public static class TestSteps {

        public void some_state() {}

        public void some_result() {}

        public void some_action() {}

    }
}
