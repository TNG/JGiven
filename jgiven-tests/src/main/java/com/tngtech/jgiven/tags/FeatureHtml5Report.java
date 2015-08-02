package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureReport
@IsTag( name = "HTML5 Report",
    description = "In order to have an interactive JGiven report for non-developers<br>"
            + "As a developer,<br>"
            + "I want that JGiven generates HTML5 reports" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureHtml5Report {

}
