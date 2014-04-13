package com.tngtech.jgiven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an annotation to be used as a tag in JGiven reports.
 * The name and a possible value will be stored.
 * A value can be an array in which case it is either translated into multiple tags, one for each array element,
 * of a comma-separated list of values.
 * <p>
 * <strong>Note that the annotation must have retention policy RUNTIME</strong>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.ANNOTATION_TYPE )
public @interface IsTag {
    /**
     * If the annotation has a value and the value is an array, whether or not to explode that array to multiple tags or not.
     */
    boolean explodeArray() default true;

    /**
     * Whether values should be ignored. 
     * If true only a single tag is created for the annotation and the value does not appear in the report.  
     */
    boolean ignoreValue() default false;
}
