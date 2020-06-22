package com.tngtech.jgiven.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method which will be used as a preamble to the next non-filler stage method.
 * In reports, filler words inherit the alignment of the stage method that follows.
 * Examples of filler words that may be used are the, some, for_a.
 * domain specific filler words can be created for specific use cases.
 */
@Documented
@Retention( RetentionPolicy.RUNTIME )
@Target( ElementType.METHOD )
public @interface FillerWord {

    /**
     * In reports, filler words marked with joinToPreviousWord will be suffixed to the previous word
     * in the sentence without whitespace.
     */
    boolean joinToPreviousWord() default false;

    /**
     * In reports, filler words marked with joinToNextWord will be prefixed to the next word
     * in the sentence without whitespace.
     */
    boolean joinToNextWord() default false;

}
