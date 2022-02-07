package com.tngtech.jgiven.example.projects.junit5;

import org.junit.jupiter.api.Tag;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;

public class JGivenTestConfiguration extends AbstractJGivenConfiguration {

    @Override
    public void configure() {
        configureTag( Tag.class )
            .prependType( true )
            .color( "orange" );
    }

}
