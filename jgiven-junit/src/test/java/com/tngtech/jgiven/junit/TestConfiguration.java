package com.tngtech.jgiven.junit;

import com.tngtech.jgiven.config.AbstractJGivenConfiguraton;
import com.tngtech.jgiven.junit.tags.ConfiguredTag;

public class TestConfiguration extends AbstractJGivenConfiguraton {

    @Override
    public void configure() {
        configureTag( ConfiguredTag.class )
            .defaultValue( "Test" )
            .description( "Test Description" );
    }

}
