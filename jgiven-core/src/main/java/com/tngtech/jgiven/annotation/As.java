package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tngtech.jgiven.impl.params.DefaultAsProvider;

/**
 * This annotation can be used to override the default
 * representation for a step method, test method or class name in the report.
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
 *
 * @since 0.7.4. Since 0.9.0 this annotation can be applied to test classes as well
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Documented
public @interface As {

    /**
     * Dummy value to indicate that no value was set
     */
    public static final String NO_VALUE = " - no value - ";

    /**
     * The alternate representation of the step or scenario summary.
     */
    String value() default NO_VALUE;

    /**
     * An optional provider to generate the representation of the stage method, scenario or scenario class.
     * <p>
     * The class that implements {@link AsProvider} interface must
     * be a public non-abstract class that is not a non-static inner class and must have a public default constructor.
     * </p>
     * <p>
     * If this attribute is set, the {@link #value()} attribute is ignored.
     * </p>
     * @since 0.12.0
     */
    Class<? extends AsProvider> provider() default DefaultAsProvider.class;

}
