package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Derived Parameters",
    description = "In order to not have to specify easily derivable parameters explicitly<br>"
            + "As a developer,<br>"
            + "I want that step arguments derived from parameters appear in a data table" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureDerivedParameters {

}
