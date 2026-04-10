package com.tngtech.jgiven.integration.spring.junit6.test.proxy;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

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
