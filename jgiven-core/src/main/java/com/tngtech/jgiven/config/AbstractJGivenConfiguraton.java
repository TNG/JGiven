package com.tngtech.jgiven.config;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.google.common.collect.Maps;

public abstract class AbstractJGivenConfiguraton {
    private final Map<Class<? extends Annotation>, TagConfiguration> tagConfigurations = Maps.newHashMap();

    public final TagConfiguration.Builder configureTag( Class<? extends Annotation> tagAnnotation ) {
        TagConfiguration configuration = new TagConfiguration( tagAnnotation );
        tagConfigurations.put( tagAnnotation, configuration );
        return new TagConfiguration.Builder( configuration );
    }

    public abstract void configure();

    public TagConfiguration getTagConfiguration( Class<? extends Annotation> annotationType ) {
        return tagConfigurations.get( annotationType );
    }

}