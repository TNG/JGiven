package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

/**
 * Tags Scenarios with Story numbers
 */
@IsTag( prependType = true, description = "Issue numbers correspond to Issues in GitHub" )
@Retention( RetentionPolicy.RUNTIME )
public @interface Issue {
    String[] value();
}
