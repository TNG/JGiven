package com.tngtech.jgiven.tests;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag
@Retention( RetentionPolicy.RUNTIME )
public @interface TestTag {
    String value();
}
