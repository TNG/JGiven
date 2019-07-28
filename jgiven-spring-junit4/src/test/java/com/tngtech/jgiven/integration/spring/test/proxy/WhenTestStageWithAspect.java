package com.tngtech.jgiven.integration.spring.test.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

/**
 * This is referred in {@link TestAspect}
 */
@JGivenStage
public class WhenTestStageWithAspect {

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
