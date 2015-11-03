package com.tngtech.jgiven.impl.tag;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.TagHrefGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

/**
 * A default implementation of {@link TagHrefGenerator}.
 * It just calls {@code tagConfiguration.getHref()}.
 *
 * @since 0.9.5
 */
public class DefaultTagHrefGenerator implements TagHrefGenerator {
    @Override
    public String generateHref( TagConfiguration tagConfiguration, Annotation annotation, Object value ) {
        return tagConfiguration.getHref();
    }
}
