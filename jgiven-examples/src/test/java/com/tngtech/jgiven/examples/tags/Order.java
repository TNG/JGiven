package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

/**
 * Used to be able to sort scenarios in the HMTL5 report
 */
@IsTag( cssClass = "hidden" )
@Retention( RetentionPolicy.RUNTIME )
public @interface Order {
    String value();
}
