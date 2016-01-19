package com.tngtech.jgiven.example.springboot;

import org.springframework.stereotype.Component;

@Component
public class HelloService {
    public String greeting() {
        return "Greetings from JGiven!";
    }
}
