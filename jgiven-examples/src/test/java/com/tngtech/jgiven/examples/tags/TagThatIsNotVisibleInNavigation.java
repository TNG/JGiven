package com.tngtech.jgiven.examples.tags;

import com.tngtech.jgiven.annotation.IsTag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Used to be able to sort scenarios in the HMTL5 report
 */
@IsTag( showInNavigation = false )
@Retention( RetentionPolicy.RUNTIME )
public @interface TagThatIsNotVisibleInNavigation {
}
