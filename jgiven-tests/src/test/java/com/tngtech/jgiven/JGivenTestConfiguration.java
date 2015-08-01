package com.tngtech.jgiven;

import com.tngtech.jgiven.config.AbstractJGivenConfiguraton;
import com.tngtech.jgiven.tags.Issue;
import com.tngtech.jgiven.tags.IssueDescriptionGenerator;

public class JGivenTestConfiguration extends AbstractJGivenConfiguraton {

    @Override
    public void configure() {
        configureTag( Issue.class )
            .prependType( true )
            .color( "orange" )
            .descriptionGenerator( IssueDescriptionGenerator.class );
    }

}
