package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag(
    description = "Tests with this tag use a browser for testing",
    color = "red" )
@Retention( RetentionPolicy.RUNTIME )
public @interface BrowserTest {

}
