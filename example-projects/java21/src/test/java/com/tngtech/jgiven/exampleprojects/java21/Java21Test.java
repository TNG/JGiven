package com.tngtech.jgiven.exampleprojects.java21;

import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;
import com.tngtech.jgiven.exampleprojects.java21.Java21;

public class Java21Test extends SimpleScenarioTest<Java21Test.Steps> {

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
            Java21.test("f");
        }

        public void some_outcome() {

        }
    }
}
