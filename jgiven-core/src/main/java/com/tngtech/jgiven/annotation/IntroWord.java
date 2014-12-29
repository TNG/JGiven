package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as an introduction word of a step.
 * Examples for introduction words are given, when, then, and, etc.
 * Introduction words are specially treated in reports, e.g., introduce a new line,
 * or are specially aligned. 
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface IntroWord {

}
