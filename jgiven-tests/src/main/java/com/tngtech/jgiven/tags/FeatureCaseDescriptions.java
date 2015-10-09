package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Case Descriptions",
    description = "Cases of parametrized scenarios can have custom descriptions by using the @CaseDescription annotation" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureCaseDescriptions {

}
