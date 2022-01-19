package com.tngtech.jgiven.junit5.test;

import com.tngtech.jgiven.annotation.JGivenConfiguration;
import com.tngtech.jgiven.config.ConfigurationUtil;
import com.tngtech.jgiven.config.TagConfiguration;
import com.tngtech.jgiven.junit5.JGivenExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith( JGivenExtension.class )
@JGivenConfiguration( CustomJUnit5TagConfiguration.class )
public class JUnit5ExtensionWithJGivenConfigurationTest {

    @Test
    @Tag( "test-tag" )
    public void JUnit5_tags_are_converted_to_JGiven_tags_using_custom_configuration() {
        TagConfiguration configuration = ConfigurationUtil.getConfiguration( JUnit5ExtensionWithJGivenConfigurationTest.class )
                .getTagConfiguration( Tag.class );

        Assertions.assertNotNull( configuration );
        Assertions.assertEquals( "custom name", configuration.getName() );
        Assertions.assertEquals( "custom description", configuration.getDescription() );
        Assertions.assertEquals( "blue", configuration.getColor() );
    }
}
