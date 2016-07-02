package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static org.assertj.core.api.Assertions.assertThat;

public class TagAnnotationTest extends ScenarioTest<GivenTestStep, WhenTestStep, ThenTestStep> {

    @IsTag( showInNavigation = false )
    @Retention( RetentionPolicy.RUNTIME )
    @interface TagNotShownInNavigation {}


    @TagNotShownInNavigation
    @Test
    public void shownInNavigation_is_correctly_evaluated() throws Throwable {
        given().some_boolean_value( true );

        getScenario().finished();

        assertThat( getScenario().getModel().getTagMap().entrySet().iterator().next().getValue().getShownInNavigation() )
                .isEqualTo( false );
    }
}
