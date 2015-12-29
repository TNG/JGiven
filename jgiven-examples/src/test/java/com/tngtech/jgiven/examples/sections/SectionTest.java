package com.tngtech.jgiven.examples.sections;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.Description;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

@Description( "Large scenarios can be structured by sections." )
public class SectionTest extends SimpleScenarioTest<SectionTest.TestSteps> {

    @Test
    public void scenarios_can_have_sections() {
        section( "The first section" );

        given().something();
        when().something();
        then().something();

        section( "The second section" );

        when().something_else();
        then().something_else();

    }

    public static class TestSteps extends Stage<TestSteps> {
        public TestSteps something() {
            return self();
        }

        public TestSteps something_else() {
            return self();
        }

        public void something_fails() {
            assertThat( true ).as( "fails for demonstration purposes" ).isFalse();
        }
    }

}
