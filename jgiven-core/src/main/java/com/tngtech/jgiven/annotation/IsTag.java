package com.tngtech.jgiven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an annotation to be used as a tag in JGiven reports.
 * The name and a possible value will be stored.
 * A value can be an array in which case it is either translated into multiple tags, one for each array element,
 * or a comma-separated list of values.
 * <p>
 * <strong>Note that the annotation must have retention policy RUNTIME</strong>
 * <h2>Example</h2>
 * <pre>
 * {@literal @}IsTag
 * {@literal @}Retention( RetentionPolicy.RUNTIME )
 * public {@literal @}interface Issue {
 *  String[] value();
 * }
 * </pre>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.ANNOTATION_TYPE )
public @interface IsTag {
    /**
     * If the annotation has a value and the value is an array, whether or not to explode that array to multiple tags or not.
     * <h2>Example</h2>
     * Take the following tag annotation
     * <pre>
     *   {@literal @}Issue( { "#23", "#12" } )
     * </pre>
     * When {@code explodeArray} is set to {@code true}
     * Then in the report there will be two tags 'Issue-#23' and 'Issue-#12'
     * instead of one tag 'Issue-#23,#12'
     */
    boolean explodeArray() default true;

    /**
     * Whether values should be ignored.
     * If true only a single tag is created for the annotation and the value does not appear in the report.
     * This is useful if the value is used as an internal comment
     * @see NotImplementedYet
     */
    boolean ignoreValue() default false;

    /**
     * An optional default value for the tag.
     */
    String value() default "";

    /**
     * An optional description of the tag that will appear in the generated report.
     */
    String description() default "";

    /**
     * An optional description generator that is used to dynamically generate
     * the description depending on the concrete value of an annotation.
     * <p>
     * The class that implements {@link TagDescriptionGenerator} interface must
     * be a public non-abstract class that is not a non-static inner class and must have a public default constructor.
     * </p>
     * <p>
     * If this attribute is set, the {@link #description()} attribute is ignored.
     * </p>
     * 
     * @since 0.6.3
     */
    Class<? extends TagDescriptionGenerator> descriptionGenerator() default DefaultTagDescriptionGenerator.class;

    /**
     * An optional type description that overrides the default which is the name of the annotation.
     */
    String type() default "";

    /**
     * Whether the type should be prepended to the tag if the tag has a value.
     */
    boolean prependType() default false;
}
