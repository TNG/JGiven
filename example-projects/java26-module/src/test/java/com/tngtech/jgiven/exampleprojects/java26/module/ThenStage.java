package com.tngtech.jgiven.exampleprojects.java21.module;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ScenarioState;

/**
 * Public stage that consumes the scenario state produced by
 * {@link PackagePrivateStage}.
 *
 * <p>The {@code @ScenarioState} fields here are package-private on purpose:
 * JGiven must {@link java.lang.reflect.Field#setAccessible(boolean) set them
 * accessible} from the JGiven module, which under JPMS requires the
 * {@code opens} directive in {@code module-info.java}.</p>
 */
public class ThenStage extends Stage<ThenStage> {

    @ScenarioState(required = true)
    String result;

    public void the_result_is(String expected) {
        org.junit.jupiter.api.Assertions.assertEquals(expected, result);
    }
}
