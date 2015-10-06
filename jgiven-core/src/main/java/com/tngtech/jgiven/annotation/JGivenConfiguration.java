package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;

/**
 * This annotation can be applied to a test class to configure:
 * <ul>
 *     <li>Tags</li>
 *     <li>Global formatter</li>
 * </ul>
 */
@Retention( RetentionPolicy.RUNTIME )
@Inherited
@Documented
public @interface JGivenConfiguration {
    Class<? extends AbstractJGivenConfiguration> value();
}
