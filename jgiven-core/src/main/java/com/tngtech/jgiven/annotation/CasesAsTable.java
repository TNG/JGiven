package com.tngtech.jgiven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test methods or test classes can be annotated with this
 * annotation to indicate that the generated report should generate
 * a data table instead of multiple scenario cases.
 * <p>
 * <b>This is an EXPERIMENTAL feature.</b>
 */
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Retention( RetentionPolicy.RUNTIME )
public @interface CasesAsTable {

}
