package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

/**
 * Tags Scenarios with Story numbers
 */
@Retention( RetentionPolicy.RUNTIME )
@IsTag
public @interface Story {
    String[] value();
}
