package com.tngtech.jgiven.annotation;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.config.TagConfiguration;

/**
 * Is used as an attribute of the {@link IsTag} annotation
 * to dynamically generate an href for an annotation depending on its value.
 * <p>
 *     Implementations of this interface must be a public non-abstract class that is not a non-static inner class
 *     and must have a public default constructor.
 * </p>
 *
 * @since 0.9.5
 */
public interface TagHrefGenerator {

    /**
     * Implement this method to generate the href for the given annotation and its value.
     * <p>
     *     Note that when the value of the annotation is an array and {@link IsTag#explodeArray()}
     *     is {@code true}, then this method is called for each value of the array and not once for the whole array.
     *     Otherwise it is called only once.
     * </p>
     * @param tagConfiguration the configuration of the tag. The values typically correspond to the {@link IsTag annotation}.
     *                         However, it is also possible to configure annotations to be tags using {@link JGivenConfiguration},
     *                         in which case there is no {@link IsTag} annotation.
     * @param annotation the actual annotation that was used as a tag. Note that this can be {@code null} in the case of
     *                   dynamically added tags.
     * @param value the value of the annotation. If the annotation has no value the default value is passed ({@link IsTag#value()}
     *
     * @return the description of the annotation for the given value
     */
    String generateHref( TagConfiguration tagConfiguration, Annotation annotation, Object value );
}
