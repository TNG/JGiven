package com.tngtech.jgiven.impl.tag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.tngtech.jgiven.ScenarioRuleTest;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;

public class GoToTestHrefGeneratorTest {

    private GoToTestHrefGenerator goToTestHrefGenerator = new GoToTestHrefGenerator();

    @Test
    public void generatesAnchorHrefForTestClass() throws Exception {
        String generatedHref = goToTestHrefGenerator.generateHref( null, null, ScenarioRuleTest.class );
        assertThat( generatedHref, is( "#class/com.tngtech.jgiven.ScenarioRuleTest" ) );
    }

    @Test( expected = JGivenWrongUsageException.class )
    public void doesNotWorkWithClassesThatDoNotExtendScenarioTestBase() throws Exception {
        goToTestHrefGenerator.generateHref( null, null, "some non-class value" );
    }
}