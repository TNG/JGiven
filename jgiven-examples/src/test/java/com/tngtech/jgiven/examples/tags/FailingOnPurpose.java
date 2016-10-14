package com.tngtech.jgiven.examples.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag(description = "Scenarios that fail on purpose, to demonstrate how errors appear in JGiven")
@Retention( RetentionPolicy.RUNTIME )
public @interface FailingOnPurpose {
}
