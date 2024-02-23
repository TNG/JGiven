package com.tngtech.jgiven.impl.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tngtech.jgiven.ScenarioRuleTest;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;
import org.junit.Test;

public class GoToTestHrefGeneratorTest {

    private final GoToTestHrefGenerator goToTestHrefGenerator = new GoToTestHrefGenerator();

    @Test
    public void generatesAnchorHrefForTestClass() {
        String generatedHref = goToTestHrefGenerator.generateHref( null, null, ScenarioRuleTest.class );
        assertThat( generatedHref ).isEqualTo( "#class/com.tngtech.jgiven.ScenarioRuleTest" );
    }

    @Test
    public void doesNotWorkWithClassesThatDoNotExtendScenarioTestBase() {
        assertThatThrownBy(() ->goToTestHrefGenerator.generateHref( null, null, "some non-class value" ))
            .isInstanceOf(JGivenWrongUsageException.class);
    }
}