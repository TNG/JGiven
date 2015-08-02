package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

/**
 * Defines a tag that is used as a category
 */
@IsTag
@Retention( RetentionPolicy.RUNTIME )
public @interface ExampleCategory {}
