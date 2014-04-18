package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to define an alternate
 * description for a step method.
 * This description is then used in the generated report
 * instead of using the method name.
 * 
 * <pre>
 * @Description("some (complicated) step")
 * public SELF some_complicated_step() {
 *    ...
 * }
 * </pre>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
@Documented
public @interface Description {

    /**
     * The description of the step
     */
    String value();

}
