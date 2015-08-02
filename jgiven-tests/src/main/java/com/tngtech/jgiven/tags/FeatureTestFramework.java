package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@Feature
@IsTag( name = "Supported Test Frameworks",
    description = "JGiven can be used together with JUnit and TestNG" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTestFramework {

}
