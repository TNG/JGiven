package com.tngtech.jgiven.integration.spring.junit5.test.proxy;

public class MessageToTheWorld {
    private String message;

    public MessageToTheWorld(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
