package com.tngtech.jgiven.tags;

import com.tngtech.jgiven.annotation.IsTag;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@FeatureTestFramework
@IsTag( name = "JUnit5",
    description = "Tests can be be executed with JUnit5" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureJUnit5 {

}
