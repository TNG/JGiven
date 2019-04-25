package com.tngtech.jgiven.exampleprojects.java11;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;
import com.tngtech.jgiven.exampleprojects.java11.Java11;

public class Java11Test extends SimpleScenarioTest<Java11Test.Steps> {

    @Test
    public void example_scenario() {
        given().some_context();
        when().some_action();
        then().some_outcome();
    }

    public static class Steps {

        public void some_context() {

        }

        public void some_action() {
            Java11.test("f");
        }

        public void some_outcome() {

        }
    }
}