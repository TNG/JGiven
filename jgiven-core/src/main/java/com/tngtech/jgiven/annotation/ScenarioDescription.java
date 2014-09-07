package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Can be used to annotated test methods to give a
 * scenario a complex description that cannot be
 * directly represented by a Java method name.
 *
 * @deprecated just use {@link @Description} instead, will be removed with v0.5
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
@Documented
@Deprecated
public @interface ScenarioDescription {
    String value();
}
