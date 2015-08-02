package com.tngtech.jgiven.tags;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.tngtech.jgiven.annotation.IsTag;

@FeatureCore
@IsTag( name = "Attachments",
    description = "In order to get additional information about a step, like screenshots, for example<br>"
            + "As a JGiven user,<br>"
            + "I want that steps can have attachments" )
@Retention( RetentionPolicy.RUNTIME )
public @interface FeatureAttachments {}
