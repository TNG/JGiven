package com.tngtech.jgiven.junit5.test;

import org.junit.jupiter.api.Assertions;

public class GeneralStage {

    private String someState;

    public void some_state() {
        someState = "SomeState";

    }

    public void some_action() {
        Assertions.assertNotNull(someState);
    }

    public void some_outcome() {
        Assertions.assertNotNull(someState);
    }
}
