package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.annotation.Pending;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.config.TagConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.tngtech.jgiven.annotation.ScenarioStage;
import com.tngtech.jgiven.junit5.JGivenExtension;

@ExtendWith( JGivenExtension.class )
public class JUnit5ExtensionTest {

    @ScenarioStage
    GivenStage givenStage;

    @ScenarioStage
    WhenStage whenStage;

    @ScenarioStage
    ThenStage thenStage;

    @Test
    public void JGiven_works_with_JUnit5() {
        givenStage.some_state();
        whenStage.some_action();
        thenStage.some_outcome();
    }

    @Test
    @Pending
    public void Pending_works() {
        whenStage.some_failing_step();
    }

    @Test
    @Tag( "test-tag" )
    public void JUnit5_tags_are_converted_to_JGiven_tags_using_default_configuration() {
        TagConfiguration configuration = ConfigurationUtil.getConfiguration( JUnit5ExtensionTest.class )
                .getTagConfiguration( Tag.class );

        Assertions.assertNotNull( configuration );
        Assertions.assertEquals( "JUnit 5 Tag", configuration.getDescription() );
        Assertions.assertEquals( "orange", configuration.getColor() );
    }
}
