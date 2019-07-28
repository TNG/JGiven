package com.tngtech.jgiven.integration.spring.junit5.test.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public class WhenTestStage {

    @Autowired
    private HelloWorldService helloWorldService;

    @ExpectedScenarioState
    private MessageToTheWorld message;

    public void appendWorld_is_called() {
        assertThat(helloWorldService).isNotNull();
        assertThat(message).isNotNull();

        message = helloWorldService.appendWorld(message);
    }

}
