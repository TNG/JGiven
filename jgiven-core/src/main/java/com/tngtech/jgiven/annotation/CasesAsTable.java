package com.tngtech.jgiven.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Test methods or test classes can be annotated with this
 * annotation to indicate that the generated report should generate
 * a data table instead of multiple scenario cases.
 * 
 * @deprecated since v0.2.2, data tables are the default, so this annotation has no effect anymore
 */
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Retention( RetentionPolicy.RUNTIME )
@Deprecated
public @interface CasesAsTable {

}
