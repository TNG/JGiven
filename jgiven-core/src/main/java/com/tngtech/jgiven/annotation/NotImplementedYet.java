package com.tngtech.jgiven.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks methods of step definitions as not implemented yet.
 * Such steps will not be executed, but will appear in 
 * the report as an not implemented yet. 
 * <p>
 * This is useful if one already wants to define the scenario without
 * already implementing all steps, for example, to verify that
 * all acceptance criteria of a story are covered by the scenario.
 * <p>
 * Can also annotated the overall step definition class to indicate
 * that no step is implemented yet.
 * <p>
 * Finally, a test method can be annotated to indicate that the whole
 * test is not implemented yet. The test will then be ignored by the testing-framework.
 * <i>Currently only works for JUnit</i>
 * 
 * <h2>Example</h2>
 * <pre>
 * @NotImplementedYet
 * public void my_cool_new_feature() {
 *   
 * }
 * </pre>
 *
 */
@Documented
@Inherited
@Retention( RUNTIME )
@Target( { METHOD, TYPE } )
@IsTag( ignoreValue = true )
public @interface NotImplementedYet {
    /**
     * Optional description to describe when the implementation will be done
     */
    String value() default "";
}
