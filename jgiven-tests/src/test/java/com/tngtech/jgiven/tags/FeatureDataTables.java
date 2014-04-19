package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( type = "Feature", value = "Data Tables", description = "Data tables can be generated in reports" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureDataTables {

}
