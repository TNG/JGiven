package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Spanish Scenarios", description = "Scenarios can be written in Spanish" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureSpanish {

}
