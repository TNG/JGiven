package com.tngtech.jgiven.tags;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.TagDescriptionGenerator;
import com.tngtech.jgiven.config.TagConfiguration;

public class IssueDescriptionGenerator implements TagDescriptionGenerator {
    private static final String ISSUE_URL = "https://github.com/TNG/JGiven/issues/";

    @Override
    public String generateDescription( TagConfiguration tagConfiguration, Annotation annotation, Object value ) {
        String valueAsString = String.valueOf( value );
        return String.format( "Scenarios of <a href='%s%s'>Issue %s</a>", ISSUE_URL, valueAsString.substring( 1 ), valueAsString );
    }
}
