package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to define an alternate
 * description for a step method or test method.
 * This description is then used in the generated report
 * instead of using the method name.
 * <p>
 * Note that the '$' character keeps its special meaning and will be
 * replaced with step arguments
 *
 * <pre>
 * {@code
 * @Description("some (complicated) step")
 * public SELF some_complicated_step() {
 *    ...
 * }
 * }
 * </pre>
 *
 * <pre>
 * {@code
 * @Description("Some 'special' scenario description")
 * @Test
 * public void some_special_scenario_description() {
 *    ...
 * }
 * }
 * </pre>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Documented
public @interface Description {

    /**
     * The description of the step.
     */
    String value();

}
