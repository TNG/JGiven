package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods to be executed before a stage is executed, 
 * i.e. before the first step is executed.
 * <p>
 * In particular, all values are injected at this point.
 * <p>
 * It is guaranteed that the method is only invoked once.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface BeforeStage {

}
