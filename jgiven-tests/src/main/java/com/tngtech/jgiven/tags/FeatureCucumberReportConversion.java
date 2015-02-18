package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( type = "Feature", value = "Cucumber Report Conversion",
    description = "In order have a smooth migration path from Cucumber to JGiven<br>"
            + "As a developer,<br>"
            + "I want that Cucumber reports can be converted into JGiven reports" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureCucumberReportConversion {}
