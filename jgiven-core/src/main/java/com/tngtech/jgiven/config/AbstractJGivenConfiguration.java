package com.tngtech.jgiven.config;

import java.lang.annotation.Annotation;
import java.util.Map;

import com.google.common.collect.Maps;
import com.tngtech.jgiven.format.Formatter;
import com.tngtech.jgiven.impl.format.FormatterCache;

public abstract class AbstractJGivenConfiguration implements FormatterConfiguration {
    private final Map<Class<? extends Annotation>, TagConfiguration> tagConfigurations = Maps.newHashMap();
    private final FormatterCache formatterCache = new FormatterCache();
    private String testClassSuffixRegEx = "Tests?";

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
     * <p>
     * When choosing a formatter, JGiven will take the formatter defined for the most specific
     * super type of a given type.
     * <p>
     * If no formatter can be found for a type, the {@link com.tngtech.jgiven.format.DefaultFormatter} is taken.
     * <p>
     * For example,
     * given the following formatter are defined:
     *
     * <pre>
     * setFormatter( Object.class, formatterA );
     * setFormatter( String.class, formatterB );
     * </pre>
     *
     * When formatting a String,<br>
     * Then {@code formatterB} will be taken.
     * <p>
     * If formatter for multiple super types of a type are defined, but these types have no subtype relation, then an arbitrary
     * formatter is taken in a non-deterministic way. Thus you should avoid this situation.
     * <p>
     * For example,
     * given the following formatter are defined:
     *
     * <pre>
     * setFormatter( Cloneable.class, formatterA );
     * setFormatter( Serializable.class, formatterB );
     * </pre>
     *
     * When formatting a String,<br>
     * Then either {@code formatterA} or {@code formatterB} will be taken non-deterministically.
     * <p>
     * The order in which the formatter are defined, does not make a difference.    
     * <p>    
     * Note that the formatter can still be overridden by using a formatting annotation.
     *
     * @param typeToBeFormatted the type for which the formatter should be defined
     * @param formatter the formatter to format instances of that type
     */
    public <T> void setFormatter( Class<T> typeToBeFormatted, Formatter<T> formatter ) {
        formatterCache.setFormatter( typeToBeFormatted, formatter );
    }

    /**
     * Set a regular expression for a test class suffix that should be removed by JGiven in the report.
     * <p>
     * By default the regular expression is {@code Tests?}
     *
     * @param suffixRegEx a regular expression that match the suffix
     */
    public void setTestClassSuffixRegEx( String suffixRegEx ) {
        testClassSuffixRegEx = suffixRegEx;
    }

    @Override
    public Formatter<?> getFormatter( final Class<?> typeToBeFormatted ) {
        return formatterCache.getFormatter( typeToBeFormatted );
    }

    public String getTestClassSuffixRegEx() {
        return testClassSuffixRegEx;
    }
}