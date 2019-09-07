package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "CurrentStep",
    description = "The CurrentStep interface gives access to the currently executing step. It can be injected into a stage using the @ScenarioState annotation." )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureCurrentStep {}
