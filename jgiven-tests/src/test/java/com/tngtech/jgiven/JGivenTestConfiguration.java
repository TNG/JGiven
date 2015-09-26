package com.tngtech.jgiven;

import com.tngtech.jgiven.config.AbstractJGivenConfiguration;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.tags.IssueDescriptionGenerator;

public class JGivenTestConfiguration extends AbstractJGivenConfiguration {

    @Override
    public void configure() {
        configureTag( Issue.class )
            .prependType( true )
            .color( "orange" )
            .descriptionGenerator( IssueDescriptionGenerator.class );
    }

}
