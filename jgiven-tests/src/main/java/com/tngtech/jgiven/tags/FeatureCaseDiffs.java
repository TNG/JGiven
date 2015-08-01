package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Case Diffs",
    description = "In order to get a better overview over structurally different cases of a scenario<br>"
            + "As a human,<br>"
            + "I want the differences highlighted in the generated report" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureCaseDiffs {

}
