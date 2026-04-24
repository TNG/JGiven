package com.tngtech.jgiven.junit6.test.configuration;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import org.junit.jupiter.api.Tag;

public class CustomJUnit6TagConfiguration extends AbstractJGivenConfiguration {

    @Override
    public void configure() {
        configureTag(Tag.class)
            .name("custom name")
            .color("blue")
            .description("custom description");
    }
}
