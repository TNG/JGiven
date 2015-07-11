package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

/**
 * This annotation can be used to override the default
 * representation for a step method or test method in the report.
 * 
 * <p>
 * Note that the '$' character keeps its special meaning and will be
 * replaced with step arguments
 *
 * <pre>
 * {@literal @}As("some (complicated) step")
 * public SELF some_complicated_step() {
 *    ...
 * }
 * </pre>
 *
 * <pre>
 * {@literal @}As("Some 'special' scenario description")
 * {@literal @}Test
 * public void some_special_scenario_description() {
 *    ...
 * }
 * </pre>
 * 
 * @since 0.7.4
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Documented
public @interface As {

    /**
     * The alternate representation of the step or scenario summary.
     */
    String value();

}
