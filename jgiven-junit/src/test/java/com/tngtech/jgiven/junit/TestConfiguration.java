package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.junit.tags.ConfiguredTag;

public class TestConfiguration extends AbstractJGivenConfiguration {

    @Override
    public void configure() {
        configureTag( ConfiguredTag.class )
            .defaultValue( "Test" )
            .description( "Test Description" );
    }

}
