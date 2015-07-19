package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

/**
 * Defines a tag that 
 */
@ExampleCategory
@CategoryWithValue( "Another Category" )
@IsTag
@Retention( RetentionPolicy.RUNTIME )
public @interface AnotherExampleSubCategory {}
