package com.tngtech.jgiven.examples.datatable.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.POJOFormat;

// @formatter:off
@POJOFormat(
    fieldFormatsAnnotation = AddressFormat.class,
    includeFields = {"zipCode", "city", "country"},
    fieldSeparator = "/"
)
// @formatter:on
@Retention( RetentionPolicy.RUNTIME )
public @interface AddressReducedPOJOFormat {}
