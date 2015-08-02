package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( value = "Tags", description = "Scenarios can be tagged with annotations" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTags {

}
