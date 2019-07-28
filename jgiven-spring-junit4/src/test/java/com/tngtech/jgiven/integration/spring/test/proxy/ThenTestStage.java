package com.tngtech.jgiven.integration.spring.test.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public class ThenTestStage {

    @ExpectedScenarioState
    private MessageToTheWorld message;

    public void the_message_should_equal_$( String expectedMessage ) {
        assertThat( message.getMessage() ).isEqualTo( expectedMessage );
    }
}
