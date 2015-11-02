package com.tngtech.jgiven.impl.tag;

import com.tngtech.jgiven.annotation.TagHrefGenerator;
import com.tngtech.jgiven.base.ScenarioTestBase;
import com.tngtech.jgiven.config.TagConfiguration;

import java.lang.annotation.Annotation;

/**
 * Implementation of {@link TagHrefGenerator} that creates
 * an anchor to a specific test.  It expects the {@code value} to be an instanceof ScenarioTestBase
 */
public class GoToTestHrefGenerator implements TagHrefGenerator {
    @Override
    public String generateHref( TagConfiguration tagConfiguration,
                                Annotation annotation, Object value ) {


        if (value instanceof Class && ScenarioTestBase.class.isAssignableFrom( (Class) value )) {
            String toLinkTo = ((Class) value).getName();
            return String.format("#class/%s", toLinkTo );
        }

        throw new IllegalArgumentException("Object value must extend ScenarioTestBase");
    }
}
