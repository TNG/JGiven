package com.tngtech.jgiven.integration.spring.test.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.Hidden;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public class ThenNewInstanceStage {

    public void the_step_instance_is_not_the_same_as_on_previous_run( @Hidden Object previousInstance ) {
        if( previousInstance != null ) {
            assertThat( this ).isNotSameAs( previousInstance );
        }
    }
}
