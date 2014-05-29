package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( type = "Feature", value = "NotImplementedYet Annotation",
    description = "tests can be annotated with @NotImplementedYet" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureNotImplementedYet {

}
