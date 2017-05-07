package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "@Table Annotation",
    description = "Using the @Table annotation to format arguments as tables" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureTableAnnotation {

}
