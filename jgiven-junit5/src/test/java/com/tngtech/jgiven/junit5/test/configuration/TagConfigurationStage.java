package com.tngtech.jgiven.junit5.test.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.config.TagConfiguration;
import org.junit.jupiter.api.Tag;

class TagConfigurationStage extends Stage<TagConfigurationStage> {

    private Class<?> configurationTarget;
    private TagConfiguration configuration;

    TagConfigurationStage no_explicit_configuration() {
        return this;
    }

    TagConfigurationStage the_configuration_defined_on(Class<?> target) {
        configurationTarget = target;
        return this;
    }

    TagConfigurationStage the_configuration_is_queried() {
        configuration = ConfigurationUtil
            .getConfiguration(configurationTarget)
            .getTagConfiguration(Tag.class);
        return this;
    }

    TagConfigurationStage the_tag_configuration_has_the_color(String color) {
        configurationIsNotNull();
        assertThat(configuration.getColor()).isEqualTo(color);
        return this;
    }

    TagConfigurationStage the_tag_configuration_has_the_description(String description) {
        configurationIsNotNull();
        assertThat(configuration.getDescription()).isEqualTo(description);
        return this;
    }

    TagConfigurationStage the_tag_configuration_has_the_name(String name) {
        configurationIsNotNull();
        assertThat(configuration.getName()).isEqualTo(name);
        return this;
    }

    private void configurationIsNotNull() {
        if (configuration == null) {
            throw new IllegalStateException("Tag configuration was never set");
        }
    }

}
