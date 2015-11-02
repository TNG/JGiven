package com.tngtech.jgiven.impl.tag;

import com.tngtech.jgiven.ScenarioRuleTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;


public class GoToTestHrefGeneratorTest {

    private GoToTestHrefGenerator goToTestHrefGenerator = new GoToTestHrefGenerator();

    @Test
    public void generatesAnchorHrefForTestClass() throws Exception {
        String generatedHref = goToTestHrefGenerator.generateHref(null, null, ScenarioRuleTest.class);
        assertThat(generatedHref, is("#class/com.tngtech.jgiven.ScenarioRuleTest"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void doesNotWorkWithClassesThatDoNotExtendScenarioTestBase() throws Exception {
        Class classThatIsNotOfExpectedType = this.getClass();
        goToTestHrefGenerator.generateHref(null, null, classThatIsNotOfExpectedType);
    }
}