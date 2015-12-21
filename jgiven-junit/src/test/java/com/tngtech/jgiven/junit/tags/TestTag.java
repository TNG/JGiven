package com.tngtech.jgiven.junit.tags;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag(name = "Security")
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface TestTag {

}
