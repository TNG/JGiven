package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Table Step Arguments",
    description = "In order to better present table-like data<br>"
            + "As a human,<br>"
            + "I want a special treatment of table-like data in step arguments" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTableStepArguments {

}
