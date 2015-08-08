package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;
import com.tngtech.jgiven.tags.FeatureCore;

/**
 * Defines a tag that 
 */
@FeatureCore
@IsTag( style = "background-color: darkgreen; color: white; font-weight: bold",
    description = "Tags can be arbitrarily styled with the 'style' attribute of the '@IsTag' annotation. " +
            "This tag shows how to apply such a custom style" )
@Retention( RetentionPolicy.RUNTIME )
public @interface TagsWithCustomStyle {}
