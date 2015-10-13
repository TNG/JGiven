package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

/**
 * This annotation can be used to provide a description for a test class.
 * 
 * <h2>Deprecated usage</h2>
 * The {@code @Description} annotation can also be used to 
 * change the representation of a step method or test method.
 * This value is then used in the generated report
 * instead of using the method name.
 * Note that this usage of this annotation is deprecated and you should use
 * the {@link com.tngtech.jgiven.annotation.As} annotation instead.
 * 
 * @see com.tngtech.jgiven.annotation.As
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD, ElementType.TYPE } )
@Documented
public @interface Description {

    /**
     * The description of the test class.
     */
    String value();

}
