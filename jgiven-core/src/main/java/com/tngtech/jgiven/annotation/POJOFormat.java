package com.tngtech.jgiven.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.format.POJOAnnotationFormatter;

/**
 * A special format annotation to format complex types.
 */
@Documented
@AnnotationFormat( value = POJOAnnotationFormatter.class )
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.PARAMETER, ElementType.ANNOTATION_TYPE } )
public @interface POJOFormat {
    /**
     * Specifies which fields should be excluded in the report.
     * <p>
     * If {@link #includeFields()} is set, then this attribute has no effect
     * 
     * <p>
     */
    String[] excludeFields() default {};

    /**
     * Specifies which fields should be included in the report.
     * 
     * All fields not in this list will be excluded.
     * <p>
     */
    String[] includeFields() default {};

    /**
     * Whether or not columns with only {@code null} values are shown or not.
     * Default is to not show them.
     * 
     */
    boolean includeNullColumns() default false;

    /**
     * When set to <code>true</code>, each formatted field value is prefixed by its field name
     */
    boolean prefixWithFieldName() default false;

    /**
     * Specify a field separator
     */
    String fieldSeparator() default ",";

    /**
     * Specify a custom {@link NamedFormatSet} annotation
     * 
     * <p>
     * The {@link NamedFormat} defined in this set will be used when formatting
     * POJOs fields.<br>
     * </p>
     * 
     */
    Class<? extends Annotation> fieldsFormatSetAnnotation() default Annotation.class;

    /**
     * Specify an array of {@link NamedFormat} to use when formatting POJOs
     * fields.
     * <p>
     * When a {@link NamedFormat#name()} matches a field name, field value is
     * formatted using this {@link NamedFormat}.
     * </p>
     * 
     * <p>
     * Note : when set, has precedence over {@link #fieldsFormatSetAnnotation()}
     * </p>
     * 
     * @See {@link #fieldFormatSet()}
     */
    NamedFormat[] fieldsFormat() default {};

}
