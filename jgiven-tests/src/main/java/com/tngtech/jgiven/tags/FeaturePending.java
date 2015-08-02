package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Pending Annotation",
    description = "As a good BDD practitioner,<br>"
            + "I want to write my scenarios before I start coding<br>"
            + "In order to discuss them with business stakeholders" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeaturePending {

}
