package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureTestFramework
@IsTag( name = "TestNG",
    description = "Tests can be be executed with TestNG" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTestNg {

}
