package com.tngtech.jgiven.annotation;

import java.lang.annotation.*;

/**
 * Methods of stages classes annotated with this annotation will not be intercepted by JGiven. 
 * <p>
 * This is useful for technical helper methods that have to be called even if a previous
 * step method has failed. Normally, JGiven will skip all step methods following a failed step, 
 * including methods with the {@link com.tngtech.jgiven.annotation.Hidden} annotation.
 * <p>
 * Note that methods annotated with this annotation will not appear in the report.
 * <p>
 * You should write technical helper methods in camelCase so that the name already indicates that
 * the method does not appear in the report.
 * <p>
 * @see com.tngtech.jgiven.annotation.Hidden
 * @since 0.8.2
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface DoNotIntercept {

}
