package com.tngtech.jgiven.integration.spring.test.proxy;

import org.springframework.stereotype.Service;

@Service
public class HelloWorldService {
    public MessageToTheWorld sayHello() {
        return new MessageToTheWorld("Hello");
    }

    public MessageToTheWorld appendWorld(MessageToTheWorld messageToTheWorld) {
        return new MessageToTheWorld(messageToTheWorld.getMessage() + " World!");
    }
}
