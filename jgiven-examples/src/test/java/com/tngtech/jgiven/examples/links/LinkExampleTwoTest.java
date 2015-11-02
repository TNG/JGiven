package com.tngtech.jgiven.examples.links;


import com.tngtech.jgiven.junit.SimpleScenarioTest;
import org.junit.Test;

public class LinkExampleTwoTest extends SimpleScenarioTest<LinkExampleOneTest.Steps> {

    @LinkToTest(LinkExampleOneTest.class)
    @Test
    public void link_to_another_test() {
        given().test_linked_to_another_test();
        when().the_report_is_generated();
        then().the_link_appears_in_the_report();
    }

}