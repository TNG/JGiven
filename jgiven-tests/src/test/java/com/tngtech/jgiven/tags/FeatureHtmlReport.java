package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( type = "Feature", value = "Html Report Generation", description = "HTML reports can be generated" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureHtmlReport {

}
