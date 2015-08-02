package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Duration", description = "The duration of steps, cases, and scenarios is measured and reported" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureDuration {

}
