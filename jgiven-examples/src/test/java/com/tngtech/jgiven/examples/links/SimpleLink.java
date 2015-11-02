package com.tngtech.jgiven.examples.links;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;



@IsTag(href="http://www.example.org", description = "Go to example.org")
@Retention( RetentionPolicy.RUNTIME )
public @interface SimpleLink {

}