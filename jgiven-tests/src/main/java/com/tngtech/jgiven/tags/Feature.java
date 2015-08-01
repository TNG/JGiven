package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@IsTag( name = "Features" )
@Retention( RetentionPolicy.RUNTIME )
public @interface Feature {

}
