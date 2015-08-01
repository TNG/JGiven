package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Data Tables",
    description = "In order to get a better overview over the different cases of a scenario<br>"
            + "As a human,<br>"
            + "I want to have different cases represented as a data table" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureDataTables {

}
