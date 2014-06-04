package com.tngtech.jgiven;

import com.tngtech.jgiven.config.AbstractJGivenConfiguraton;
import com.tngtech.jgiven.tags.Issue;

public class JGivenTestConfiguration extends AbstractJGivenConfiguraton {

    @Override
    public void configure() {
        configureTag( Issue.class )
            .prependType( true )
            .description( "Issue numbers correspond to Issues in GitHub" );
    }

}
