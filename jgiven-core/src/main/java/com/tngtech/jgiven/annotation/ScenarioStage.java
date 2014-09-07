package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks fields to be stage instances
 * This is useful to reuse stages within stages.
 * It can also be used to inject stages into test classes,
 * for example, when more than three step definition classes are needed
 *
 * <pre>
 * {@literal @}ScenarioStage
 * MyStage stage;
 * </pre>
 *
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.FIELD )
public @interface ScenarioStage {

}
