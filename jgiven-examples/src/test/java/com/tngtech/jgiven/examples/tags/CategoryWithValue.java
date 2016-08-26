package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

/**
 * Demonstrates that category tags can have values
 */
// tag::noPackage[]
@IsTag( prependType = false )
@Retention( RetentionPolicy.RUNTIME )
public @interface CategoryWithValue {
    String value();
}
//end::noPackage[]