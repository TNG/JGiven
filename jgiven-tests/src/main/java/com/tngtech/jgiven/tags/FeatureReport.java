package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@Feature
@IsTag( name = "Reporting",
    description = "JGiven can generate text and HTML reports. An AsciiDoc report is currently under development." )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureReport {

}
