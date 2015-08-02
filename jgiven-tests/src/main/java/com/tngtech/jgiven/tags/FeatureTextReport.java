package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureReport
@IsTag( name = "Text Report", description = "Plain text reports can be generated" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTextReport {

}
