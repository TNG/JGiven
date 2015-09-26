package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;

@Retention( RetentionPolicy.RUNTIME )
@Inherited
@Documented
public @interface JGivenConfiguration {
    Class<? extends AbstractJGivenConfiguration> value();
}
