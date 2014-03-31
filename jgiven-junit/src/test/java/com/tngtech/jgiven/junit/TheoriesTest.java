package com.tngtech.jgiven.junit;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import com.tngtech.jgiven.junit.ScenarioTest;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

@RunWith( Theories.class )
public class TheoriesTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {
    @DataPoints
    public static Integer[] someIntegers = { 1, 2, 3 };

    @Theory
    public void someTest( Integer param ) {
        given().some_integer_value( param );
        when().multiply_with_two();
        then().the_result_is( 2 * param );

    }
}
