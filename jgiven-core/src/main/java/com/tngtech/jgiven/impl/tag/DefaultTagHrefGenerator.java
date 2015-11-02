package com.tngtech.jgiven.impl.tag;

import com.tngtech.jgiven.annotation.TagHrefGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

import java.lang.annotation.Annotation;

/**
 * A default implementation of {@link TagHrefGenerator}.
 * It just calls {@code tagConfiguration.getHref()}.
 */
public class DefaultTagHrefGenerator implements TagHrefGenerator {
    @Override
    public String generateHref( TagConfiguration tagConfiguration, Annotation annotation, Object value ) {
        return tagConfiguration.getHref();
    }
}
