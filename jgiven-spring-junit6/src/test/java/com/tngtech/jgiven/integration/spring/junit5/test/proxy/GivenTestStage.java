package com.tngtech.jgiven.integration.spring.junit5.test.proxy;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public class GivenTestStage {

    @Autowired
    private HelloWorldService helloWorldService;

    @ProvidedScenarioState
    private MessageToTheWorld message;

    public GivenTestStage should_say_hello() {
        assertThat(helloWorldService).isNotNull();

        message = helloWorldService.sayHello();
        return this;
    }
}
