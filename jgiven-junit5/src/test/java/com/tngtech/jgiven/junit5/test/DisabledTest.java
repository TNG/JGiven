package com.tngtech.jgiven.junit5.test;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;

public class DisabledTest extends SimpleScenarioTest<DisabledTest.ExtensibleReproStage> {
    @Test
    @Disabled
    void test1() {
        given().something();
    }

    @Test
    void test2() {
        // given() returns null when following test1
        given().something();
    }

    @Test
    @Disabled
    void test3() {
        given().something();
    }

    @Test
    void test4() {
        // given() returns null when following test1
        given().something();
    }


    public static class ExtensibleReproStage<SELF extends ExtensibleReproStage<SELF>> extends Stage<SELF> {
        public SELF something() {
            return self();
        }
    }
}
