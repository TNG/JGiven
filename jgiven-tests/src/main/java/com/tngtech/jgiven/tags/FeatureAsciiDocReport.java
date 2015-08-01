package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureReport
@IsTag( name = "AsciiDoc Report",
    description = "In order to easily combine hand-written documentation with JGiven scenarios<br>"
            + "As a developer,<br>"
            + "I want that JGiven generates AsciiDoc reports" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureAsciiDocReport {

}
