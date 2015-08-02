package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@Feature
@IsTag( name = "Core Features" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureCore {

}
