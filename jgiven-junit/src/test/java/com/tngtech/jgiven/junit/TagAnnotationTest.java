package com.tngtech.jgiven.junit;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.Test;

import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.junit.test.GivenTaggedTestStep;
import com.tngtech.jgiven.junit.test.GivenTestStep;
import com.tngtech.jgiven.junit.test.ThenTestStep;
import com.tngtech.jgiven.junit.test.WhenTestStep;

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

    @Test
    public void tag_on_step_method_is_recognized() throws Throwable {
        given().a_tagged_step_method();

        getScenario().finished();

        assertThat( getScenario().getModel().getTagMap().keySet() ).contains( "StepMethodTag" );
    }

    @Test
    public void tag_on_stage_class_is_recognized() throws Throwable {
        GivenTaggedTestStep givenTaggedTestStep = addStage( GivenTaggedTestStep.class );
        givenTaggedTestStep.some_step_method_in_a_tagged_stage();

        getScenario().finished();

        assertThat( getScenario().getModel().getTagMap().keySet() ).contains( "StageTag" );
    }
}
