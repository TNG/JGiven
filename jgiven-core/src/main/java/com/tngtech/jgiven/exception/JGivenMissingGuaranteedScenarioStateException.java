package com.tngtech.jgiven.exception;

import java.lang.reflect.Field;

public class JGivenMissingGuaranteedScenarioStateException extends RuntimeException {
    public JGivenMissingGuaranteedScenarioStateException(Field field) {
        super("The field " + field.getName() + " is guaranteed but the stage has not initialized it");
    }
}
