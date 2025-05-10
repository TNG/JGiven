package com.tngtech.jgiven.exampleprojects.java17;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;
import com.tngtech.jgiven.exampleprojects.java17.Java17;

public class Java17Test extends SimpleScenarioTest<Java17Test.Steps> {

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
            Java17.test("f");
        }

        public void some_outcome() {

        }
    }
}