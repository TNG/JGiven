package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Tags Scenarios with Story numbers
 */
@Retention( RetentionPolicy.RUNTIME )
public @interface Issue {
    String[] value();
}
