package com.tngtech.jgiven.annotation;

import com.tngtech.jgiven.impl.tag.DefaultTagDescriptionGenerator;
import com.tngtech.jgiven.impl.tag.DefaultTagHrefGenerator;

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
     * @see Pending
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
     * @since 0.7.0
     */
    Class<? extends TagDescriptionGenerator> descriptionGenerator() default DefaultTagDescriptionGenerator.class;

    /**
     * @deprecated use {@link #name()} instead
     */
    @Deprecated
    String type() default "";

    /**
     * An optional name that overrides the default which is the name of the annotation.
     * <p>
     * It is possible that multiple annotations have the same type name. However, in this case every 
     * annotation must have a specified value that must be unique. 
     * </p>
     * @since 0.7.4
     */
    String name() default "";

    /**
     * Whether the type should be prepended to the tag if the tag has a value.
     */
    boolean prependType() default false;

    /**
     * Sets a CSS class that should be used in the HTML report for this tag.
     * <p>
     * The default CSS class is {@code 'tag-<name>'} where {@code <name>} is the type of the tag
     * </p>
     * <p>
     * Non-HTML reports ignore this attribute
     * </p>
     *
     * @since 0.7.2
     */
    String cssClass() default "";

    /**
     * A color that should be used in reports for this tag.
     * <p>
     * It depends on the type of the report whether and how this value is interpreted.
     * HTML reports take this value as the <b>background color</b> for the tag.
     * </p>
     * <p>
     * Example values for the HTML report are 'red', '#ff0000', 'rgba(100,0,0,0.5)'
     * </p>
     * <p>
     * This attribute is for simple use cases.
     * For advanced styling options use the {@link #cssClass()} or {@link #style()} attributes instead.
     * </p>
     *
     * @since 0.7.2
     */
    String color() default "";

    /**
     * Defines an inline style that is used in the HTML report for this tag.
     * <p>
     * This is an alternative to the {@link #cssClass()} attribute.
     * </p>
     * For example, to given the tag a white color and a red background-color you could write:
     * <code>style = "background-color: red; color: white;"</code>
     * <p>
     * Non-HTML reports ignore this attribute
     * </p>
     *
     * @since 0.8.0
     */
    String style() default "";

    /**
     * An optional href of the tag that will appear in the generated report.
     *
     * @since 0.9.5
     */
    String href() default "";

    /**
     * An optional href generator that is used to dynamically generate
     * the href depending on the concrete value of an annotation.
     * <p>
     * The class that implements {@link TagHrefGenerator} interface must
     * be a public non-abstract class that is not a non-static inner class and must have a public default constructor.
     * </p>
     * <p>
     * If this attribute is set, the {@link #href()} attribute is ignored.
     * </p>
     *
     * @since 0.9.5
     */
    Class<? extends TagHrefGenerator> hrefGenerator() default DefaultTagHrefGenerator.class;

    /**
     * Whether the tag should be shown in the navigation part of the report
     *
     * @since 0.12.0
     */
    boolean showInNavigation() default true;

}
