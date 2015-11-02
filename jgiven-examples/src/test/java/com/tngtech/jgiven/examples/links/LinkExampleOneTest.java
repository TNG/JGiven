package com.tngtech.jgiven.examples.links;


import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

public class LinkExampleOneTest extends SimpleScenarioTest<LinkExampleOneTest.Steps> {

    @SimpleLink
    @Test
    public void link_to_fixed_location() {
        given().test_annotated_with_links();
        when().the_report_is_generated();
        then().the_link_appears_in_the_report();
    }

    @LinkToTest(LinkExampleTwoTest.class)
    @Test
    public void link_to_another_test() {
        given().test_linked_to_another_test();
        when().the_report_is_generated();
        then().the_link_appears_in_the_report();
    }

    public static class Steps {
        void test_annotated_with_links() {
        }

        void the_report_is_generated() {
        }

        void the_link_appears_in_the_report() {
        }

        void test_linked_to_another_test() {
        }

    }
}