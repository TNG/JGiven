package com.tngtech.jgiven.tests;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.tngtech.jgiven.junit.ScenarioTest;

@RunWith( Parameterized.class )
public class TestClassWithParameterizedRunner extends ScenarioTest<GivenTestStage, WhenTestStage, ThenTestStage> {

    @Parameters
    public static Iterable<Object[]> data() {
        return Arrays.asList( new Object[][] {
            { 1 },
            { 2 }
        } );
    }

    private final Integer value;

    public TestClassWithParameterizedRunner( Integer value ) {
        this.value = value;
    }

    @Test
    public void test1() {
        given().nothing();
    }

    @Test
    public void test2() {
        given().nothing();
    }
}
