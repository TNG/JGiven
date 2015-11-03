package com.tngtech.jgiven.impl.tag;

import java.lang.annotation.Annotation;

import com.tngtech.jgiven.annotation.TagHrefGenerator;
import com.tngtech.jgiven.config.TagConfiguration;
import com.tngtech.jgiven.exception.JGivenWrongUsageException;

/**
 * Implementation of {@link TagHrefGenerator} that creates
 * an anchor to a specific test.  It expects the {@code value} to be an instanceof Class
 *
 * @since 0.9.5
 */
public class GoToTestHrefGenerator implements TagHrefGenerator {
    @Override
    public String generateHref( TagConfiguration tagConfiguration,
            Annotation annotation, Object value ) {

        if( value instanceof Class ) {
            String toLinkTo = ( (Class) value ).getName();
            return String.format( "#class/%s", toLinkTo );
        }

        throw new JGivenWrongUsageException( "Values of the GoToTestHrefGenerator must be classes" );
    }
}
