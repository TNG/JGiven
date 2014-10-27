package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( type = "Feature", value = "Html Report Generation",
    description = "In order to show JGiven scenarios to non-developers<br>"
            + "As a developer,<br>"
            + "I want that JGiven generates HTML reports" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureHtmlReport {

}
