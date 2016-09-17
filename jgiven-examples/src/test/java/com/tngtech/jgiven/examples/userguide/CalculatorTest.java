package com.tngtech.jgiven.examples.userguide;

// tag::noPackage[]

import org.junit.Test;

import com.tngtech.jgiven.junit.SimpleScenarioTest;

public class CalculatorTest extends SimpleScenarioTest<WhenCalculator> {

    @Test
    public void test() {
        when().$_percent_are_added( 10 );
    }

}
// end::noPackage[]
