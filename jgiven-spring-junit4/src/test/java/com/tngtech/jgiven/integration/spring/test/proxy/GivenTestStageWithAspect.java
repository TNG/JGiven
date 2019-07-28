package com.tngtech.jgiven.integration.spring.test.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

/**
 * This is referred in {@link TestAspect}
 */
@JGivenStage
public class GivenTestStageWithAspect {

    @Autowired
    private HelloWorldService helloWorldService;

    @ProvidedScenarioState
    private MessageToTheWorld message;

    public void should_say_hello() {
        assertThat(helloWorldService).isNotNull();

        message = helloWorldService.sayHello();
    }
}
