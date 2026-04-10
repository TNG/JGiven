package com.tngtech.jgiven.junit5.test.configuration;

import com.tngtech.jgiven.junit5.JGivenExtension;
import com.tngtech.jgiven.junit5.SimpleScenarioTest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JGivenExtension.class)
class JUnit5DefaultTagConfigurationTest extends SimpleScenarioTest<TagConfigurationStage> {

    @Test
    @Tag("test-tag")
    void JUnit5_tags_are_converted_to_JGiven_tags_using_default_configuration() {
        given().no_explicit_configuration();
        when().the_configuration_is_queried();
        then().the_tag_configuration_has_the_color("orange")
            .and().the_tag_configuration_has_the_name("")
            .and().the_tag_configuration_has_the_description("JUnit 5 Tag");
    }
}
