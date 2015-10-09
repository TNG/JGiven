package com.tngtech.jgiven.impl.tag;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.TagDescriptionGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

/**
 * A default implementation of {@link com.tngtech.jgiven.annotation.TagDescriptionGenerator}.
 * It just calls {@code tagConfiguration.getDescription()}.
 *
 * @since 0.6.3
 */
public class DefaultTagDescriptionGenerator implements TagDescriptionGenerator {
    @Override
    public String generateDescription( TagConfiguration tagConfiguration, Annotation annotation, Object value ) {
        return tagConfiguration.getDescription();
    }
}
