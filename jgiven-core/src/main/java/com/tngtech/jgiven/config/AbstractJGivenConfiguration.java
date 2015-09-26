package com.tngtech.jgiven.config;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.google.common.collect.Maps;
import com.tngtech.jgiven.format.Formatter;

public abstract class AbstractJGivenConfiguration {
    private final Map<Class<? extends Annotation>, TagConfiguration> tagConfigurations = Maps.newHashMap();
    private final Map<Class<?>, Formatter<?>> formatter = Maps.newHashMap();

    /**
     * Configures the given annotation as a tag.
     * 
     * This is useful if you want to treat annotations as tags in JGiven that you cannot or want not
     * to be annotated with the {@link com.tngtech.jgiven.annotation.IsTag} annotation.
     * 
     * @param tagAnnotation the tag to be configured
     * @return a configuration builder for configuring the tag
     */
    public final TagConfiguration.Builder configureTag( Class<? extends Annotation> tagAnnotation ) {
        TagConfiguration configuration = new TagConfiguration( tagAnnotation );
        tagConfigurations.put( tagAnnotation, configuration );
        return new TagConfiguration.Builder( configuration );
    }

    public abstract void configure();

    public TagConfiguration getTagConfiguration( Class<? extends Annotation> annotationType ) {
        return tagConfigurations.get( annotationType );
    }

    /**
     * Sets the formatter for the given type.
     * 
     * Note that the formatter is exactly set for the given type. It will <strong>not</strong> be
     * applied to subtypes of that type.
     * 
     * Also note that the formatter can still be overridden by using a formatting annotation.
     * 
     * @param typeToBeFormatted the type for which the formatter should be defined
     * @param formatter the formatter to format instances of that type
     */
    public <T> void setFormatter( Class<T> typeToBeFormatted, Formatter<T> formatter ) {
        this.formatter.put( typeToBeFormatted, formatter );
    }

    public <T> Formatter<T> getFormatter( Class<T> typeToBeFormatted ) {
        return (Formatter<T>) formatter.get( typeToBeFormatted );
    }

}