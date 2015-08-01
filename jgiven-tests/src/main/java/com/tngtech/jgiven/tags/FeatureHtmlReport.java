package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureReport
@IsTag( name = "Static HTML Report",
    description = "In order to show JGiven scenarios to non-developers<br>"
            + "As a developer,<br>"
            + "I want that JGiven generates static HTML reports" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureHtmlReport {

}
