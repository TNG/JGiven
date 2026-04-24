package com.tngtech.jgiven.integration.spring.junit5.test.proxy;


import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import static org.assertj.core.api.Assertions.assertThat;

@JGivenStage
public class ThenTestStage {

    @ExpectedScenarioState
    private MessageToTheWorld message;

    public void the_message_should_equal_$( String expectedMessage ) {
        assertThat( message.getMessage() ).isEqualTo( expectedMessage );
    }
}
