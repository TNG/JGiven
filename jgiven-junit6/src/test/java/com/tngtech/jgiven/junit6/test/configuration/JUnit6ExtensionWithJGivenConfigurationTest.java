package com.tngtech.jgiven.junit6.test.configuration;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.junit6.JGivenExtension;
import com.tngtech.jgiven.junit6.SimpleScenarioTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JGivenExtension.class)
@JGivenConfiguration(CustomJUnit6TagConfiguration.class)
public class JUnit6ExtensionWithJGivenConfigurationTest
    extends SimpleScenarioTest<TagConfigurationStage> {

    @Test
    @Tag("test-tag")
    public void JUnit6_tags_are_converted_to_JGiven_tags_using_custom_configuration() {
        given().the_configuration_defined_on(getClass());
        when().the_configuration_is_queried();
        then().the_tag_configuration_has_the_name("custom name")
            .and().the_tag_configuration_has_the_description("custom description")
            .and().the_tag_configuration_has_the_color("blue");
    }

}
