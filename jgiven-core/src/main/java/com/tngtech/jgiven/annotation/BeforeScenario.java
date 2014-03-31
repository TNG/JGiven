package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods to be executed before the whole Scenario has been executed.
 * Essentially means that the method is executed before the first call of
 * either given(), when(), or then().
 * <p>
 * It is guaranteed that the method is only invoked once
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface BeforeScenario {

}
