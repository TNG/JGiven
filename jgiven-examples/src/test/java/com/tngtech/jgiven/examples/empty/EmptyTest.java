package com.tngtech.jgiven.examples.empty;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.examples.simpletestcase.SimpleScenarioTestExampleTest;
import com.tngtech.jgiven.junit.SimpleScenarioTest;

/**
 * This test contains no real JGiven scenario.
 * It is used to show the effect --exclude-empty-scenarios option, namely that the
 * resulting report will not include this class if the option is set to true
 */
public class EmptyTest extends SimpleScenarioTest<SimpleScenarioTestExampleTest.TestSteps> {

    @Test
    public void someNonScenario() {
        assertThat( true ).isTrue();
    }
}
